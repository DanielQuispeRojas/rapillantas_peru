package pe.rapillantas.api.modules.loyalty;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PuntoMovimientoRepository extends JpaRepository<PuntoMovimiento, UUID> {
}
