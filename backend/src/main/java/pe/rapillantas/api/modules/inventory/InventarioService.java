package pe.rapillantas.api.modules.inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventarioService {
  private final ProductoRepository productos;
  private final StockMovimientoRepository movs;

  public InventarioService(ProductoRepository productos, StockMovimientoRepository movs) {
    this.productos = productos;
    this.movs = movs;
  }

  public List<Producto> listar() {
    return productos.findAll();
  }

  @Transactional
  public Producto crear(Producto p) {
    aplicarPrecio(p);
    return productos.save(p);
  }

  @Transactional
  public Producto asignarMargen(UUID id, BigDecimal margenPct) {
    var p = productos.findById(id).orElseThrow(() -> new IllegalArgumentException("producto no existe"));
    p.margenPct = margenPct;
    aplicarPrecio(p);
    return productos.save(p);
  }

  @Transactional
  public void mover(UUID productoId, String tipo, int cantidad, String motivo, UUID refId) {
    var p = productos.findById(productoId).orElseThrow(() -> new IllegalArgumentException("producto no existe"));
    p.stockActual += cantidad; // cantidad con signo desde el llamador
    if (p.stockActual < 0) throw new IllegalStateException("stock insuficiente");
    productos.save(p);
    var m = new StockMovimiento();
    m.productoId = productoId; m.tipo = tipo; m.cantidad = cantidad; m.motivo = motivo; m.refId = refId;
    movs.save(m);
  }

  private void aplicarPrecio(Producto p) {
    // RN-02: precio = costo x (1+margen)
    if (p.costoPromedio != null && p.margenPct != null) {
      p.precio = p.costoPromedio.multiply(BigDecimal.ONE.add(p.margenPct.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)));
    }
  }
}
