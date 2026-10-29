<script setup lang="ts">
import { ref, computed } from 'vue'
import { api, waLink } from '../lib/api'
import { mMercaderias, mServicios, aMercaderia, aServicio } from '../lib/moodu'
import { useAuth } from '../stores/auth'
import { useCotizacion } from '../stores/cotizacion'
const auth = useAuth()
const cotizacion = useCotizacion()

const abierto = ref(false)
const bq = ref('')
const prods = ref<any[]>([])
const servs = ref<any[]>([])
const cargado = ref(false)
const vehiculos = ref<any[]>([])
const vehSel = ref('')
const vMarca = ref('')
const vModelo = ref('')
const vPlaca = ref('')

async function abrir() {
  abierto.value = true
  if (cargado.value) return
  // Catalogo en MOODU (el overlay de puntos no hace falta para cotizar).
  const [mm, ss] = await Promise.all([
    mMercaderias(undefined, 50).catch(() => []),
    mServicios(undefined, 50).catch(() => []),
  ])
  prods.value = mm.map(aMercaderia)
  servs.value = ss.map((s) => aServicio(s))
  if (auth.listo && auth.rol === 'CLIENTE') {
    const { data: v } = await api.get('/perfil/vehiculos').catch(() => ({ data: [] }))
    vehiculos.value = Array.isArray(v) ? v : []
  }
  cargado.value = true
}

const norm = (s: string) => s.toLowerCase()
const resultados = computed(() => {
  const t = bq.value.trim().toLowerCase()
  if (!t) return [] as any[]
  const ps = prods.value
    .filter((p: any) => norm(`${p.nombre} ${p.marca ?? ''} ${p.modelo ?? ''} ${p.medida ?? ''}`).includes(t))
    .slice(0, 5).map((p: any) => ({ tipo: 'producto', id: p.id, nombre: p.nombre, detalle: [p.marca, p.medida].filter(Boolean).join(' ') }))
  const ss = servs.value
    .filter((s: any) => norm(`${s.nombre} ${s.descripcion ?? ''}`).includes(t))
    .slice(0, 5).map((s: any) => ({ tipo: 'servicio', id: s.id, nombre: s.nombre }))
  return [...ps, ...ss]
})

const vehiculoTxt = computed(() => {
  if (vehSel.value) {
    const v = vehiculos.value.find((x: any) => x.id === vehSel.value)
    if (v) return `${v.marca} ${v.modelo} ${v.placa}`.trim()
  }
  return [vMarca.value.trim(), vModelo.value.trim(), vPlaca.value.trim().toUpperCase()].filter(Boolean).join(' ')
})

