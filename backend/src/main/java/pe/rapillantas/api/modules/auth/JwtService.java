package pe.rapillantas.api.modules.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final SecretKey key;
  private final long expiracionMs;

  public JwtService(@Value("${app.jwt-secret:}") String secret,
                    @Value("${app.jwt-expiracion-ms:3600000}") long expiracionMs,
                    @Value("${spring.profiles.active:dev}") String profile) {
    if (secret == null || secret.isBlank()) {
      if (profile.toLowerCase().contains("prod"))
        throw new IllegalStateException("app.jwt-secret obligatorio en prod (env APP_JWT_SECRET); sin él los tokens son forjables");
      secret = "rapillantas-dev-secret-min-32-chars!!"; // solo desarrollo local, nunca prod
    }
    if (secret.length() < 32)
      throw new IllegalStateException("app.jwt-secret debe tener al menos 32 caracteres");
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expiracionMs = expiracionMs;
  }

  public String emitir(Usuario u) {
    return Jwts.builder()
        .subject(u.login)
        .claims(Map.of("rol", u.rol, "nombre", u.nombre, "paneles", u.paneles != null ? u.paneles : ""))
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiracionMs))
        .signWith(key)
        .compact();
  }

  public Map<String, String> validar(String token) {
    var claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    return Map.of("login", claims.getSubject(),
        "rol", claims.get("rol", String.class),
        "nombre", claims.get("nombre", String.class),
        "paneles", claims.get("paneles", String.class) != null ? claims.get("paneles", String.class) : "");
  }
}
