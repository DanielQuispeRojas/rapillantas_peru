package pe.rapillantas.api.modules.inventory;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

// Imagen del sitio por SKU de MOODU (sin FK: el catalogo vive en MOODU,
// este overlay solo cuelga fotos por sku). Max 3 por item en la UI.
@Entity
@Table(name = "producto_imagenes")
public class ProductoImagen {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(nullable = false) public String sku;
  @Column(nullable = false) public String url;
  @Column(nullable = false) public int orden = 0;
  @Column(name = "created_at", nullable = false) public OffsetDateTime createdAt = OffsetDateTime.now();
}
