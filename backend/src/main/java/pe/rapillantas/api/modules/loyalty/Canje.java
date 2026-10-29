package pe.rapillantas.api.modules.loyalty;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

// Canje del cliente: queda por_recoger hasta que administración confirma el despacho.
@Entity
@Table(name = "canjes")
public class Canje {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(name = "cliente_id", nullable = false) public UUID clienteId;
  @Column(name = "canjeable_id") public UUID canjeableId;
  // Snapshot del nombre (si el canjeable se edita/elimina, el historial no cambia)
  @Column(name = "canjeable_nombre", nullable = false) public String canjeableNombre = "";
  @Column(name = "puntos_usados", nullable = false) public int puntosUsados = 0;
  // por_recoger | despachado | cancelado | rechazado | expirado
  @Column(nullable = false) public String estado = "por_recoger";
  @Column(name = "created_at", nullable = false) public OffsetDateTime createdAt = OffsetDateTime.now();
  @Column(name = "despachado_at") public OffsetDateTime despachadoAt;
  // Plazo de recojo: 3 días desde el canje; vencido se devuelven los puntos.
  @Column(name = "expira_at") public OffsetDateTime expiraAt;
  // Nota visible al cliente (ej. motivo de rechazo o aviso de expiración).
  @Column(name = "motivo_nota") public String motivoNota;
}
