package pe.rapillantas.api.modules.auth;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pe.rapillantas.api.modules.customers.Cliente;
import pe.rapillantas.api.modules.customers.ClienteRepository;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final UsuarioRepository usuarios;
  private final ClienteRepository clientes;
  private final PasswordEncoder pass;
  private final JwtService jwt;

  public AuthController(UsuarioRepository usuarios, ClienteRepository clientes,
                        PasswordEncoder pass, JwtService jwt) {
    this.usuarios = usuarios;
    this.clientes = clientes;
    this.pass = pass;
    this.jwt = jwt;
  }

  /** Registro público: solo rol CLIENTE. Enlaza Cliente.cuenta = login. */
  @PostMapping("/registro")
  @Transactional
  public Map<String, Object> registro(@RequestBody Map<String, String> b) {
    String login = b.getOrDefault("login", "").trim().toLowerCase();
    String clave = b.getOrDefault("clave", "");
    String nombres = b.getOrDefault("nombres", "").trim();
    String apellidos = b.getOrDefault("apellidos", "").trim();
    String correo = b.getOrDefault("correo", "").trim().toLowerCase();
    String telefono = b.getOrDefault("telefono", "").trim();
    if (login.isBlank() || clave.length() < 4 || nombres.isBlank() || apellidos.isBlank()
        || telefono.isBlank() || !correo.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+"))
      throw new IllegalArgumentException("nombres, apellidos, correo válido, teléfono, login y clave (mín 4) obligatorios");
    if (usuarios.findByLogin(login).isPresent()) throw new IllegalStateException("login ya registrado");
    if (clientes.findByCorreo(correo).isPresent()) throw new IllegalStateException("correo ya registrado");
    var u = new Usuario();
    u.nombre = nombres + (b.getOrDefault("apellidos", "").isBlank() ? "" : " " + b.get("apellidos").trim());
    u.login = login;
    u.passHash = pass.encode(clave);
    u.rol = "CLIENTE";
    usuarios.save(u);
    var c = new Cliente();
    c.nombres = nombres;
    c.apellidos = apellidos;
    c.telefono = telefono;
    c.correo = correo;
    c.cuenta = login;
    clientes.save(c);
    return Map.of("token", jwt.emitir(u), "rol", u.rol, "nombre", u.nombre, "login", u.login, "paneles", "");
  }

  /** Renovar sesión activa: emite token fresco (el frontend lo pide con actividad). */
  @PostMapping("/renovar")
  public Map<String, Object> renovar() {
    String login = org.springframework.security.core.context.SecurityContextHolder
        .getContext().getAuthentication().getName();
    var u = usuarios.findByLogin(login).orElseThrow(() -> new IllegalArgumentException("sesión inválida"));
    if (!u.activo) throw new IllegalStateException("usuario inactivo");
    return Map.of("token", jwt.emitir(u));
  }

  @PostMapping("/login")
  public Map<String, Object> login(@RequestBody Map<String, String> b) {
    String login = b.getOrDefault("login", "").trim().toLowerCase();
    var u = usuarios.findByLogin(login).orElseThrow(() -> new IllegalArgumentException("credenciales inválidas"));
    if (!u.activo || !pass.matches(b.getOrDefault("clave", ""), u.passHash))
      throw new IllegalArgumentException("credenciales inválidas");
    return Map.of("token", jwt.emitir(u), "rol", u.rol, "nombre", u.nombre,
        "login", u.login, "paneles", u.paneles != null ? u.paneles : "");
  }

  /** Crea ADMIN/MECANICO/COLABORADOR (solo ADMIN o PANEL_USUARIOS). Con paneles a medida. */
  @PostMapping("/usuarios")
  @Transactional
  public Map<String, Object> crearInterno(@RequestBody Map<String, String> b) {
    String rol = b.getOrDefault("rol", "");
    if (!rol.equals("ADMIN") && !rol.equals("MECANICO") && !rol.equals("COLABORADOR"))
      throw new IllegalArgumentException("rol debe ser ADMIN, MECANICO o COLABORADOR");
    String login = b.getOrDefault("login", "").trim().toLowerCase();
    if (usuarios.findByLogin(login).isPresent()) throw new IllegalStateException("login ya registrado");
    var u = new Usuario();
    u.nombre = b.getOrDefault("nombre", login);
    u.login = login;
    u.passHash = pass.encode(b.getOrDefault("clave", ""));
    u.rol = rol;
    u.paneles = b.getOrDefault("paneles", rol.equals("MECANICO") ? "taller" : "");
    usuarios.save(u);
    return Map.of("login", u.login, "rol", u.rol);
  }

  @GetMapping("/usuarios")
  public Object listar() {
    // Nunca exponer passHash: se devuelve un DTO seguro (RN: hashes fuera de la API).
    return usuarios.findAll().stream().map(u -> Map.of(
        "id", u.id.toString(), "nombre", u.nombre, "login", u.login,
        "rol", u.rol, "paneles", u.paneles != null ? u.paneles : "",
        "activo", u.activo)).toList();
  }

  @GetMapping("/google-client-id")
  public Map<String, String> googleClientId(
      @Value("${app.google-client-id:}") String clientId) {
    return Map.of("clientId", clientId != null ? clientId : "");
  }

  /** Regístrate con Google: verifica el ID token y crea/enlaza la cuenta CLIENTE. */
  @PostMapping("/google")
  @Transactional
  public Map<String, Object> google(@RequestBody Map<String, String> b,
                                    @Value("${app.google-client-id:}") String clientId) throws Exception {
    if (clientId == null || clientId.isBlank())
      throw new IllegalStateException("Google OAuth no configurado (GOOGLE_CLIENT_ID)");
    var verifier = new com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier.Builder(
        new com.google.api.client.http.javanet.NetHttpTransport(),
        com.google.api.client.json.gson.GsonFactory.getDefaultInstance())
        .setAudience(java.util.Collections.singletonList(clientId)).build();
    var idToken = verifier.verify(b.getOrDefault("idToken", ""));
    if (idToken == null) throw new IllegalArgumentException("token Google inválido");
    var payload = idToken.getPayload();
    if (!Boolean.TRUE.equals(payload.getEmailVerified()))
      throw new IllegalArgumentException("correo Google no verificado");
    String correo = ((String) payload.getEmail()).toLowerCase();
    var u = usuarios.findByLogin(correo).orElseGet(() -> {
      var n = new Usuario();
      n.nombre = (String) payload.getOrDefault("name", correo);
      n.login = correo;
      n.passHash = pass.encode(java.util.UUID.randomUUID().toString());
      n.rol = "CLIENTE";
      return usuarios.save(n);
    });
    if (!"CLIENTE".equals(u.rol)) throw new IllegalStateException("cuenta interna, usa login normal");
    if (clientes.findByCuenta(correo).isEmpty() && clientes.findByCorreo(correo).isEmpty()) {
      var c = new Cliente();
      c.nombres = (String) payload.getOrDefault("given_name", correo);
      c.apellidos = (String) payload.getOrDefault("family_name", "");
      c.telefono = "";
      c.correo = correo;
      c.cuenta = correo;
      clientes.save(c);
    }
    return Map.of("token", jwt.emitir(u), "rol", u.rol, "nombre", u.nombre, "login", u.login,
        "paneles", u.paneles != null ? u.paneles : "");
  }
}
