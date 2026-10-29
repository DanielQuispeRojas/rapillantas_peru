package pe.rapillantas.api.modules.loyalty;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

// Puntos por SKU del catalogo de MOODU (overlay propio del sitio).
// El frontend los muestra en la tienda y VentaService los acredita al vender.
@RestController
@RequestMapping("/api/v1/admin/puntos-sku")
public class PuntosSkuController {
  private final PuntosSkuRepository repo;

  public PuntosSkuController(PuntosSkuRepository repo) {
    this.repo = repo;
  }

  @GetMapping
  public List<PuntosSku> listar() {
    return repo.findAll();
  }

  @PutMapping("/{sku}")
  public PuntosSku fijar(@PathVariable String sku, @RequestBody Map<String, Object> body) {
    if (sku == null || sku.isBlank()) throw new IllegalArgumentException("sku obligatorio");
    int pts = 0;
    try {
      pts = Integer.parseInt(String.valueOf(body.getOrDefault("puntos", "0")));
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("puntos debe ser numero");
    }
    if (pts < 0) throw new IllegalArgumentException("puntos no puede ser negativo");
    var p = repo.findById(sku.trim().toUpperCase()).orElseGet(PuntosSku::new);
    p.sku = sku.trim().toUpperCase();
    p.puntos = pts;
    p.updatedAt = OffsetDateTime.now();
    return repo.save(p);
  }

  @DeleteMapping("/{sku}")
  public void quitar(@PathVariable String sku) {
    repo.deleteById(sku.trim().toUpperCase());
  }
}
