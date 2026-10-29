package pe.rapillantas.api.modules.store;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "sliders")
public class Slider {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(nullable = false, unique = true) public String clave;
  @Column(nullable = false) public String nombre;
  @Column(name = "created_at", nullable = false) public OffsetDateTime createdAt = OffsetDateTime.now();
}
