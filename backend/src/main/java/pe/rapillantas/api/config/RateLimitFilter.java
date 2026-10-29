package pe.rapillantas.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// Límite de solicitudes por IP (ventana deslizante de 1 min, en memoria):
// - general /api/**: 120/min por IP
// - auth sensible (login/registro/google): 10/min por IP
// Al exceder responde 429 + Retry-After sin llegar a los controladores.
@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {
  private static final long VENTANA_MS = 60_000L;
  private static final int LIMITE_API = 120;
  private static final int LIMITE_AUTH = 10;
  private final ConcurrentHashMap<String, Deque<Long>> ventanas = new ConcurrentHashMap<>();

  @Override
  protected boolean shouldNotFilter(HttpServletRequest req) {
    return !req.getRequestURI().startsWith("/api/");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String path = req.getRequestURI();
    boolean sensible = path.startsWith("/api/v1/auth/login")
        || path.startsWith("/api/v1/auth/registro")
        || path.startsWith("/api/v1/auth/google");
    int limite = sensible ? LIMITE_AUTH : LIMITE_API;
    String xf = req.getHeader("X-Forwarded-For");
    String ip = xf != null && !xf.isBlank() ? xf.split(",")[0].trim() : req.getRemoteAddr();
    String clave = ip + "|" + (sensible ? "auth" : "api");
    long ahora = System.currentTimeMillis();
    Deque<Long> q = ventanas.computeIfAbsent(clave, k -> new ArrayDeque<>());
    boolean excede;
    synchronized (q) {
      while (!q.isEmpty() && ahora - q.peekFirst() > VENTANA_MS) q.pollFirst();
      excede = q.size() >= limite;
      if (!excede) q.addLast(ahora);
    }
    if (ventanas.size() > 20000) ventanas.clear(); // purga simple anti-memoria
    if (excede) {
      res.setStatus(429);
      res.setHeader("Retry-After", "60");
      res.setContentType("application/json");
      res.getWriter().write("{\"error\":\"demasiadas solicitudes, intenta en un minuto\"}");
      return;
    }
    chain.doFilter(req, res);
  }
}
