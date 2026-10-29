package pe.rapillantas.api.modules.inventory;

import jakarta.persistence.*;
import java.util.UUID;

// Compatibilidad del sitio por SKU de MOODU (sin FK: el catalogo vive en
// MOODU, este overlay solo cuelga compatibilidades por sku).
@Entity
@Table(name = "producto_compatibilidad")
public class ProductoCompatibilidad {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(nullable = false) public String sku;
  @Column(nullable = false) public String marca;
  @Column(nullable = false) public String modelo;
  @Column(name = "anio_desde") public Integer anioDesde;
  @Column(name = "anio_hasta") public Integer anioHasta;
  public String nota;
}
