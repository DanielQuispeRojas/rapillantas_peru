package pe.rapillantas.api.modules.purchasing;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OcRepository extends JpaRepository<Oc, UUID> {
}
