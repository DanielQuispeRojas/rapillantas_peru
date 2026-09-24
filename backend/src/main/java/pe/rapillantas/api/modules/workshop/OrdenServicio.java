package pe.rapillantas.api.modules.workshop;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "os")
public class OrdenServicio {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(name = "vehiculo_id", nullable = false) public UUID vehiculoId;
  public String diagnostico;
  @Column(name = "mecanico_id") public UUID mecanicoId;
  @Column(nullable = false) public String estado = "abierta";
  @Column(name = "km_atencion", nullable = false) public int kmAtencion;
  @Column(name = "created_at", nullable = false) public OffsetDateTime createdAt = OffsetDateTime.now();
  @Column(name = "closed_at") public OffsetDateTime closedAt;
}
