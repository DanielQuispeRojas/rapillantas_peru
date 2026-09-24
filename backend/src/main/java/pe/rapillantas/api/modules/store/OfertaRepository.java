package pe.rapillantas.api.modules.store;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfertaRepository extends JpaRepository<Oferta, UUID> {
}
