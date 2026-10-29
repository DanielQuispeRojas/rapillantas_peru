package pe.rapillantas.api.modules.customers;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class ValoracionController {
  private final ValoracionRepository repo;
  private final ClienteRepository clientes;

  public ValoracionController(ValoracionRepository repo, ClienteRepository clientes) {
    this.repo = repo;
    this.clientes = clientes;
  }

  private Cliente clienteActual() {
    String login = SecurityContextHolder.getContext().getAuthentication().getName();
    return clientes.findByCuenta(login).orElseThrow(() -> new IllegalArgumentException("solo clientes registrados"));
  }

  private void validar(int estrellas, String comentario) {
    if (estrellas < 1 || estrellas > 5) throw new IllegalArgumentException("estrellas de 1 a 5");
    if (comentario == null || comentario.trim().isEmpty()) throw new IllegalArgumentException("comentario obligatorio");
    if (comentario.trim().length() > 500) throw new IllegalArgumentException("comentario máximo 500 caracteres");
  }

  /** Público: comentarios frescos primero (al editar sube al principio). */
  @GetMapping("/api/v1/valoraciones")
  public List<Valoracion> publicas() {
    return repo.findAllByOrderByUpdatedAtDesc();
  }

  /** Mía (o null): para precargar el formulario. */
  @GetMapping("/api/v1/perfil/valoracion")
  public Object mia() {
    var c = clienteActual();
    return repo.findByClienteId(c.id).orElse(null);
  }

  /** Crear (solo si no tengo): una por cliente. */
  @PostMapping("/api/v1/perfil/valoracion")
  public Object crear(@RequestBody Map<String, Object> b) {
    var c = clienteActual();
    if (repo.findByClienteId(c.id).isPresent())
      throw new IllegalStateException("ya tienes un comentario: edítalo en vez de crear otro");
    int estrellas = b.get("estrellas") != null ? Integer.parseInt(b.get("estrellas").toString()) : 5;
    String comentario = b.get("comentario") != null ? b.get("comentario").toString().trim() : "";
    validar(estrellas, comentario);
    var v = new Valoracion();
    v.clienteId = c.id;
    v.nombres = c.nombres + " " + (c.apellidos != null ? c.apellidos : "");
    v.estrellas = estrellas;
    v.comentario = comentario;
    return repo.save(v);
  }

  /** Editar la mía: máximo 3 veces por día; al editar sube al principio. */
  @PutMapping("/api/v1/perfil/valoracion")
  public Object editar(@RequestBody Map<String, Object> b) {
    var c = clienteActual();
    var v = repo.findByClienteId(c.id).orElseThrow(() -> new IllegalArgumentException("aún no tienes comentario"));
    int estrellas = b.get("estrellas") != null ? Integer.parseInt(b.get("estrellas").toString()) : v.estrellas;
    String comentario = b.get("comentario") != null ? b.get("comentario").toString().trim() : v.comentario;
    validar(estrellas, comentario);
    var hoy = LocalDate.now();
    if (hoy.equals(v.fechaUltimaEdicion)) {
      if (v.vecesEditadoHoy >= 3) throw new IllegalStateException("límite de 3 ediciones por día");
      v.vecesEditadoHoy++;
    } else {
      v.fechaUltimaEdicion = hoy;
      v.vecesEditadoHoy = 1;
    }
    v.estrellas = estrellas;
    v.comentario = comentario;
    v.nombres = c.nombres + " " + (c.apellidos != null ? c.apellidos : "");
    v.updatedAt = OffsetDateTime.now();
    v.vistoAdmin = false;
    return repo.save(v);
  }

  /** Admin: bandeja con indicador (vistoAdmin=false = nuevo o cambiado). */
  @GetMapping("/api/v1/admin/valoraciones")
  public List<Valoracion> bandeja() {
    return repo.findAllByOrderByUpdatedAtDesc();
  }

  @PutMapping("/api/v1/admin/valoraciones/{id}/visto")
  public Object marcarVisto(@PathVariable UUID id) {
    var v = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("no existe"));
    v.vistoAdmin = true;
    return repo.save(v);
  }

  @DeleteMapping("/api/v1/admin/valoraciones/{id}")
  public void eliminar(@PathVariable UUID id) {
    repo.deleteById(id);
  }
}
