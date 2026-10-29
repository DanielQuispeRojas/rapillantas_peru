package pe.rapillantas.api.modules.store;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/descuentos")
public class DescuentoController {
  private final DescuentoService svc;

  public DescuentoController(DescuentoService svc) {
    this.svc = svc;
  }

  @GetMapping
  public List<Descuento> listar() {
    return svc.listar();
  }

  @PostMapping
  public Descuento programar(@RequestBody Descuento d) {
    return svc.programar(d);
  }

  @PostMapping("/{id}/desactivar")
  public void desactivar(@PathVariable UUID id) {
    svc.desactivar(id);
  }

  @GetMapping("/vigentes")
  public List<Descuento> vigentes(@RequestParam(defaultValue = "productos") String catalogo) {
    return svc.vigentes(catalogo);
  }
}
