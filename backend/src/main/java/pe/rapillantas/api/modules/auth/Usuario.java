package pe.rapillantas.api.modules.auth;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
public class Usuario {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(nullable = false) public String nombre;
  @Column(nullable = false, unique = true) public String login;
  @Column(name = "pass_hash", nullable = false) public String passHash;
  @Column(nullable = false) public String rol; // ADM_LIMA, SUPERVISOR, ADM_HYO, MECANICO
  @Column(nullable = false) public boolean activo = true;
}
