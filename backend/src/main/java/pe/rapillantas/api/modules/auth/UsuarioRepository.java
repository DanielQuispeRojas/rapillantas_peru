package pe.rapillantas.api.modules.auth;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
  java.util.Optional<Usuario> findByLogin(String login);
}
