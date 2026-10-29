<script setup lang="ts">
import { cargarEmpresa, empresa } from '../../lib/api'
import { onMounted } from 'vue'
onMounted(cargarEmpresa)
</script>
<template>
  <div class="narrow left">
    <section class="shop-section">
      <p class="eyebrow">Legal</p>
      <h1 class="display display-md">Términos y condiciones</h1>
      <p class="lead">Compra informada en Rapillantas Huancayo.</p>
    </section>
    <section class="shop-section">
      <h2>1. Precios y cotizaciones</h2>
      <p>Los precios publicados son referenciales y pueden variar sin previo aviso. La cotización válida es la confirmada por WhatsApp al momento de la compra.</p>
      <h2>2. Comprobantes</h2>
      <p>Emitimos boleta o factura según lo solicitado. Si necesitas factura, indícanos tu RUC antes de pagar.</p>
      <h2>3. Garantías</h2>
      <p>Los neumáticos y refacciones cuentan con garantía contra defectos de fabricación según las condiciones del fabricante. No cubre desgaste normal, mal uso ni daños por instalación externa.</p>
      <h2>4. Servicios de taller</h2>
      <p>Todo servicio se registra en una orden de servicio (OS) con diagnóstico y costo acordado antes de ejecutarse. Ningún trabajo adicional se realiza sin tu autorización.</p>
      <h2>5. Datos personales que recogemos</h2>
      <p>Al registrarte nos das: nombres, apellidos, correo, teléfono, nombre de usuario y clave. Si usas "Regístrate con Google", recibimos de Google tu nombre y correo verificado. Además guardamos los datos de tus vehículos (placa, marca, modelo, año, kilometraje) para tus expedientes, y el detalle de tus atenciones, compras y comprobantes.</p>
      <h2>6. Para qué usamos tus datos</h2>
      <p>Solo para operar el servicio: identificarte, dar seguimiento a tus OS, recordatorios de mantenimiento (cada 10 000 km), emitir comprobantes y mostrarte tu historial. No vendemos ni cedemos tu información con fines publicitarios.</p>
      <h2>7. Cómo protegemos tus datos</h2>
      <p>Tu clave nunca se guarda en texto: se almacena como hash irreversible (BCrypt) y jamás se expone en la plataforma. La sesión usa un token temporal que caduca, y el acceso a taller, caja y administración está restringido por roles y permisos. Aplicamos límites de intentos contra accesos automatizados y validamos los datos de entrada.</p>
      <h2>8. Servicios de terceros</h2>
      <p>Usamos dos servicios externos que reciben datos cuando los utilizas: <strong>Google Identity Services</strong> (accounts.google.com), solo si eliges registrarte con Google —le envías tu credencial de Google y nos devuelve tu nombre y correo verificado—; y <strong>WhatsApp (Meta)</strong>, cuando pulsas nuestros botones de contacto o cotización: se abre WhatsApp con tu número y el mensaje que envíes queda sujeto a sus políticas. No usamos analítica ni publicidad de terceros en este sitio. Los datos de sesión (token, rol) se guardan solo en tu propio navegador.</p>
      <h2>9. Tus derechos</h2>
      <p>Conforme a la Ley N.º 29733 de Protección de Datos Personales del Perú, puedes pedirnos acceso, rectificación, cancelación u oposición sobre tus datos escribiendo a {{ empresa.correo }}. Atenderemos tu solicitud y, de corresponder, eliminaremos tu cuenta y datos asociados.</p>
      <h2>10. Desarrollo del sitio</h2>
      <p>Este sitio fue desarrollado con la asistencia de herramientas de inteligencia artificial, bajo supervisión, revisión y responsabilidad del equipo de Rapillantas. El contenido, los precios y las condiciones aquí publicados son definidos por nosotros.</p>
      <h2>11. Contacto</h2>
      <p>{{ empresa.direccion }} · {{ empresa.correo }}<template v-if="empresa.ruc"> · RUC {{ empresa.ruc }}</template></p>
    </section>
  </div>
</template>
