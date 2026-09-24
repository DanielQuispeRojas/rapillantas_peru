package pe.rapillantas.api.modules.workshop;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenServicioRepository extends JpaRepository<OrdenServicio, UUID> {
}
