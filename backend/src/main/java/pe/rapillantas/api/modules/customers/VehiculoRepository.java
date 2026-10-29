package pe.rapillantas.api.modules.customers;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehiculoRepository extends JpaRepository<Vehiculo, UUID> {
  java.util.Optional<Vehiculo> findByPlacaIgnoreCase(String placa);
}
