package pe.rapillantas.api.modules.store;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DescuentoRepository extends JpaRepository<Descuento, UUID> {
  List<Descuento> findByCatalogoAndActivo(String catalogo, boolean activo);
}
