package pe.rapillantas.api.modules.store;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioRepository extends JpaRepository<Servicio, UUID> {
}
