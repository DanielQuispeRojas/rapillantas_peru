package pe.rapillantas.api.modules.inventory;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoImagenRepository extends JpaRepository<ProductoImagen, UUID> {
  List<ProductoImagen> findBySkuOrderByOrdenAsc(String sku);
}
