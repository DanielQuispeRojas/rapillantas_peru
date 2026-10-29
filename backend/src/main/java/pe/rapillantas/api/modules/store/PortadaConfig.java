package pe.rapillantas.api.modules.store;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "portada_config")
public class PortadaConfig {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(name = "imagen_url", nullable = false) public String imagenUrl = "/banners/banner-1.jpg";
  @Column(nullable = false) public String titulo = "Promo de la semana";
  @Column(nullable = false) public String subtitulo = "Consúltanos por WhatsApp";
  @Column(name = "mensaje_wsp", nullable = false) public String mensajeWsp = "Hola, me interesa la oferta";
  @Column(name = "updated_at", nullable = false) public OffsetDateTime updatedAt = OffsetDateTime.now();
}
