package pe.rapillantas.api.modules.workshop;

import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/os")
public class OsController {
  private final TallerService svc;

  public OsController(TallerService svc) {
    this.svc = svc;
  }

  @GetMapping
  public Object listar() {
    return svc.listar();
  }

  @PostMapping
  public Object abrir(@RequestBody OrdenServicio os) {
    return svc.abrir(os);
  }

  @PostMapping("/{id}/cerrar")
  public Object cerrar(@PathVariable UUID id) {
    return svc.cerrar(id);
  }
}
