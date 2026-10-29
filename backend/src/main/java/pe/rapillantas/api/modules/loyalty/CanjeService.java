package pe.rapillantas.api.modules.loyalty;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.rapillantas.api.modules.customers.ClienteRepository;

@Service
public class CanjeService {
  private final CanjeableRepository canjeables;
  private final CanjeRepository canjes;
  private final ClienteRepository clientes;
  private final PuntoMovimientoRepository puntosMov;

  public CanjeService(CanjeableRepository canjeables, CanjeRepository canjes,
                      ClienteRepository clientes, PuntoMovimientoRepository puntosMov) {
    this.canjeables = canjeables;
    this.canjes = canjes;
    this.clientes = clientes;
    this.puntosMov = puntosMov;
  }

  /** Vitrina pública: publicados, activos y con tope disponible (NULL = ilimitado). */
  public List<Canjeable> publicos() {
    return canjeables.findAll().stream()
        .filter(c -> c.publicado && c.activo && (c.stock == null || c.stock > 0))
        .sorted((a, b) -> Integer.compare(a.puntosCosto, b.puntosCosto)).toList();
  }

  public List<Canjeable> todos() {
    return canjeables.findAll().stream()
        .sorted((a, b) -> b.createdAt.compareTo(a.createdAt)).toList();
  }

  @Transactional
  public Canjeable guardar(Canjeable c) {
    if (c.nombre == null || c.nombre.isBlank()) throw new IllegalArgumentException("nombre obligatorio");
    if (c.puntosCosto < 0) throw new IllegalArgumentException("puntos no pueden ser negativos");
    if (c.stock != null && c.stock < 0) throw new IllegalArgumentException("tope no puede ser negativo");
    // Si está activo se publica, si no, no.
    c.publicado = c.activo;
    return canjeables.save(c);
  }

  @Transactional
  public Canjeable editar(UUID id, Canjeable cambios) {
    var c = canjeables.findById(id).orElseThrow(() -> new IllegalArgumentException("canjeable no existe"));
    if (cambios.nombre != null && !cambios.nombre.isBlank()) c.nombre = cambios.nombre.trim();
    if (cambios.descripcion != null) c.descripcion = cambios.descripcion;
    if (cambios.puntosCosto >= 0) c.puntosCosto = cambios.puntosCosto;
    // stock NULL = ilimitado (el formulario admin siempre manda número o null).
    c.stock = cambios.stock;
    if (c.stock != null && c.stock < 0) throw new IllegalArgumentException("tope no puede ser negativo");
    if (cambios.imagenUrl != null) c.imagenUrl = cambios.imagenUrl.isBlank() ? null : cambios.imagenUrl;
    if (cambios.imagenUrl2 != null) c.imagenUrl2 = cambios.imagenUrl2.isBlank() ? null : cambios.imagenUrl2;
    if (cambios.imagenUrl3 != null) c.imagenUrl3 = cambios.imagenUrl3.isBlank() ? null : cambios.imagenUrl3;
    c.activo = cambios.activo;
    // Si está activo se publica, si no, no.
    c.publicado = c.activo;
    return canjeables.save(c);
  }

  @Transactional
  public void eliminar(UUID id) {
    canjeables.deleteById(id);
  }

  /** Cliente canjea: descuenta puntos y stock; queda por_recoger 3 días hasta el despacho. */
  @Transactional
  public Canje canjear(UUID clienteId, UUID canjeableId) {
    var cli = clientes.findById(clienteId).orElseThrow(() -> new IllegalArgumentException("cliente no existe"));
    var c = canjeables.findById(canjeableId).orElseThrow(() -> new IllegalArgumentException("canjeable no existe"));
    if (!c.publicado || !c.activo) throw new IllegalStateException("canjeable no disponible");
    if (c.stock <= 0) throw new IllegalStateException("canjeable agotado");
    if (cli.puntos < c.puntosCosto)
      throw new IllegalStateException("puntos insuficientes: tienes " + cli.puntos + " y cuesta " + c.puntosCosto);
    // Tope de oferta: solo aplica si tiene tope (NULL = ilimitado).
    if (c.stock != null && c.stock <= 0) throw new IllegalStateException("canjeable agotado");
    cli.puntos -= c.puntosCosto;
    clientes.save(cli);
    if (c.stock != null) {
      c.stock -= 1;
      canjeables.save(c);
    }
    var cj = new Canje();
    cj.clienteId = cli.id; cj.canjeableId = c.id;
    cj.canjeableNombre = c.nombre; cj.puntosUsados = c.puntosCosto;
    cj.estado = "por_recoger";
    cj.expiraAt = OffsetDateTime.now().plusDays(3);
    canjes.save(cj);
    var m = new PuntoMovimiento();
    m.clienteId = cli.id; m.tipo = "canje"; m.puntos = -c.puntosCosto; m.refCanje = cj.id;
    puntosMov.save(m);
    return cj;
  }

  public List<Canje> mios(UUID clienteId) {
    expirarVencidos();
    return canjes.findByClienteIdOrderByCreatedAtDesc(clienteId);
  }

