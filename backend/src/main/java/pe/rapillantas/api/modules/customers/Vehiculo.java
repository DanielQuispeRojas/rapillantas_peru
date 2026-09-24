package pe.rapillantas.api.modules.customers;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "vehiculos")
public class Vehiculo {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(name = "cliente_id", nullable = false) public UUID clienteId;
  @Column(nullable = false, unique = true) public String placa;
  public String marca; public String modelo; public Integer anio;
  @Column(name = "km_actual", nullable = false) public int kmActual = 0;
  @Column(name = "ultimo_servicio_km") public Integer ultimoServicioKm;
}
