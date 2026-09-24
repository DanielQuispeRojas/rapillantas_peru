package pe.rapillantas.api.modules.sales;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "venta_items")
public class VentaItem {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(name = "producto_id", nullable = false) public UUID productoId;
  @Column(nullable = false) public int cantidad;
  @Column(name = "precio_unit", nullable = false) public BigDecimal precioUnit = BigDecimal.ZERO;
}
