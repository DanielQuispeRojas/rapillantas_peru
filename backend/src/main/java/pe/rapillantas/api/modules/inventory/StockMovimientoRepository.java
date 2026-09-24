package pe.rapillantas.api.modules.inventory;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovimientoRepository extends JpaRepository<StockMovimiento, UUID> {
}
