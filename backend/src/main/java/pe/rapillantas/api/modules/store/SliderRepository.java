package pe.rapillantas.api.modules.store;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SliderRepository extends JpaRepository<Slider, UUID> {
  java.util.Optional<Slider> findByClave(String clave);
}
