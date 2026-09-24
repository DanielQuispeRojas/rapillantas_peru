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
  @NotBlank @Column(nullable = false) public String telefono; // RF-20 obligatorio
  @Column(nullable = false) public boolean consentimiento = true;
  @Column(nullable = false) public int puntos = 0;
}
