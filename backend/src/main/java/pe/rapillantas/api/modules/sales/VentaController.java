package pe.rapillantas.api.modules.sales;

import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ventas")
public class VentaController {
  private final VentaService svc;

  public VentaController(VentaService svc) {
    this.svc = svc;
  }

  @GetMapping
  public Object listar() {
    return svc.listar();
  }

  @PostMapping
  public Object vender(@RequestBody Venta v) {
    return svc.vender(v);
  }

  @PostMapping("/{id}/anular")
  public Object anular(@PathVariable UUID id) {
    return svc.anular(id);
  }
}
