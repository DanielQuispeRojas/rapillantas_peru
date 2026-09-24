<template>
  <div style="display:flex;min-height:100vh">
    <aside style="width:220px;padding:1rem;border-right:1px solid #ddd">
      <strong>Admin Rapillantas</strong>
      <nav style="display:flex;flex-direction:column;gap:.5rem;margin-top:1rem">
        <RouterLink to="/admin">Dashboard</RouterLink>
        <RouterLink to="/admin/productos">Productos/Stock</RouterLink>
        <RouterLink to="/admin/ocs">Compras OC</RouterLink>
        <RouterLink to="/admin/ventas">Ventas</RouterLink>
        <RouterLink to="/admin/os">Taller OS</RouterLink>
        <RouterLink to="/admin/ofertas">Ofertas</RouterLink>
        <RouterLink to="/">Ver tienda</RouterLink>
      </nav>
    </aside>
    <main style="flex:1;padding:1rem"><RouterView /></main>
  </div>
</template>
