package pe.rapillantas.api.modules.inventory;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "productos")
public class Producto {
  @Id
  public UUID id = UUID.randomUUID();
  @NotBlank @Column(nullable = false, unique = true) public String sku;
  @NotBlank @Column(nullable = false) public String nombre;
  @Column(nullable = false) public String categoria = "general";
  @Column(name = "costo_promedio", nullable = false) public BigDecimal costoPromedio = BigDecimal.ZERO;
  @Column(name = "margen_pct", nullable = false) public BigDecimal margenPct = BigDecimal.ZERO;
  @Column(nullable = false) public BigDecimal precio = BigDecimal.ZERO;
  @Column(name = "stock_actual", nullable = false) public int stockActual = 0;
  @Column(name = "es_consignacion", nullable = false) public boolean esConsignacion = false;
  @Column(nullable = false) public boolean activo = true;
  @Column(name = "visible_tienda", nullable = false) public boolean visibleTienda = true;
  public String medida; public String marca; @Column(name = "imagen_url") public String imagenUrl;
}
