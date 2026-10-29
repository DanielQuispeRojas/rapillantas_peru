package pe.rapillantas.api.modules.store;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "portada_slides")
public class PortadaSlide {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(name = "imagen_url", nullable = false) public String imagenUrl = "/banners/banner-1.jpg";
  @Column(nullable = false) public String titulo = "";
  @Column(nullable = false) public String subtitulo = "";
  @Column(name = "mensaje_wsp", nullable = false) public String mensajeWsp = "";
  @Column(nullable = false) public int orden = 0;
  @Column(nullable = false) public boolean activo = true;
  @Column(name = "slider_id") public UUID sliderId;
  @Column(name = "created_at", nullable = false) public OffsetDateTime createdAt = OffsetDateTime.now();
}