const linkWa = computed(() => waLink(cotizacion.mensaje(vehiculoTxt.value)))
</script>
<template>
  <button class="wa-float" @click="abrir()" aria-label="Cotizar">
    Cotizar
    <span v-if="cotizacion.count" class="wa-count">{{ cotizacion.count }}</span>
  </button>

  <div v-if="abierto" class="cot-overlay" @click.self="abierto = false">
    <div class="cot-panel" role="dialog" aria-modal="true" aria-label="Cotización">
      <div class="cot-head">
        <h3>Cotización<template v-if="cotizacion.count"> ({{ cotizacion.count }})</template></h3>
        <button class="btn btn-ghost btn-sm" @click="abierto = false">Cerrar ✕</button>
      </div>

      <label>Agregar servicios o productos
        <input v-model="bq" placeholder="Llanta 205, alineamiento…" />
      </label>
      <ul v-if="resultados.length" class="cot-results">
        <li v-for="r in resultados" :key="`${r.tipo}-${r.id}`">
          <span><small class="muted">{{ r.tipo === 'producto' ? 'Producto' : 'Servicio' }} · </small>{{ r.nombre }} <small class="muted">{{ r.detalle }}</small></span>
          <button v-if="!cotizacion.tiene(r.tipo, r.id)" class="btn btn-outline btn-sm" @click="cotizacion.agregar(r)">+ Agregar</button>
          <span v-else class="muted">✓</span>
        </li>
      </ul>

      <h4>Lo que quieres cotizar</h4>
      <ul v-if="cotizacion.items.length" class="cot-results">
        <li v-for="x in cotizacion.items" :key="`${x.tipo}-${x.id}`">
          <span><small class="muted">{{ x.tipo === 'producto' ? 'Producto' : 'Servicio' }} · </small>{{ x.nombre }} <small class="muted">{{ x.detalle }}</small></span>
          <button class="btn btn-ghost btn-sm" @click="cotizacion.quitar(x.tipo, x.id)">Quitar</button>
        </li>
      </ul>
      <p v-else class="muted" style="text-align:left">Vacía: usa + Cotizar en productos/servicios o el buscador de arriba.</p>

      <h4>Datos de tu vehículo</h4>
      <label v-if="vehiculos.length">Elegir registrado
        <select v-model="vehSel" style="width:100%">
          <option value="">— escribir abajo —</option>
          <option v-for="v in vehiculos" :key="v.id" :value="v.id">{{ v.placa }} · {{ v.marca }} {{ v.modelo }}</option>
        </select>
      </label>
      <div v-if="!vehSel" style="display:flex;gap:.5rem;flex-wrap:wrap">
        <label>Marca <input v-model="vMarca" placeholder="Toyota" /></label>
        <label>Modelo <input v-model="vModelo" placeholder="Hilux" /></label>
        <label>Placa <input v-model="vPlaca" placeholder="ABC-123" style="text-transform:uppercase" /></label>
      </div>

      <div class="cot-actions">
        <button v-if="cotizacion.count" class="btn btn-ghost" @click="cotizacion.limpiar()">Limpiar</button>
        <a v-if="cotizacion.count" class="btn btn-primary" :href="linkWa" target="_blank" rel="noopener">Enviar WhatsApp ›</a>
      </div>
    </div>
  </div>
</template>
<style scoped>
.wa-float { position: fixed; right: 16px; bottom: 16px; background: #25d366;
  color: #fff; padding: 12px 20px; border-radius: 999px; border: none; cursor: pointer;
  font-size: 16px; font-weight: 700; display: inline-flex; gap: 8px; align-items: center; }
.wa-float:hover { background: #1da851; }
.wa-count { min-width: 22px; height: 22px; border-radius: 999px; background: #fff;
  color: #1da851; font-size: 13px; font-weight: 800; display: inline-flex;
  align-items: center; justify-content: center; padding: 0 6px; }
.cot-overlay { position: fixed; inset: 0; background: rgba(38,38,38,.55); z-index: 60;
  display: flex; justify-content: flex-end; }
.cot-panel { background: #fff; width: min(420px, 100%); height: 100%; overflow-y: auto;
  padding: 20px 18px 28px; box-sizing: border-box; display: flex; flex-direction: column; gap: 10px; }
.cot-head { display: flex; align-items: center; justify-content: space-between; }
.cot-head h3 { margin: 0; font-weight: 300; font-size: 24px; }
.cot-panel h4 { margin: 8px 0 0; }
.cot-panel label { display: flex; flex-direction: column; gap: 4px; font-size: 14px; font-weight: 600; }
.cot-panel input, .cot-panel select { border: 1px solid var(--color-concrete); border-radius: var(--radius-s);
  padding: 10px 12px; font-size: 15px; font-family: var(--font-work); color: var(--color-carbon);
  background: #fff; width: 100%; box-sizing: border-box; }
.cot-results { list-style: none; margin: 0; padding: 0; display: flex; flex-direction: column; }
.cot-results li { display: flex; gap: 8px; align-items: center; justify-content: space-between;
  padding: 8px 0; border-bottom: 1px solid #eee; }
.cot-actions { display: flex; gap: 10px; justify-content: flex-end; margin-top: 8px; position: sticky; bottom: 0;
  background: #fff; padding-top: 10px; }
.btn-sm { padding: 6px 12px; font-size: 14px; }
</style>
