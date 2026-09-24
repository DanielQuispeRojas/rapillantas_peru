package pe.rapillantas.api.modules.store;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "servicios")
public class Servicio {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(nullable = false) public String nombre;
  public String descripcion;
  @Column(name = "precio_ref", nullable = false) public BigDecimal precioRef = BigDecimal.ZERO;
  @Column(nullable = false) public boolean visible = true;
  @Column(nullable = false) public boolean activo = true;
}
