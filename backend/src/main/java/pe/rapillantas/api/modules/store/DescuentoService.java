package pe.rapillantas.api.modules.store;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DescuentoService {
  private final DescuentoRepository repo;

  public DescuentoService(DescuentoRepository repo) {
    this.repo = repo;
  }

  public List<Descuento> listar() {
    return repo.findAll();
  }

  /** Programa un descuento: inicio nunca menor a hoy, fin >= inicio, sin solape misma categoría. */
  @Transactional
  public Descuento programar(Descuento d) {
    var hoy = LocalDate.now();
    if (d.inicio == null || d.inicio.isBefore(hoy))
      throw new IllegalArgumentException("inicio debe ser hoy o una fecha mayor");
    if (d.fin == null || d.fin.isBefore(d.inicio))
      throw new IllegalArgumentException("fin debe ser igual o mayor al inicio");
    if (d.pct == null || d.pct.signum() <= 0 || d.pct.compareTo(new BigDecimal("100")) >= 0)
      throw new IllegalArgumentException("dscto % debe estar entre 0 y 100");
    if (d.categoria == null || d.categoria.isBlank()) d.categoria = "todos";
    boolean solapa = repo.findByCatalogoAndActivo(d.catalogo, true).stream()
        .anyMatch(x -> x.categoria.equalsIgnoreCase(d.categoria)
            && !(d.fin.isBefore(x.inicio) || d.inicio.isAfter(x.fin)));
    if (solapa) throw new IllegalStateException(
        "ya existe un descuento para " + d.catalogo + "/" + d.categoria + " en esas fechas");
    d.activo = true;
    return repo.save(d);
  }

  @Transactional
  public void desactivar(java.util.UUID id) {
    var d = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("descuento no existe"));
    d.activo = false;
    repo.save(d);
  }

  public List<Descuento> vigentes(String catalogo) {
    var hoy = LocalDate.now();
    return repo.findByCatalogoAndActivo(catalogo, true).stream()
        .filter(d -> !hoy.isBefore(d.inicio) && !hoy.isAfter(d.fin)).toList();
  }

  /** Precio con descuento: busca vigente para la categoría exacta, si no el "todos". */
  public BigDecimal aplicar(String catalogo, String categoria, BigDecimal precio) {
    var hoy = LocalDate.now();
    var vig = vigentes(catalogo);
    var d = vig.stream().filter(x -> x.categoria.equalsIgnoreCase(categoria)).findFirst()
        .or(() -> vig.stream().filter(x -> x.categoria.equalsIgnoreCase("todos")).findFirst());
    if (d.isEmpty() || precio == null) return precio;
    return precio.multiply(BigDecimal.ONE.subtract(
        d.get().pct.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
        .setScale(2, RoundingMode.HALF_UP);
  }
}
