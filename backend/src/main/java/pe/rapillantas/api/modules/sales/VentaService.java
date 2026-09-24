package pe.rapillantas.api.modules.sales;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.rapillantas.api.modules.inventory.InventarioService;
import pe.rapillantas.api.modules.inventory.ProductoRepository;

@Service
public class VentaService {
  private final VentaRepository ventas;
  private final ProductoRepository productos;
  private final InventarioService inventario;

  public VentaService(VentaRepository ventas, ProductoRepository productos, InventarioService inventario) {
    this.ventas = ventas;
    this.productos = productos;
    this.inventario = inventario;
  }

  public List<Venta> listar() {
    return ventas.findAll();
  }

  @Transactional
  public Venta vender(Venta v) {
    BigDecimal total = BigDecimal.ZERO;
    for (var it : v.items) {
      var p = productos.findById(it.productoId).orElseThrow(() -> new IllegalArgumentException("producto no existe"));
      if (p.stockActual < it.cantidad) throw new IllegalStateException("stock insuficiente: " + p.sku);
      if (it.precioUnit == null || it.precioUnit.signum() == 0) it.precioUnit = p.precio;
      total = total.add(it.precioUnit.multiply(BigDecimal.valueOf(it.cantidad)));
    }
    v.total = total;
    var saved = ventas.save(v);
    for (var it : saved.items) inventario.mover(it.productoId, "venta", -it.cantidad, "venta " + saved.id, saved.id);
    return saved;
  }

  @Transactional
  public Venta anular(UUID id) {
    var v = ventas.findById(id).orElseThrow(() -> new IllegalArgumentException("venta no existe"));
    if (v.anulada) throw new IllegalStateException("ya anulada");
    for (var it : v.items) inventario.mover(it.productoId, "anulacion", it.cantidad, "anula " + v.id, v.id);
    v.anulada = true;
    return ventas.save(v);
  }
}
