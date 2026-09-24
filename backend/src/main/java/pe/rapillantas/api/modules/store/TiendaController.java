package pe.rapillantas.api.modules.store;

import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import pe.rapillantas.api.modules.inventory.ProductoRepository;

@RestController
public class TiendaController {
  private final ProductoRepository productos;
  private final OfertaRepository ofertas;
  private final ServicioRepository servicios;

  public TiendaController(ProductoRepository productos, OfertaRepository ofertas, ServicioRepository servicios) {
    this.productos = productos;
    this.ofertas = ofertas;
    this.servicios = servicios;
  }

  // Públicos (sin login) — RF-25
  @GetMapping("/api/v1/catalogo")
  public Object catalogo(@RequestParam(required = false) String categoria,
                         @RequestParam(required = false) String marca,
                         @RequestParam(required = false) String medida) {
    return productos.findAll().stream()
        .filter(p -> p.activo && p.visibleTienda)
        .filter(p -> categoria == null || p.categoria.equalsIgnoreCase(categoria))
        .filter(p -> marca == null || (p.marca != null && p.marca.equalsIgnoreCase(marca)))
        .filter(p -> medida == null || (p.medida != null && p.medida.equalsIgnoreCase(medida)))
        .toList();
  }

  @GetMapping("/api/v1/ofertas")
  public Object ofertas() {
    var hoy = LocalDate.now();
    return ofertas.findAll().stream()
        .filter(o -> o.activa && !o.vigenciaDesde.isAfter(hoy)
            && (o.vigenciaHasta == null || !o.vigenciaHasta.isBefore(hoy)))
        .toList();
  }

  @GetMapping("/api/v1/servicios")
  public Object servicios() {
    return servicios.findAll().stream().filter(s -> s.activo && s.visible).toList();
  }

  // Admin — publicar ofertas
  @PostMapping("/api/v1/admin/ofertas")
  public Object crearOferta(@RequestBody Oferta o) {
    if (o.productoId == null && o.servicioId == null) throw new IllegalStateException("oferta debe apuntar a producto o servicio");
    return ofertas.save(o);
  }

  @GetMapping("/api/v1/admin/ofertas")
  public List<Oferta> listarOfertas() {
    return ofertas.findAll();
  }
}