  public List<Canje> porEstado(String estado) {
    expirarVencidos();
    var todos = canjes.findAll().stream()
        .sorted((a, b) -> b.createdAt.compareTo(a.createdAt)).toList();
    if (estado == null || estado.isBlank()) return todos;
    return todos.stream().filter(c -> estado.equals(c.estado)).toList();
  }

  /** Administración confirma el despacho: el cliente lo recoge en tienda. */
  @Transactional
  public Canje despachar(UUID id) {
    var cj = canjes.findById(id).orElseThrow(() -> new IllegalArgumentException("canje no existe"));
    if (!"por_recoger".equals(cj.estado)) throw new IllegalStateException("solo se despacha un canje por recoger");
    cj.estado = "despachado";
    cj.despachadoAt = OffsetDateTime.now();
    return canjes.save(cj);
  }

  /** Administración cancela: devuelve puntos y stock. */
  @Transactional
  public Canje cancelar(UUID id) {
    return anularConDevolucion(id, "cancelado");
  }

  /** Administración rechaza un por_recoger (sin stock / oferta caída): devuelve puntos y stock. */
  @Transactional
  public Canje rechazar(UUID id) {
    return anularConDevolucion(id, "rechazado", null);
  }

  /**
   * Vencimiento automático: todo por_recoger con más de 3 días se expira,
   * se devuelven los puntos y el stock, con nota visible al cliente.
   * Se ejecuta cada hora por scheduler y también antes de cada lectura (mios/porEstado).
   */
  @Transactional
  public int expirarVencidos() {
    var ahora = OffsetDateTime.now();
    var vencidos = canjes.findAll().stream()
        .filter(c -> "por_recoger".equals(c.estado)
            && c.expiraAt != null && !c.expiraAt.isAfter(ahora))
        .toList();
    for (var c : vencidos) {
      anularConDevolucion(c.id, "expirado",
          "No viniste a reclamar tu canje, te devolvimos tus puntos para cuando quieras reclamar otro ítem.");
    }
    return vencidos.size();
  }

  @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 * * * *")
  @Transactional
  public void expirarVencidosProgramado() {
    expirarVencidos();
  }

  private Canje anularConDevolucion(UUID id, String estado) {
    return anularConDevolucion(id, estado, null);
  }

  private Canje anularConDevolucion(UUID id, String estado, String nota) {
    var cj = canjes.findById(id).orElseThrow(() -> new IllegalArgumentException("canje no existe"));
    if (!"por_recoger".equals(cj.estado)) throw new IllegalStateException("solo se anula un canje por recoger");
    cj.estado = estado;
    if (nota != null) cj.motivoNota = nota;
    canjes.save(cj);
    var cli = clientes.findById(cj.clienteId).orElse(null);
    if (cli != null) {
      cli.puntos += cj.puntosUsados;
      clientes.save(cli);
      var m = new PuntoMovimiento();
      m.clienteId = cli.id; m.tipo = "acumulacion"; m.puntos = cj.puntosUsados; m.refCanje = cj.id;
      puntosMov.save(m);
    }
    if (cj.canjeableId != null) {
      canjeables.findById(cj.canjeableId).ifPresent(c -> {
        // Solo se devuelve el tope si lo tiene (NULL = ilimitado, nada que devolver).
        if (c.stock != null) {
          c.stock += 1;
          canjeables.save(c);
        }
      });
    }
    return cj;
  }

  /** Sube foto desde el dispositivo (máximo 3 por canjeable): ocupa el primer slot libre. */
  @Transactional
  public java.util.Map<String, String> subirImagen(UUID id, org.springframework.web.multipart.MultipartFile archivo) throws java.io.IOException {
    var c = canjeables.findById(id).orElseThrow(() -> new IllegalArgumentException("canjeable no existe"));
    if (archivo == null || archivo.isEmpty()) throw new IllegalArgumentException("archivo vacío");
    if (archivo.getSize() > 5 * 1024 * 1024) throw new IllegalArgumentException("máximo 5MB por foto");
    String tipo = archivo.getContentType();
    if (tipo == null || !tipo.startsWith("image/")) throw new IllegalArgumentException("solo se admiten imágenes");
    String ext = ".jpg";
    String original = archivo.getOriginalFilename();
    if (original != null && original.contains(".")) {
      String e = original.substring(original.lastIndexOf('.')).toLowerCase();
      if (List.of(".jpg", ".jpeg", ".png", ".webp").contains(e)) ext = e.equals(".jpeg") ? ".jpg" : e;
    }
    java.nio.file.Path dir = java.nio.file.Paths.get("uploads", "canjeables", id.toString());
    java.nio.file.Files.createDirectories(dir);
    String nombre = java.util.UUID.randomUUID() + ext;
    try (var in = archivo.getInputStream()) {
      java.nio.file.Files.copy(in, dir.resolve(nombre), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }
    String url = "/uploads/canjeables/" + id + "/" + nombre;
    if (c.imagenUrl == null || c.imagenUrl.isBlank()) c.imagenUrl = url;
    else if (c.imagenUrl2 == null || c.imagenUrl2.isBlank()) c.imagenUrl2 = url;
    else if (c.imagenUrl3 == null || c.imagenUrl3.isBlank()) c.imagenUrl3 = url;
    else throw new IllegalStateException("máximo 3 imágenes por canjeable");
    canjeables.save(c);
    return java.util.Map.of("url", url);
  }
}
