package pe.rapillantas.api.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Solo desarrollo (perfil distinto de prod): inserta datos base idempotentes
 * del SITIO (sliders, categorias de gasto, admin). El catalogo (productos,
 * servicios), las OC y las OS viven en MOODU y se consumen por /pub
 * (ver lib/moodu.ts): este seed ya no crea nada de eso.
 * En prod se usa validate + migraciones SQL (db/).
 */
@Configuration
@Profile("!prod")
public class DevDataSeeder {

  @Bean
  CommandLineRunner seed(org.springframework.context.ApplicationContext ctx) {
    return args -> {
      // Slider de recomendaciones del taller (Inicio + Mi perfil; se edita en Admin — Portada)
      var sliders = ctx.getBean(pe.rapillantas.api.modules.store.SliderRepository.class);
      var slides = ctx.getBean(pe.rapillantas.api.modules.store.PortadaSlideRepository.class);
      var reco = sliders.findByClave("recomendaciones").orElseGet(() -> {
        var n = new pe.rapillantas.api.modules.store.Slider();
        n.clave = "recomendaciones"; n.nombre = "Recomendaciones del taller";
        return sliders.save(n);
      });
      if (slides.findBySliderIdOrderByOrdenAsc(reco.id).isEmpty()) {
        String[][] tips = {
            {"Presion cada 15 dias", "Rodar bajo inflama el consumo y desgasta los bordes. Revisa la presion en frio segun tu medida."},
            {"Rotacion cada 10.000 km", "Rota las llantas para un desgaste parejo y alarga su vida util hasta un 20%."},
            {"Alineamiento anual", "Si el timon jala a un lado o el desgaste es disparejo, toca alinear."},
            {"Frenos sin chillidos", "Chillido o vibracion al frenar: pastillas al limite. No esperes al metal contra metal."},
            {"Aceite a tiempo", "Respeta el kilometraje del cambio de aceite; el motor lo agradece en consumo y potencia."},
            {"Luces y bateria", "Antes de un viaje largo revisa luces, bateria y llanta de repuesto."},
        };
        for (int i = 0; i < tips.length; i++) {
          var s = new pe.rapillantas.api.modules.store.PortadaSlide();
          s.sliderId = reco.id; s.titulo = tips[i][0]; s.subtitulo = tips[i][1];
          s.imagenUrl = ""; s.mensajeWsp = ""; s.orden = i; s.activo = true;
          slides.save(s);
        }
      }
      // ADMIN inicial dev: login/clave por env (defaults solo para dev local).
      // En prod el perfil "!prod" desactiva este seed; el primer admin se crea
      // manualmente o via setup. Nunca commitees una clave real aqui.
      var adminLogin = System.getenv().getOrDefault("ADMIN_LOGIN", "admin");
      var adminPass  = System.getenv().getOrDefault("ADMIN_PASSWORD", "admin123");
      var adminNombre = System.getenv().getOrDefault("ADMIN_NOMBRE", "Administrador");
      var users = ctx.getBean(pe.rapillantas.api.modules.auth.UsuarioRepository.class);
      if (users.findByLogin(adminLogin).isEmpty()) {
        var a = new pe.rapillantas.api.modules.auth.Usuario();
        a.nombre = adminNombre;
        a.login = adminLogin;
        a.passHash = ctx.getBean(org.springframework.security.crypto.password.PasswordEncoder.class).encode(adminPass);
        a.rol = "ADMIN";
        users.save(a);
      }
    };
  }
}
