package pe.rapillantas.api.modules.workshop;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TallerService {
  private final OrdenServicioRepository osRepo;

  public TallerService(OrdenServicioRepository osRepo) {
    this.osRepo = osRepo;
  }

  public List<OrdenServicio> listar() {
    return osRepo.findAll();
  }

  @Transactional
  public OrdenServicio abrir(OrdenServicio os) {
    os.estado = "abierta";
    return osRepo.save(os);
  }

  @Transactional
  public OrdenServicio cerrar(UUID id) {
    var os = osRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("OS no existe"));
    if (os.estado.equals("cerrada") || os.estado.equals("entregada")) throw new IllegalStateException("OS cerrada inmutable (RN-04)");
    os.estado = "cerrada";
    os.closedAt = OffsetDateTime.now();
    return osRepo.save(os);
  }
}
