<template>
  <div>
    <header style="display:flex;gap:1rem;padding:1rem;border-bottom:1px solid #ddd">
      <strong>Rapillantas Huancayo</strong>
      <nav style="display:flex;gap:1rem">
        <RouterLink to="/">Inicio</RouterLink>
        <RouterLink to="/neumaticos">Neumáticos</RouterLink>
        <RouterLink to="/refacciones">Refacciones</RouterLink>
        <RouterLink to="/servicios">Servicios</RouterLink>
        <RouterLink to="/ofertas">Ofertas</RouterLink>
        <RouterLink to="/contacto">Contacto</RouterLink>
        <RouterLink to="/admin">Admin</RouterLink>
      </nav>
    </header>
    <main style="padding:1rem"><RouterView /></main>
    <a href="https://wa.me/51999999999" target="_blank" rel="noopener"
       style="position:fixed;right:1rem;bottom:1rem;background:#25d366;color:#fff;padding:.75rem 1rem;border-radius:999px">WhatsApp</a>
  </div>
</template>
