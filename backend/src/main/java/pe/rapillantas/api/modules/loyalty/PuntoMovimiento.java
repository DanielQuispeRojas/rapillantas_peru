package pe.rapillantas.api.modules.loyalty;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

// Movimiento de puntos del cliente (delta con signo: acumulacion +, canje −).
// Reusa la tabla puntos_movimientos creada en V1 (más ref_canje de V29).
@Entity
@Table(name = "puntos_movimientos")
public class PuntoMovimiento {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(name = "cliente_id", nullable = false) public UUID clienteId;
  @Column(nullable = false) public String tipo = "acumulacion";
  @Column(nullable = false) public int puntos = 0;
  @Column(name = "servicio_canje") public String servicioCanje;
  @Column(name = "ref_venta") public UUID refVenta;
  @Column(name = "ref_canje") public UUID refCanje;
  @Column(name = "created_at") public OffsetDateTime createdAt = OffsetDateTime.now();
}
