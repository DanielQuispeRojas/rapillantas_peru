package pe.rapillantas.api.modules.loyalty;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

// Ítem canjeable con puntos: nombre + descripción + tope opcional de oferta.
// Solo se canjea por puntos; se publica en Ofertas. No es mercadería:
// nunca toca el stock de productos ni lleva precio.
@Entity
@Table(name = "canjeables")
public class Canjeable {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(nullable = false) public String nombre;
  @Column(nullable = false) public String descripcion = "";
  // Legado (ya no se usa en la UI): se conserva la columna para no romper la BD.
  @Column(nullable = false) public String tipo = "otro";
  @Column(name = "servicio_id") public UUID servicioId;
  // Vinculo opcional al catalogo de MOODU por SKU (sin FK). La columna
  // producto_id anterior queda stale en BDs viejas.
  public String sku;
  // Costo en puntos por unidad
  @Column(name = "puntos_costo", nullable = false) public int puntosCosto = 0;
  // Tope SOLO de la oferta (ej. 10 alineamientos). NULL = ilimitado hasta despublicar.
  @Column(nullable = true) public Integer stock;
  @Column(name = "imagen_url") public String imagenUrl;
  @Column(name = "imagen_url2") public String imagenUrl2;
  @Column(name = "imagen_url3") public String imagenUrl3;
  @Column(nullable = false) public boolean publicado = true;
  @Column(nullable = false) public boolean activo = true;
  @Column(name = "created_at", nullable = false) public OffsetDateTime createdAt = OffsetDateTime.now();
}
