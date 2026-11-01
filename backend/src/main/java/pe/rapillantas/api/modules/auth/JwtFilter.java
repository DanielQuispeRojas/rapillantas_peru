package pe.rapillantas.api.modules.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {
  private final JwtService jwt;

  public JwtFilter(JwtService jwt) {
    this.jwt = jwt;
  }

  // Rutas públicas (espejo de SecurityConfig). Un token inválido/expirado en
  // estas rutas NO debe bloquear: se limpia el contexto y se sigue como anónimo.
  // Esto evita que un token viejo en localStorage rompa el login desde un túnel
  // (el frontend adjunta el token si existe, aunque la ruta sea pública).
  private static final java.util.List<String> RUTAS_PUBLICAS = java.util.List.of(
      "/api/v1/auth/", "/api/v1/tienda/", "/api/v1/ofertas", "/api/v1/sku/",
      "/api/v1/compatibilidad/", "/api/v1/puntos", "/api/v1/canjeables",
      "/api/v1/portada", "/api/v1/empresa", "/api/v1/valoraciones",
      "/uploads/", "/actuator/health");

  private boolean esRutaPublica(String path) {
    for (var r : RUTAS_PUBLICAS) if (path.startsWith(r)) return true;
    return false;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String h = req.getHeader("Authorization");
    if (h != null && h.startsWith("Bearer ")) {
      try {
        var d = jwt.validar(h.substring(7));
        var auths = new java.util.ArrayList<SimpleGrantedAuthority>();
        auths.add(new SimpleGrantedAuthority("ROLE_" + d.get("rol")));
        for (var p : d.get("paneles").split(",")) {
          if (!p.isBlank()) auths.add(new SimpleGrantedAuthority("PANEL_" + p.trim().toUpperCase()));
        }
        var auth = new UsernamePasswordAuthenticationToken(d.get("login"), null, auths);
        auth.setDetails(d);
        SecurityContextHolder.getContext().setAuthentication(auth);
      } catch (Exception e) {
        SecurityContextHolder.clearContext();
        // En rutas públicas no bloqueamos: el request sigue como anónimo.
        if (!esRutaPublica(req.getRequestURI())) {
          res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          res.setContentType("application/json");
          res.getWriter().write("{\"error\":\"sesión expirada o inválida\"}");
          return;
        }
      }
    }
    chain.doFilter(req, res);
  }
}
