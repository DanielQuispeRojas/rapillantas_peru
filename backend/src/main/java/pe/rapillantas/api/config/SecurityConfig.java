package pe.rapillantas.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pe.rapillantas.api.modules.auth.JwtFilter;

@Configuration
public class SecurityConfig {
  private final JwtFilter jwtFilter;

  public SecurityConfig(JwtFilter jwtFilter) {
    this.jwtFilter = jwtFilter;
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // ADMIN entra a todo; el resto por panel (PANEL_*) o rol de oficio.
    String A = "ROLE_ADMIN";
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(c -> c.configurationSource(corsConfig()))
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(a -> a
            // VISITANTE (anónimo): tienda pública + auth + uploads.
            // El catalogo (productos/servicios) vive en MOODU y se consume por
            // /pub desde el frontend; aqui solo el overlay por SKU + ofertas.
            .requestMatchers("/api/v1/auth/registro", "/api/v1/auth/login",
                "/api/v1/auth/google", "/api/v1/auth/google-client-id",
                "/api/v1/tienda/**", "/api/v1/ofertas", "/api/v1/sku/**",
                "/api/v1/compatibilidad/**", "/api/v1/puntos",
                "/api/v1/canjeables",
                "/api/v1/portada", "/api/v1/empresa", "/api/v1/valoraciones",
                "/uploads/**", "/actuator/health").permitAll()
            // CLIENTE: su perfil privado + sus canjes
            .requestMatchers("/api/v1/perfil/**", "/api/v1/canjes/**").hasAnyRole("CLIENTE", "ADMIN")
            // Paneles admin (lo operativo vive en MOODU: taller, almacen, caja)
            .requestMatchers("/api/v1/admin/canjeables/**", "/api/v1/admin/puntos-sku/**").hasAnyAuthority(A, "PANEL_CANJES")
            .requestMatchers("/api/v1/admin/portada/**", "/api/v1/admin/sliders").hasAnyAuthority(A, "PANEL_PORTADA")
            .requestMatchers("/api/v1/admin/empresa").hasAnyAuthority(A, "PANEL_EMPRESA")
            .requestMatchers("/api/v1/auth/usuarios", "/api/v1/auth/usuarios/**").hasAnyAuthority(A, "PANEL_USUARIOS")
            .requestMatchers("/api/v1/admin/ofertas", "/api/v1/admin/ofertas/**").hasAnyAuthority(A, "PANEL_PORTADA")
            .requestMatchers("/api/v1/admin/valoraciones/**").hasAnyAuthority(A, "PANEL_VALORACIONES")
            .anyRequest().authenticated())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  org.springframework.web.cors.CorsConfigurationSource corsConfig() {
    var c = new org.springframework.web.cors.CorsConfiguration();
    // Orígenes del frontend (dev + prod). Ajusta por env: APP_CORS_ORIGINS coma-separado.
    String extra = System.getenv().getOrDefault("APP_CORS_ORIGINS", "");
    var origenes = new java.util.ArrayList<>(java.util.List.of("http://localhost:5173"));
    for (var o : extra.split(",")) if (!o.isBlank()) origenes.add(o.trim());
    c.setAllowedOrigins(origenes);
    c.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    c.setAllowedHeaders(java.util.List.of("*"));
    var s = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
    s.registerCorsConfiguration("/**", c);
    return s;
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
