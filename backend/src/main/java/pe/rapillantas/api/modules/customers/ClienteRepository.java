package pe.rapillantas.api.modules.customers;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
  java.util.Optional<Cliente> findByCuenta(String cuenta);
  java.util.Optional<Cliente> findByCorreo(String correo);
}
