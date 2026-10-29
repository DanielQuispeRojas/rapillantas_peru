package pe.rapillantas.api.modules.customers;

import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {
  private final ClienteRepository clientes;
  private final VehiculoRepository vehiculos;

  public ClienteController(ClienteRepository clientes, VehiculoRepository vehiculos) {
    this.clientes = clientes;
    this.vehiculos = vehiculos;
  }

  @PostMapping
  public Object crear(@Valid @RequestBody Cliente c) {
    if (c.telefono == null || c.telefono.isBlank()) throw new IllegalStateException("telefono obligatorio (RF-20)");
    return clientes.save(c);
  }

  @PostMapping("/{id}/vehiculos")
  public Object addVehiculo(@PathVariable("id") java.util.UUID id, @RequestBody Vehiculo v) {
    v.clienteId = id;
    return vehiculos.save(v);
  }
}
