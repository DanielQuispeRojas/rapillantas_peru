package pe.rapillantas.api.modules.loyalty;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CanjeRepository extends JpaRepository<Canje, UUID> {
  List<Canje> findByClienteIdOrderByCreatedAtDesc(UUID clienteId);
}
