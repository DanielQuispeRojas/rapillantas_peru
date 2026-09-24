package pe.rapillantas.api.modules.purchasing;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "oc_items")
public class OcItem {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(name = "producto_id", nullable = false) public UUID productoId;
  @Column(nullable = false) public int cantidad;
  @Column(name = "costo_unit", nullable = false) public BigDecimal costoUnit = BigDecimal.ZERO;
}
