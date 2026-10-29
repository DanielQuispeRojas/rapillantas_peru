package pe.rapillantas.api.modules.customers;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
@Table(name = "clientes")
public class Cliente {
  @Id
  public UUID id = UUID.randomUUID();
  @NotBlank @Column(nullable = false) public String nombres;
  @Column(nullable = false) public String apellidos = "";
  // cuenta web: solo usuarios registrados en el sitio pueden tener expediente técnico
  @Column(unique = true) public String cuenta;
  @Column(unique = true) public String correo;
  @NotBlank @Column(nullable = false) public String telefono; // RF-20 obligatorio
  @Column(nullable = false) public boolean consentimiento = true;
  @Column(nullable = false) public int puntos = 0;
}
