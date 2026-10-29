package pe.rapillantas.api.modules.store;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "empresa_config")
public class EmpresaConfig {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(nullable = false) public String whatsapp = "51999999999";
  @Column(nullable = false) public String direccion = "Huancayo, Perú";
  @Column(nullable = false) public String correo = "contacto@rapillantas.pe";
  @Column(nullable = false) public String ruc = "";
  @Column(nullable = false) public String facebook = "";
  @Column(nullable = false) public String instagram = "";
  @Column(nullable = false) public String tiktok = "";
  // Estado del taller (banner de la tienda): abierto/cerrado + anuncio (ej. "sábado sin atención").
  @Column(name = "taller_abierto", nullable = false) public boolean tallerAbierto = true;
  @Column(name = "aviso_taller", nullable = false) public String avisoTaller = "";
  // Horario por día ("8:00–18:00" o "cerrado"): se muestra al pasar el mouse sobre el estado.
  @Column(name = "horario_lun", nullable = false) public String horarioLun = "8:00–18:00";
  @Column(name = "horario_mar", nullable = false) public String horarioMar = "8:00–18:00";
  @Column(name = "horario_mie", nullable = false) public String horarioMie = "8:00–18:00";
  @Column(name = "horario_jue", nullable = false) public String horarioJue = "8:00–18:00";
  @Column(name = "horario_vie", nullable = false) public String horarioVie = "8:00–18:00";
  @Column(name = "horario_sab", nullable = false) public String horarioSab = "8:00–13:00";
  @Column(name = "horario_dom", nullable = false) public String horarioDom = "cerrado";
  @Column(name = "updated_at", nullable = false) public OffsetDateTime updatedAt = OffsetDateTime.now();
}
