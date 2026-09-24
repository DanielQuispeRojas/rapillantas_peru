package pe.rapillantas.api.modules.inventory;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {
  private final InventarioService svc;

  public ProductoController(InventarioService svc) {
    this.svc = svc;
  }

  @GetMapping
  public Object listar() {
    return svc.listar();
  }

  @PostMapping
  public Object crear(@Valid @RequestBody Producto p) {
    return svc.crear(p);
  }

  @PutMapping("/{id}/margen")
  public Object margen(@PathVariable UUID id, @RequestBody Map<String, BigDecimal> body) {
    return svc.asignarMargen(id, body.get("margenPct"));
  }
}
