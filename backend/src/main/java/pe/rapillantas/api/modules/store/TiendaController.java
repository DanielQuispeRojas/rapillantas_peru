package pe.rapillantas.api.modules.store;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

// Tienda del sitio: ofertas propias + overlay por SKU del catalogo de MOODU
// (imagenes, compatibilidades). El catalogo (productos/servicios) vive en
// MOODU y se consume por /pub desde el frontend (ver lib/moodu.ts).
@RestController
public class TiendaController {
  private final OfertaRepository ofertas;
  private final DescuentoService descuentos;
  private final pe.rapillantas.api.modules.inventory.ProductoCompatibilidadRepository compat;
  private final pe.rapillantas.api.modules.inventory.ProductoImagenRepository imagenes;
  private final pe.rapillantas.api.modules.loyalty.PuntosSkuRepository puntos;

  public TiendaController(OfertaRepository ofertas, DescuentoService descuentos,
                          pe.rapillantas.api.modules.inventory.ProductoCompatibilidadRepository compat,
                          pe.rapillantas.api.modules.inventory.ProductoImagenRepository imagenes,
                          pe.rapillantas.api.modules.loyalty.PuntosSkuRepository puntos) {
    this.ofertas = ofertas;
    this.descuentos = descuentos;
    this.compat = compat;
    this.imagenes = imagenes;
    this.puntos = puntos;
  }

  // Puntos por SKU (publico): la tienda muestra ★ pts sin login.
  @GetMapping("/api/v1/puntos")
  public Object puntos() {
    var m = new java.util.LinkedHashMap<String, Integer>();
    for (var p : puntos.findAll()) m.put(p.sku, Math.max(0, p.puntos));
    return m;
  }

  @GetMapping("/api/v1/ofertas")
  public Object ofertas() {    var hoy = LocalDate.now();
    return ofertas.findAll().stream()
        .filter(o -> o.activa && !o.vigenciaDesde.isAfter(hoy)
            && (o.vigenciaHasta == null || !o.vigenciaHasta.isBefore(hoy)))
        .toList();
  }

  // Overlay por SKU (publico): fotos y compatibilidades que el sitio cuelga
  // al SKU de MOODU. Sin SKU registrado => lista vacia (el item igual se ve).
  @GetMapping("/api/v1/sku/{sku}/imagenes")
  public Object imagenesPorSku(@PathVariable String sku) {
    if (sku == null || sku.isBlank()) return List.of();
    return imagenes.findBySkuOrderByOrdenAsc(sku.trim().toUpperCase()).stream().map(i -> i.url).toList();
  }

  @GetMapping("/api/v1/sku/{sku}/compatibilidad")
  public Object compatPorSku(@PathVariable String sku) {
    if (sku == null || sku.isBlank()) return List.of();
    return compat.findBySku(sku.trim().toUpperCase());
  }

  @GetMapping("/api/v1/compatibilidad/buscar")
  public Object compatPorVehiculo(@RequestParam(required = false) String marca,
                                  @RequestParam(required = false) String modelo) {
    if ((marca == null || marca.isBlank()) && (modelo == null || modelo.isBlank())) return List.of();
    return compat.findByMarcaIgnoreCaseAndModeloIgnoreCase(
        marca != null ? marca : "", modelo != null ? modelo : "");
  }

  // Admin - publicar ofertas (promos con descripci�n larga; sku MOODU opcional)
  @PostMapping("/api/v1/admin/ofertas")
  public Object crearOferta(@RequestBody Oferta o) {
    if (o.titulo == null || o.titulo.isBlank()) throw new IllegalArgumentException("t�tulo obligatorio");
    if (o.sku != null && !o.sku.isBlank()) o.sku = o.sku.trim().toUpperCase();
    validarVigencia(o);
    return ofertas.save(o);
  }

