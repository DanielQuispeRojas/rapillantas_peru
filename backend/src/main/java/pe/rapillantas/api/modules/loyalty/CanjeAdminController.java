package pe.rapillantas.api.modules.loyalty;

import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

/** Administración: catálogo de canjeables + despacho de canjes por recoger. */
@RestController
@RequestMapping("/api/v1/admin/canjeables")
public class CanjeAdminController {
  private final CanjeService svc;

  public CanjeAdminController(CanjeService svc) {
    this.svc = svc;
  }

  @GetMapping
  public Object todos() {
    return svc.todos();
  }

  @PostMapping
  public Object crear(@RequestBody Canjeable c) {
    return svc.guardar(c);
  }

  @PutMapping("/{id}")
  public Object editar(@PathVariable UUID id, @RequestBody Canjeable c) {
    return svc.editar(id, c);
  }

  @DeleteMapping("/{id}")
  public void eliminar(@PathVariable UUID id) {
    svc.eliminar(id);
  }

  @PostMapping(value = "/{id}/imagen", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
  public Object subirImagen(@PathVariable UUID id,
                            @RequestParam("archivo") org.springframework.web.multipart.MultipartFile archivo) throws java.io.IOException {
    return svc.subirImagen(id, archivo);
  }

  @GetMapping("/canjes")
  public Object canjes(@RequestParam(required = false) String estado) {
    return svc.porEstado(estado);
  }

  /** Confirma el despacho: el cliente recoge en tienda. */
  @PostMapping("/canjes/{id}/despachar")
  public Object despachar(@PathVariable UUID id) {
    return svc.despachar(id);
  }

  /** Cancela un por_recoger: devuelve puntos y stock. */
  @PostMapping("/canjes/{id}/cancelar")
  public Object cancelar(@PathVariable UUID id) {
    return svc.cancelar(id);
  }

  /** Rechaza un por_recoger (sin stock / oferta caída): devuelve puntos y stock. */
  @PostMapping("/canjes/{id}/rechazar")
  public Object rechazar(@PathVariable UUID id) {
    return svc.rechazar(id);
  }
}
