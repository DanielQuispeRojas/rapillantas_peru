package pe.rapillantas.api.modules.purchasing;

import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ocs")
public class OcController {
  private final OcService svc;

  public OcController(OcService svc) {
    this.svc = svc;
  }

  @GetMapping
  public Object listar() {
    return svc.listar();
  }

  @PostMapping
  public Object crear(@RequestBody Oc oc) {
    return svc.crear(oc);
  }

  @PostMapping("/{id}/recepcion")
  public Object recepcionar(@PathVariable UUID id) {
    return svc.recepcionar(id);
  }
}
