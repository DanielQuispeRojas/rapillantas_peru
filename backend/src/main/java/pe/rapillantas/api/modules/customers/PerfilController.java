package pe.rapillantas.api.modules.customers;

import java.util.List;
import java.util.Map;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pe.rapillantas.api.modules.auth.UsuarioRepository;

/** Perfil privado del CLIENTE (requiere token): sus veh�culos y puntos.
 * El historial de taller (expedientes/OT) vive en MOODU y se consulta por
 * /pub desde el frontend con la placa (ver lib/moodu.ts). */
@RestController
@RequestMapping("/api/v1/perfil")
public class PerfilController {
  private final ClienteRepository clientes;
  private final VehiculoRepository vehiculos;
  private final UsuarioRepository usuarios;

  public PerfilController(ClienteRepository clientes, VehiculoRepository vehiculos,
                          UsuarioRepository usuarios) {
    this.clientes = clientes;
    this.vehiculos = vehiculos;
    this.usuarios = usuarios;
  }

  private Cliente clienteActual() {
    String login = SecurityContextHolder.getContext().getAuthentication().getName();
    return clientes.findByCuenta(login).orElseThrow(() -> new IllegalArgumentException("sin ficha de cliente"));
  }

  @GetMapping
  public Object mio() {
    var c = clienteActual();
    var vehs = vehiculos.findAll().stream().filter(v -> c.id.equals(v.clienteId)).toList();
    return Map.of("cliente", Map.of("nombres", c.nombres, "apellidos", c.apellidos,
            "telefono", c.telefono, "correo", c.correo != null ? c.correo : "",
            "cuenta", c.cuenta != null ? c.cuenta : "", "puntos", c.puntos),
        "vehiculos", vehs);
  }

  /** El cliente edita sus datos (nombre, teléfono, correo). */
  @PutMapping
  @Transactional
  public Object actualizar(@RequestBody Map<String, Object> b) {
    var c = clienteActual();
    Object nom = b.get("nombres");
    if (nom != null && !nom.toString().isBlank()) c.nombres = nom.toString().trim();
    Object ape = b.get("apellidos");
    if (ape != null) c.apellidos = ape.toString().trim();
    Object tel = b.get("telefono");
    if (tel != null) {
      if (tel.toString().isBlank()) throw new IllegalArgumentException("teléfono obligatorio");
      c.telefono = tel.toString().trim();
    }
    Object cor = b.get("correo");
    if (cor != null) {
      String correo = cor.toString().trim().toLowerCase();
      if (!correo.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) throw new IllegalArgumentException("correo inválido");
      var otro = clientes.findByCorreo(correo);
      if (otro.isPresent() && !otro.get().id.equals(c.id)) throw new IllegalStateException("correo ya registrado");
      c.correo = correo;
    }
    if (c.nombres.isBlank()) throw new IllegalArgumentException("nombres obligatorios");
    clientes.save(c);
    // Nombre visible de la cuenta acompaña al cambio
    if (c.cuenta != null) {
      usuarios.findByLogin(c.cuenta).ifPresent(u -> {
        u.nombre = c.nombres + (c.apellidos.isBlank() ? "" : " " + c.apellidos);
        usuarios.save(u);
      });
    }
    return Map.of("ok", true);
  }

  /** El cliente registra manualmente un vehículo suyo (crea su expediente). */
  @PostMapping("/vehiculos")
  @Transactional
  public Object agregarVehiculo(@RequestBody Vehiculo v) {
    var c = clienteActual();
    if (v.placa == null || v.placa.isBlank()) throw new IllegalArgumentException("placa obligatoria");
    v.placa = v.placa.trim().toUpperCase();
    if (vehiculos.findByPlacaIgnoreCase(v.placa).isPresent())
      throw new IllegalStateException("esa placa ya tiene un expediente");
    if (v.marca == null || v.marca.isBlank() || v.modelo == null || v.modelo.isBlank())
      throw new IllegalArgumentException("marca y modelo obligatorios");
    if (v.kmActual < 0) throw new IllegalArgumentException("km no puede ser negativo");
    v.clienteId = c.id;
    return vehiculos.save(v);
  }

  @GetMapping("/vehiculos")
  public List<Vehiculo> misVehiculos() {
    String login = SecurityContextHolder.getContext().getAuthentication().getName();
    var c = clientes.findByCuenta(login).orElseThrow(() -> new IllegalArgumentException("sin ficha de cliente"));
    return vehiculos.findAll().stream().filter(v -> c.id.equals(v.clienteId)).toList();
  }
}
