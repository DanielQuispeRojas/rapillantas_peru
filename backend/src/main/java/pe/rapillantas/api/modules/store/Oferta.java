package pe.rapillantas.api.modules.store;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ofertas")
public class Oferta {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(name = "producto_id") public UUID productoId;
  @Column(name = "servicio_id") public UUID servicioId;
  @Column(nullable = false) public String titulo;
  @Column(name = "precio_oferta") public BigDecimal precioOferta;
  @Column(name = "vigencia_desde", nullable = false) public LocalDate vigenciaDesde = LocalDate.now();
  @Column(name = "vigencia_hasta") public LocalDate vigenciaHasta;
  public String banner;
  @Column(nullable = false) public boolean activa = true;
  @Column(name = "created_at", nullable = false) public OffsetDateTime createdAt = OffsetDateTime.now();
}
