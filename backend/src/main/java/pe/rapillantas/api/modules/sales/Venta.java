package pe.rapillantas.api.modules.sales;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "ventas")
public class Venta {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(name = "cliente_id") public UUID clienteId;
  @Column(name = "os_id") public UUID osId;
  @Column(nullable = false) public BigDecimal total = BigDecimal.ZERO;
  @Column(name = "forma_pago") public String formaPago;
  @Column(nullable = false) public boolean anulada = false;
  @Column(name = "created_at") public OffsetDateTime createdAt = OffsetDateTime.now();
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "venta_id")
  public List<VentaItem> items = new ArrayList<>();
}
