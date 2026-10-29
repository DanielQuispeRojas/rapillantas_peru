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
  // Vinculo opcional al catalogo de MOODU por SKU (sin FK: el catalogo vive
  // en MOODU). Columnas UUID anteriores quedan stale en BDs viejas.
  public String sku;
  @Column(name = "servicio_sku") public String servicioSku;
  @Column(nullable = false) public String titulo;
  // Descripción larga de la promo (condiciones, regalos, restricciones).
  @Column(columnDefinition = "TEXT", nullable = false) public String descripcion = "";
  @Column(name = "precio_oferta") public BigDecimal precioOferta;
  @Column(name = "vigencia_desde", nullable = false) public LocalDate vigenciaDesde = LocalDate.now();
  @Column(name = "vigencia_hasta") public LocalDate vigenciaHasta;
  public String banner;
  // Máximo 3 imágenes por oferta (imagenUrl + 2/3).
  @Column(name = "imagen_url") public String imagenUrl;
  @Column(name = "imagen_url2") public String imagenUrl2;
  @Column(name = "imagen_url3") public String imagenUrl3;
  @Column(nullable = false) public int orden = 0;
  @Column(nullable = false) public boolean activa = true;
  @Column(name = "created_at", nullable = false) public OffsetDateTime createdAt = OffsetDateTime.now();
}
