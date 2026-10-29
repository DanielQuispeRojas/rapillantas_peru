package pe.rapillantas.api.modules.store;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "descuentos")
public class Descuento {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(nullable = false) public String catalogo = "productos"; // productos | servicios
  @Column(nullable = false) public BigDecimal pct = BigDecimal.ZERO;
  @Column(nullable = false) public LocalDate inicio = LocalDate.now();
  @Column(nullable = false) public LocalDate fin = LocalDate.now();
  @Column(nullable = false) public String categoria = "todos";
  @Column(nullable = false) public boolean activo = true;
  @Column(name = "created_at", nullable = false) public OffsetDateTime createdAt = OffsetDateTime.now();
}
