package pe.rapillantas.api.modules.loyalty;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

// Puntos que otorga un SKU de MOODU por unidad comprada. El catalogo vive en
// MOODU (sin FK); este overlay del sitio solo cuelga puntos por sku.
@Entity
@Table(name = "puntos_sku")
public class PuntosSku {
  @Id
  @Column(nullable = false) public String sku;
  @Column(nullable = false) public int puntos = 0;
  @Column(name = "updated_at", nullable = false) public OffsetDateTime updatedAt = OffsetDateTime.now();
}
