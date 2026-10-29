package pe.rapillantas.api.modules.customers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValoracionRepository extends JpaRepository<Valoracion, UUID> {
  Optional<Valoracion> findByClienteId(UUID clienteId);
  List<Valoracion> findAllByOrderByUpdatedAtDesc();
}
