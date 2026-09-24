package pe.rapillantas.api.modules.purchasing;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "ocs")
public class Oc {
  @Id
  public UUID id = UUID.randomUUID();
  @Column(name = "proveedor_id", nullable = false) public UUID proveedorId;
  @Column(nullable = false) public String estado = "borrador";
  @Column(nullable = false) public LocalDate fecha = LocalDate.now();
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "oc_id")
  public List<OcItem> items = new ArrayList<>();
}
