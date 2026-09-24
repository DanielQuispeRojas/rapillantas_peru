package pe.rapillantas.api.modules.purchasing;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.rapillantas.api.modules.inventory.InventarioService;
import pe.rapillantas.api.modules.inventory.ProductoRepository;

@Service
public class OcService {
  private final OcRepository ocs;
  private final ProductoRepository productos;
  private final InventarioService inventario;

  public OcService(OcRepository ocs, ProductoRepository productos, InventarioService inventario) {
    this.ocs = ocs;
    this.productos = productos;
    this.inventario = inventario;
  }

  public List<Oc> listar() {
    return ocs.findAll();
  }

  @Transactional
  public Oc crear(Oc oc) {
    oc.estado = "confirmada";
    return ocs.save(oc);
  }

  @Transactional
  public Oc recepcionar(UUID id) {
    var oc = ocs.findById(id).orElseThrow(() -> new IllegalArgumentException("OC no existe"));
    if (oc.estado.equals("recibida")) throw new IllegalStateException("OC ya recibida");
    for (var it : oc.items) {
      var p = productos.findById(it.productoId).orElseThrow(() -> new IllegalArgumentException("producto no existe"));
      int total = p.stockActual + it.cantidad;
      var nuevoCosto = p.costoPromedio.multiply(java.math.BigDecimal.valueOf(p.stockActual))
          .add(it.costoUnit.multiply(java.math.BigDecimal.valueOf(it.cantidad)));
      if (total > 0) p.costoPromedio = nuevoCosto.divide(java.math.BigDecimal.valueOf(total), 2, java.math.RoundingMode.HALF_UP);
      productos.save(p);
      inventario.mover(p.id, "recepcion_oc", it.cantidad, "OC " + oc.id, oc.id);
    }
    oc.estado = "recibida";
    return ocs.save(oc);
  }
}
