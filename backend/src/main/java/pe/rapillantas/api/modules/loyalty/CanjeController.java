package pe.rapillantas.api.modules.loyalty;

import java.util.Map;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pe.rapillantas.api.modules.customers.ClienteRepository;

/** Vitrina pública de canjeables + canjes del cliente (requiere login CLIENTE). */
@RestController
public class CanjeController {
  private final CanjeService svc;
  private final ClienteRepository clientes;

  public CanjeController(CanjeService svc, ClienteRepository clientes) {
    this.svc = svc;
    this.clientes = clientes;
  }

  @GetMapping("/api/v1/canjeables")
  public Object publicos() {
    return svc.publicos();
  }

  private UUID clienteActual() {
    String login = SecurityContextHolder.getContext().getAuthentication().getName();
    return clientes.findByCuenta(login)
        .orElseThrow(() -> new IllegalArgumentException("sin ficha de cliente")).id;
  }

  /** Cliente canjea sus puntos: queda por_recoger hasta el despacho en tienda. */
  @PostMapping("/api/v1/canjes")
  public Object canjear(@RequestBody Map<String, String> body) {
    String id = body != null ? body.get("canjeableId") : null;
    if (id == null || id.isBlank()) throw new IllegalArgumentException("canjeableId obligatorio");
    return svc.canjear(clienteActual(), UUID.fromString(id));
  }

  @GetMapping("/api/v1/canjes/mios")
  public Object mios() {
    return svc.mios(clienteActual());
  }
}
