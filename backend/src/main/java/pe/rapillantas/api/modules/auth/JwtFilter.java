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
        // Token presente pero inválido/expirado: 401 directo (no seguir como anónimo).
        SecurityContextHolder.clearContext();
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json");
        res.getWriter().write("{\"error\":\"sesión expirada o inválida\"}");
        return;
      }
    }
    chain.doFilter(req, res);
  }
}
