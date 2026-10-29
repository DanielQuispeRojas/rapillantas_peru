package pe.rapillantas.api.modules.store;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortadaSlideRepository extends JpaRepository<PortadaSlide, UUID> {
  java.util.List<PortadaSlide> findByActivoOrderByOrdenAsc(boolean activo);
  java.util.List<PortadaSlide> findBySliderIdAndActivoOrderByOrdenAsc(UUID sliderId, boolean activo);
  java.util.List<PortadaSlide> findBySliderIdOrderByOrdenAsc(UUID sliderId);
}
