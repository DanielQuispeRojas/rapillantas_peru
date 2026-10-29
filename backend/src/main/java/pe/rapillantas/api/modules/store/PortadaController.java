package pe.rapillantas.api.modules.store;

import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class PortadaController {
  private final PortadaConfigRepository repo;
  private final PortadaSlideRepository slides;
  private final SliderRepository sliders;
  private final String portadaDir;

  public PortadaController(PortadaConfigRepository repo, PortadaSlideRepository slides,
                           SliderRepository sliders,
                           @Value("${app.portada-dir:../vue/public/portada}") String portadaDir) {
    this.repo = repo;
    this.slides = slides;
    this.sliders = sliders;
    this.portadaDir = portadaDir;
  }

  private PortadaConfig actual() {
    return repo.findAll().stream().findFirst().orElseGet(() -> repo.save(new PortadaConfig()));
  }

  /** Público: slides activos del slider indicado (?clave=portada) para incrustar el bloque. */
  @GetMapping("/api/v1/portada")
  public java.util.List<PortadaSlide> portada(@RequestParam(defaultValue = "portada") String clave) {
    var s = sliders.findByClave(clave).orElse(null);
    java.util.List<PortadaSlide> lista = s != null
        ? slides.findBySliderIdAndActivoOrderByOrdenAsc(s.id, true)
        : slides.findByActivoOrderByOrdenAsc(true);
    if (!lista.isEmpty()) return lista;
    // compat: si no hay slides, expone la config única como slide
    var p = actual();
    var uno = new PortadaSlide();
    uno.imagenUrl = p.imagenUrl; uno.titulo = p.titulo; uno.subtitulo = p.subtitulo; uno.mensajeWsp = p.mensajeWsp;
    return java.util.List.of(uno);
  }

  @GetMapping("/api/v1/admin/sliders")
  public java.util.List<Slider> listarSliders() {
    var todos = sliders.findAll();
    if (todos.isEmpty()) {
      var p = new Slider(); p.clave = "portada"; p.nombre = "Portada inicio"; sliders.save(p);
      var l = new Slider(); l.clave = "login"; l.nombre = "Inicio de sesión"; sliders.save(l);
      todos = sliders.findAll();
    }
    return todos;
  }

  @PostMapping("/api/v1/admin/sliders")
  @Transactional
  public Slider crearSlider(@RequestBody Slider s) {
    // Clave automática desde el nombre (slug único)
    String base = java.text.Normalizer.normalize(s.nombre.trim().toLowerCase(), java.text.Normalizer.Form.NFD)
        .replaceAll("[^a-z0-9 ]", "").trim().replaceAll("\\s+", "-");
    if (base.isBlank()) base = "slider";
    String clave = base;
    int i = 2;
    while (sliders.findByClave(clave).isPresent()) clave = base + "-" + (i++);
    s.clave = clave;
    return sliders.save(s);
  }

  @DeleteMapping("/api/v1/admin/sliders/{id}")
  @Transactional
  public void eliminarSlider(@PathVariable UUID id) {
    sliders.deleteById(id);
  }

  @GetMapping("/api/v1/admin/portada/slides")
  public java.util.List<PortadaSlide> listarSlides(@RequestParam(required = false) UUID sliderId) {
    var todos = sliderId != null ? slides.findBySliderIdOrderByOrdenAsc(sliderId) : slides.findAll();
    return todos.stream().sorted((a, b) -> Integer.compare(a.orden, b.orden)).toList();
  }

  @PostMapping("/api/v1/admin/portada/slides")
  @Transactional
  public PortadaSlide crearSlide(@RequestBody PortadaSlide s) {
    if (s.sliderId == null) {
      var p = sliders.findByClave("portada").orElseGet(() -> {
        var n = new Slider(); n.clave = "portada"; n.nombre = "Portada inicio";
        return sliders.save(n);
      });
      s.sliderId = p.id;
    }
    s.orden = slides.findBySliderIdOrderByOrdenAsc(s.sliderId).size();
    return slides.save(s);
  }

  @PutMapping("/api/v1/admin/portada/slides/{id}")
  @Transactional
  public PortadaSlide actualizarSlide(@PathVariable UUID id, @RequestBody PortadaSlide s) {
    var a = slides.findById(id).orElseThrow(() -> new IllegalArgumentException("slide no existe"));
    a.imagenUrl = s.imagenUrl; a.titulo = s.titulo; a.subtitulo = s.subtitulo;
    a.mensajeWsp = s.mensajeWsp; a.activo = s.activo;
    return slides.save(a);
  }

  @DeleteMapping("/api/v1/admin/portada/slides/{id}")
  @Transactional
  public void eliminarSlide(@PathVariable UUID id) {
    slides.deleteById(id);
  }

  @PutMapping("/api/v1/admin/portada/slides/reordenar")
  @Transactional
  public void reordenar(@RequestBody Map<String, java.util.List<UUID>> body) {
    var orden = body.get("orden");
    for (int i = 0; i < orden.size(); i++) {
      var s = slides.findById(orden.get(i)).orElseThrow();
      s.orden = i;
      slides.save(s);
    }
  }

  /** Admin: configura flyer, título y mensaje de WhatsApp. */
  @PutMapping("/api/v1/admin/portada")
  public PortadaConfig guardar(@RequestBody Map<String, String> b) {
    var p = actual();
    if (b.get("imagenUrl") != null) p.imagenUrl = b.get("imagenUrl");
    if (b.get("titulo") != null) p.titulo = b.get("titulo");
    if (b.get("subtitulo") != null) p.subtitulo = b.get("subtitulo");
    if (b.get("mensajeWsp") != null) p.mensajeWsp = b.get("mensajeWsp");
    p.updatedAt = java.time.OffsetDateTime.now();
    return repo.save(p);
  }

  /** Admin: sube el flyer desde el dispositivo (jpg/png/webp, máx 5MB).
   * Se guarda en vue/public/portada/ y se sirve directo como /portada/.... */
  @PostMapping(value = "/api/v1/admin/portada/imagen", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
  public Map<String, String> subir(@RequestParam("archivo") MultipartFile archivo) throws java.io.IOException {
    if (archivo == null || archivo.isEmpty()) throw new IllegalArgumentException("archivo vacío");
    if (archivo.getSize() > 5 * 1024 * 1024) throw new IllegalArgumentException("máximo 5MB");
    String tipo = archivo.getContentType();
    if (tipo == null || !tipo.startsWith("image/")) throw new IllegalArgumentException("solo imágenes");
    var dir = java.nio.file.Paths.get(portadaDir);
    java.nio.file.Files.createDirectories(dir);
    String ext = ".jpg";
    String original = archivo.getOriginalFilename();
    if (original != null && original.contains(".")) {
      String e = original.substring(original.lastIndexOf('.')).toLowerCase();
      if (java.util.List.of(".jpg", ".jpeg", ".png", ".webp").contains(e)) ext = e.equals(".jpeg") ? ".jpg" : e;
    }
    String nombre = UUID.randomUUID() + ext;
    try (var in = archivo.getInputStream()) {
      java.nio.file.Files.copy(in, dir.resolve(nombre), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }
    var p = actual();
    p.imagenUrl = "/portada/" + nombre;
    p.updatedAt = java.time.OffsetDateTime.now();
    repo.save(p);
    return Map.of("imagenUrl", p.imagenUrl);
  }
}
