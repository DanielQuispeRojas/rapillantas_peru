package pe.rapillantas.api.modules.inventory;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoCompatibilidadRepository extends JpaRepository<ProductoCompatibilidad, UUID> {
  List<ProductoCompatibilidad> findBySku(String sku);
  List<ProductoCompatibilidad> findByMarcaIgnoreCaseAndModeloIgnoreCase(String marca, String modelo);
}
