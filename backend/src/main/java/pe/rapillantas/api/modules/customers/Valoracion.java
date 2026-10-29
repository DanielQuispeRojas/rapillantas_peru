package pe.rapillantas.api.modules.customers;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

// Valoración de la tienda: UNA por cliente (se edita, no se duplica).
// Máximo 3 ediciones por día; al editar sube al principio (updatedAt desc).
@Entity
@Table(name = "valoraciones")
public class Valoracion {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(name = "cliente_id", nullable = false, unique = true) public UUID clienteId;
  @Column(nullable = false) public String nombres = "";
  @Column(nullable = false) public int estrellas = 5;
  @Column(columnDefinition = "TEXT", nullable = false) public String comentario = "";
  @Column(name = "veces_editado_hoy", nullable = false) public int vecesEditadoHoy = 0;
  @Column(name = "fecha_ultima_edicion") public LocalDate fechaUltimaEdicion;
  @Column(name = "visto_admin", nullable = false) public boolean vistoAdmin = false;
  @Column(name = "created_at", nullable = false) public OffsetDateTime createdAt = OffsetDateTime.now();
  @Column(name = "updated_at", nullable = false) public OffsetDateTime updatedAt = OffsetDateTime.now();
}
