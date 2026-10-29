package pe.rapillantas.api.modules.store;

import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmpresaController {
  private final EmpresaConfigRepository repo;

  public EmpresaController(EmpresaConfigRepository repo) {
    this.repo = repo;
  }

  private EmpresaConfig actual() {
    return repo.findAll().stream().findFirst().orElseGet(() -> repo.save(new EmpresaConfig()));
  }

  /** Público: WhatsApp, dirección y correo que consumen los botones de la tienda. */
  @GetMapping("/api/v1/empresa")
  public EmpresaConfig publica() {
    return actual();
  }

  /** Admin: actualiza los datos de la empresa. */
  @PutMapping("/api/v1/admin/empresa")
  public EmpresaConfig guardar(@RequestBody Map<String, String> b) {
    var e = actual();
    if (b.get("whatsapp") != null) e.whatsapp = b.get("whatsapp").replaceAll("[^0-9]", "");
    if (b.get("direccion") != null) e.direccion = b.get("direccion");
    if (b.get("correo") != null) e.correo = b.get("correo");
    if (b.get("ruc") != null) e.ruc = b.get("ruc").replaceAll("[^0-9]", "");
    if (b.get("facebook") != null) e.facebook = b.get("facebook").trim();
    if (b.get("instagram") != null) e.instagram = b.get("instagram").trim();
    if (b.get("tiktok") != null) e.tiktok = b.get("tiktok").trim();
    if (b.get("tallerAbierto") != null) e.tallerAbierto = Boolean.parseBoolean(b.get("tallerAbierto"));
    if (b.get("avisoTaller") != null) e.avisoTaller = b.get("avisoTaller").trim();
    for (var d : new String[]{"Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom"}) {
      String k = "horario" + d;
      if (b.get(k) != null) {
        String v = b.get(k).trim();
        if (d.equals("Lun")) e.horarioLun = v; else if (d.equals("Mar")) e.horarioMar = v;
        else if (d.equals("Mie")) e.horarioMie = v; else if (d.equals("Jue")) e.horarioJue = v;
        else if (d.equals("Vie")) e.horarioVie = v; else if (d.equals("Sab")) e.horarioSab = v;
        else e.horarioDom = v;
      }
    }
    e.updatedAt = java.time.OffsetDateTime.now();
    return repo.save(e);
  }
}