  @PutMapping("/api/v1/admin/ofertas/{id}")
  public Object editarOferta(@PathVariable java.util.UUID id, @RequestBody Oferta cambios) {
    var o = ofertas.findById(id).orElseThrow(() -> new IllegalArgumentException("oferta no existe"));
    if (cambios.titulo != null && !cambios.titulo.isBlank()) o.titulo = cambios.titulo;
    if (cambios.descripcion != null) o.descripcion = cambios.descripcion;
    if (cambios.precioOferta != null) o.precioOferta = cambios.precioOferta;
    if (cambios.vigenciaDesde != null) o.vigenciaDesde = cambios.vigenciaDesde;
    o.vigenciaHasta = cambios.vigenciaHasta;
    if (cambios.imagenUrl != null) o.imagenUrl = cambios.imagenUrl.isBlank() ? null : cambios.imagenUrl;
    if (cambios.imagenUrl2 != null) o.imagenUrl2 = cambios.imagenUrl2.isBlank() ? null : cambios.imagenUrl2;
    if (cambios.imagenUrl3 != null) o.imagenUrl3 = cambios.imagenUrl3.isBlank() ? null : cambios.imagenUrl3;
    if (cambios.sku != null) o.sku = cambios.sku.isBlank() ? null : cambios.sku.trim().toUpperCase();
    if (cambios.servicioSku != null) o.servicioSku = cambios.servicioSku.isBlank() ? null : cambios.servicioSku.trim().toUpperCase();
    o.activa = cambios.activa;
    validarVigencia(o);
    return ofertas.save(o);
  }

  @DeleteMapping("/api/v1/admin/ofertas/{id}")
  public void eliminarOferta(@PathVariable java.util.UUID id) {
    ofertas.deleteById(id);
  }

  private void validarVigencia(Oferta o) {
    var hoy = LocalDate.now();
    if (o.vigenciaDesde == null) o.vigenciaDesde = hoy;
    if (o.vigenciaDesde.isBefore(hoy)) throw new IllegalArgumentException("inicio no puede ser menor a hoy");
    if (o.vigenciaHasta != null && !o.vigenciaHasta.isAfter(o.vigenciaDesde))
      throw new IllegalArgumentException("fin debe ser mayor al inicio");
  }

  /** Sube foto desde el dispositivo (m�ximo 3 por oferta): ocupa el primer slot libre. */
  @PostMapping(value = "/api/v1/admin/ofertas/{id}/imagen", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
  public Object subirImagenOferta(@PathVariable java.util.UUID id,
                                  @RequestParam("archivo") org.springframework.web.multipart.MultipartFile archivo) throws java.io.IOException {
    var o = ofertas.findById(id).orElseThrow(() -> new IllegalArgumentException("oferta no existe"));
    if (archivo == null || archivo.isEmpty()) throw new IllegalArgumentException("archivo vac�o");
    if (archivo.getSize() > 5 * 1024 * 1024) throw new IllegalArgumentException("m�ximo 5MB por foto");
    String tipo = archivo.getContentType();
    if (tipo == null || !tipo.startsWith("image/")) throw new IllegalArgumentException("solo se admiten im�genes");
    String ext = ".jpg";
    String original = archivo.getOriginalFilename();
    if (original != null && original.contains(".")) {
      String e = original.substring(original.lastIndexOf('.')).toLowerCase();
      if (List.of(".jpg", ".jpeg", ".png", ".webp").contains(e)) ext = e.equals(".jpeg") ? ".jpg" : e;
    }
    java.nio.file.Path dir = java.nio.file.Paths.get("uploads", "ofertas", id.toString());
    java.nio.file.Files.createDirectories(dir);
    String nombre = java.util.UUID.randomUUID() + ext;
    try (var in = archivo.getInputStream()) {
      java.nio.file.Files.copy(in, dir.resolve(nombre), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }
    String url = "/uploads/ofertas/" + id + "/" + nombre;
    if (o.imagenUrl == null || o.imagenUrl.isBlank()) o.imagenUrl = url;
    else if (o.imagenUrl2 == null || o.imagenUrl2.isBlank()) o.imagenUrl2 = url;
    else if (o.imagenUrl3 == null || o.imagenUrl3.isBlank()) o.imagenUrl3 = url;
    else throw new IllegalStateException("m�ximo 3 im�genes por oferta");
    ofertas.save(o);
    return Map.of("url", url);
  }

  @GetMapping("/api/v1/admin/ofertas")
  public List<Oferta> listarOfertas() {
    return ofertas.findAll();
  }
}
