package pe.rapillantas.api.modules.inventory;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_movimientos")
public class StockMovimiento {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(name = "producto_id", nullable = false) public UUID productoId;
  @Column(nullable = false) public String tipo; // ingreso,salida,ajuste,merma,recepcion_oc,venta,anulacion
  @Column(nullable = false) public int cantidad;
  public String motivo;
  @Column(name = "ref_id") public UUID refId;
  @Column(name = "created_at", nullable = false) public OffsetDateTime createdAt = OffsetDateTime.now();
}
