<template>
  <div class="narrow" style="text-align:center">
    <section class="shop-section">
      <p class="eyebrow">Error 500</p>
      <h1 class="display display-xl">Algo falló de nuestro lado</h1>
      <p class="lead">Intenta de nuevo en unos momentos. Si persiste, avísanos por WhatsApp.</p>
      <p style="margin-top:24px;display:flex;gap:.6rem;justify-content:center;flex-wrap:wrap">
        <button class="btn btn-primary" @click="$router.go(0)">Reintentar</button>
        <RouterLink to="/" class="btn btn-outline">Ir a la tienda</RouterLink>
      </p>
    </section>
  </div>
</template>
