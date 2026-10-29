<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { api } from '../../lib/api'
import { mExpediente } from '../../lib/moodu'
import { useSubmit } from '../../composables/useSubmit'
import { useRecomendaciones } from '../../composables/useRecomendaciones'
const g = useSubmit()

// Perfil del CLIENTE con índice lateral: Mis datos · Mis vehículos.
// El historial de cada vehículo se muestra inline al pulsar "Ver historial"
// en su tarjeta (el cliente puede tener varios vehículos).
const datos = ref<any>(null)
const canjes = ref<any[]>([])
const mival = ref<any>(null)
const errCarga = ref('')
const seccion = ref<'datos' | 'vehiculos'>('datos')
function ir(s: typeof seccion.value) {
  seccion.value = s
  nextTick(() => document.getElementById(`perf-${s}`)?.scrollIntoView({ behavior: 'smooth', block: 'start' }))
}

// Edición por campo: cada fila despliega su valor con un lápiz que abre
// la edición inline dentro del mismo contenedor (sin modal externo).
const campoEnEdicion = ref<string | null>(null)
const fCampo = ref('')
const campoMsg = ref('')
const CAMPOS = [
  { key: 'nombres', label: 'Nombres', type: 'text', req: true, edit: true },
  { key: 'apellidos', label: 'Apellidos', type: 'text', req: false, edit: true },
  { key: 'telefono', label: 'Teléfono', type: 'tel', req: true, edit: true },
  { key: 'correo', label: 'Correo', type: 'email', req: true, edit: true },
  { key: 'cuenta', label: 'Usuario', type: 'text', req: false, edit: false },
  { key: 'puntos', label: 'Puntos', type: 'text', req: false, edit: false },
] as const
function valorCampo(key: string) {
  const c = datos.value?.cliente ?? {}
  if (key === 'puntos') return `${c.puntos ?? 0} pts`
  return c[key] ?? '—'
}
function iniciarEdicion(key: string) {
  campoMsg.value = ''
  fCampo.value = datos.value?.cliente?.[key] ?? ''
  campoEnEdicion.value = key
}
function cancelarEdicion() {
  campoEnEdicion.value = null
  campoMsg.value = ''
}
async function guardarCampo(key: string) {
  campoMsg.value = ''
  const v = fCampo.value.trim()
  const meta = CAMPOS.find((c) => c.key === key)
  if (meta?.req && !v) { campoMsg.value = 'Este campo es obligatorio.'; return }
  if (key === 'correo' && v && !/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(v.toLowerCase())) {
    campoMsg.value = 'Correo inválido.'
    return
  }
  await g.ejecutar(async () => {
    await api.put('/perfil', { [key]: key === 'correo' ? v.toLowerCase() : v }).catch((e: any) => {
      campoMsg.value = e?.response?.data?.error ?? e?.response?.data?.message ?? 'No se pudo guardar.'
      throw e
    })
    campoEnEdicion.value = null
    await cargar()
  })
}

// — Esqueleto del slider lateral (panel derecho) —
// TODO: reemplaza `sliderDemo` y el bloque `.slider-shell` por tu slider real.
// La estructura (viewport + flechas + dots) ya queda lista para reutilizar.
const sliderIdx = ref(0)
const sliderDemo = ref([
  { titulo: 'Espacio reservado 1', texto: 'Aquí irá tu primer slide.' },
  { titulo: 'Espacio reservado 2', texto: 'Aquí irá tu segundo slide.' },
  { titulo: 'Espacio reservado 3', texto: 'Aquí irá tu tercer slide.' },
])
function moverSlider(d: number) {
  const n = sliderDemo.value.length
  sliderIdx.value = (sliderIdx.value + d + n) % n
}

const showVeh = ref(false)
const fveh = ref({ placa: '', marca: '', modelo: '', anio: null as any, kmActual: 0, kmInicial: null as any })
const vmsg = ref('')
async function guardarVehiculo() {
  vmsg.value = ''
  await api.post('/perfil/vehiculos', {
    placa: fveh.value.placa, marca: fveh.value.marca, modelo: fveh.value.modelo,
    anio: fveh.value.anio ? Number(fveh.value.anio) : null,
    kmActual: Number(fveh.value.kmActual) || 0,
    kmInicial: fveh.value.kmInicial !== null && fveh.value.kmInicial !== '' ? Number(fveh.value.kmInicial) : null,
  }).catch((e: any) => {
    vmsg.value = e?.response?.data?.error ?? e?.response?.data?.message ?? 'No se pudo registrar.'
    throw e
  })
  fveh.value = { placa: '', marca: '', modelo: '', anio: null, kmActual: 0, kmInicial: null }
  showVeh.value = false
  await cargar()
}

// Historial por vehículo: se muestra inline debajo de la tarjeta
// al pulsar "Ver historial" (un vehículo a la vez).
const selPlaca = ref('')
const exp = ref<any>(null)
const hmsg = ref('')
async function verHistorial(placa: string) {
  hmsg.value = ''
  // toggle: si ya está abierto, ciérralo
  if (selPlaca.value === placa && exp.value) { selPlaca.value = ''; exp.value = null; return }
  exp.value = null
  selPlaca.value = placa
  // Ficha en MOODU por placa (sin datos de cliente). Sin expediente: el taller
  // lo abre en tu primera visita.
  const ficha = await mExpediente(placa)
  if (!ficha) { hmsg.value = 'Sin expediente para esa placa (se abre en tu primera visita al taller).'; return }
  exp.value = { vehiculo: ficha }
  nextTick(() => document.getElementById('veh-historial')?.scrollIntoView({ behavior: 'smooth', block: 'start' }))
}

// Recomendaciones (mismo slider de la tienda; se edita en Admin → Portada).
// Ahora viven debajo de todo, en carrusel horizontal.
const { tips } = useRecomendaciones()
const selTip = ref(0)
const tipRefs = ref<HTMLElement[]>([])
function centrarTip(i: number) {
  tipRefs.value[i]?.scrollIntoView({ behavior: 'smooth', inline: 'center', block: 'nearest' })
}
function elegirTip(i: number) { selTip.value = i; centrarTip(i) }
function moverTip(d: number) {
  if (!tips.value.length) return
  const n = (selTip.value + d + tips.value.length) % tips.value.length
  elegirTip(n)
}

async function cargar() {
  errCarga.value = ''
  const { data } = await api.get('/perfil').catch(() => ({ data: null }))
  datos.value = data
  if (!data) { errCarga.value = 'Tu cuenta no tiene ficha de cliente (solo clientes registrados la tienen).'; return }
  const { data: cj } = await api.get('/canjes/mios').catch(() => ({ data: [] }))
  canjes.value = cj ?? []
  const { data: mv } = await api.get('/perfil/valoracion').catch(() => ({ data: null }))
  mival.value = mv
  if (data.vehiculos?.length && !selPlaca.value) {
    const placa0 = data.vehiculos[0].placa
    const e0 = await mExpediente(placa0).catch(() => null)
    if (e0) { selPlaca.value = placa0; exp.value = { vehiculo: e0 } }
  }
}
onMounted(cargar)
const inicial = (c: any) => ((c?.nombres ?? '?').trim().charAt(0) || '?').toUpperCase()
const estrellasTxt = (n: number) => '★'.repeat(n ?? 0) + '☆'.repeat(5 - (n ?? 0))
const porRecoger = (cajes: any[]) => cajes.filter((c: any) => c.estado === 'por_recoger')
const etiquetaCanje = (e: string) => e === 'por_recoger' ? 'por recoger · 3 días'
  : e === 'rechazado' ? 'rechazado · puntos devueltos'
  : e === 'expirado' ? 'expirado · puntos devueltos' : e
</script>
<template>
  <div>
    <div class="perf-layout">
      <aside class="perf-aside">
        <p class="eyebrow">Mi cuenta</p>
        <nav>
          <button :class="{ on: seccion === 'datos' }" @click="ir('datos')">Mis datos</button>
          <button :class="{ on: seccion === 'vehiculos' }" @click="ir('vehiculos')">Mis vehículos<template v-if="datos"> ({{ datos.vehiculos.length }})</template></button>
        </nav>
      </aside>

      <div class="perf-body">
        <p v-if="errCarga" class="form-error" role="alert">{{ errCarga }}</p>
        <template v-if="datos">
          <section v-if="seccion === 'datos'" id="perf-datos" class="shop-section">
            <div class="perf-card">
              <div class="perf-avatar" aria-hidden="true">{{ inicial(datos.cliente) }}</div>
              <div class="perf-main">
                <h2>{{ datos.cliente.nombres }} {{ datos.cliente.apellidos }}</h2>
                <p class="muted" style="text-align:left;margin:0">@{{ datos.cliente.cuenta || '—' }}</p>
                <p class="muted" style="text-align:left;margin:0">{{ datos.cliente.telefono }} · {{ datos.cliente.correo }}</p>
              </div>
              <div class="perf-side">
                <span class="pts-badge" title="Puntos acumulados por tus compras">★ {{ datos.cliente.puntos }} pts</span>
              </div>
            </div>

            <div class="datos-card">
              <div class="datos-head">
                <h3>Mis datos</h3>
                <p class="muted">Toca el lápiz de cada campo para editarlo sin salir de aquí.</p>
              </div>
              <ul class="datos-fields">
                <li v-for="c in CAMPOS" :key="c.key" class="campo-row">
                  <div class="campo-info">
                    <span class="campo-label">{{ c.label }}</span>
                    <template v-if="campoEnEdicion !== c.key">
                      <span class="campo-valor">{{ valorCampo(c.key) }}</span>
                    </template>
                    <template v-else>
                      <div class="campo-edit">
                        <input :type="c.type" v-model="fCampo" class="inp" :aria-label="c.label"
                          :required="c.req" @keyup.enter="guardarCampo(c.key)" @keyup.escape="cancelarEdicion" />
                        <div class="campo-actions">
                          <button class="btn btn-primary btn-sm" :disabled="g.ocupado.value"
                            @click="guardarCampo(c.key)">Guardar</button>
                          <button class="btn btn-ghost btn-sm" type="button" @click="cancelarEdicion">Cancelar</button>
                        </div>
                        <p v-if="campoMsg" class="form-error" role="alert">{{ campoMsg }}</p>
                      </div>
                    </template>
                  </div>
                  <button v-if="c.edit && campoEnEdicion !== c.key" class="icon-pencil"
                    :aria-label="`Editar ${c.label}`" title="Editar" @click="iniciarEdicion(c.key)">✎</button>
                  <span v-else-if="!c.edit" class="campo-lock" title="Solo lectura">—</span>
                </li>
              </ul>
            </div>

            <div v-if="mival" class="val-card" style="margin-top:12px">
              <p class="eyebrow">Mi última reseña</p>
              <p class="val-head"><span class="stars-static">{{ estrellasTxt(mival.estrellas) }}</span>
                <small class="muted">· {{ String(mival.updatedAt ?? mival.createdAt ?? '').slice(0, 10) }}</small></p>
              <p>{{ mival.comentario }}</p>
            </div>

            <h3 style="margin-top:24px">Mis canjes</h3>
            <div v-if="porRecoger(canjes).length" class="canje-note">
              <strong>Tienes {{ porRecoger(canjes).length }} canje(s) por recoger en tienda (3 días de plazo).</strong>
            </div>
            <ul v-if="canjes.length" class="canje-list">
              <li v-for="c in canjes" :key="c.id">
                <strong>{{ c.canjeableNombre }}</strong> · {{ c.puntosUsados }} pts ·
                <span class="canje-est" :class="`ce-${c.estado}`">{{ etiquetaCanje(c.estado) }}</span>
                <small class="muted"> · {{ String(c.createdAt ?? '').slice(0, 10) }}</small>
                <small v-if="c.estado === 'por_recoger' && c.expiraAt" class="muted"> · vence {{ String(c.expiraAt).slice(0, 10) }}</small>
                <p v-if="c.estado === 'expirado'" class="canje-nota">No viniste a reclamar tu canje, te devolvimos tus puntos para cuando quieras reclamar otro ítem.</p>
                <p v-else-if="c.motivoNota" class="canje-nota">{{ c.motivoNota }}</p>
              </li>
            </ul>
            <p v-else class="muted">Sin canjes. Acumula puntos con tus compras y canjéalos en <a href="/ofertas">Ofertas</a>.</p>
          </section>

          <section v-if="seccion === 'vehiculos'" id="perf-vehiculos" class="shop-section">
            <div class="title-row">
              <button class="btn btn-primary" style="margin-left:auto" @click="showVeh = !showVeh">{{ showVeh ? 'Cerrar' : '+ Agregar vehículo' }}</button>
            </div>
            <div v-if="showVeh" class="perf-form">
              <label>Placa * <input v-model="fveh.placa" placeholder="ABC-123" autocapitalize="characters" /></label>
              <label>Marca * <input v-model="fveh.marca" placeholder="Toyota" /></label>
              <label>Modelo * <input v-model="fveh.modelo" placeholder="Hilux" /></label>
              <label>Año <input v-model.number="fveh.anio" type="number" placeholder="2020" /></label>
              <label>Km actual <input v-model.number="fveh.kmActual" type="number" min="0" /></label>
              <label>Km inicial 10K (opcional) <input v-model.number="fveh.kmInicial" type="number" min="0" placeholder="Base del aviso" /></label>
              <div style="flex-basis:100%;display:flex;gap:.5rem">
                <button class="btn btn-primary" @click="g.ejecutar(guardarVehiculo)" :disabled="g.ocupado.value">Registrar</button>
              </div>
              <p v-if="vmsg" class="form-error" role="alert" style="flex-basis:100%">{{ vmsg }}</p>
            </div>
            <div v-if="datos.vehiculos.length" class="veh-grid">
              <div v-for="v in datos.vehiculos" :key="v.id" class="veh-card" :class="{ sel: selPlaca === v.placa }">
                <strong>{{ v.placa }}</strong>
                <span>{{ v.marca }} {{ v.modelo }}<template v-if="v.anio"> · {{ v.anio }}</template></span>
                <small class="muted">{{ v.kmActual }} km</small>
                <button class="btn btn-outline btn-sm" @click="verHistorial(v.placa)">{{ selPlaca === v.placa && exp ? 'Ocultar historial ‹' : 'Ver historial ›' }}</button>
              </div>
            </div>
            <p v-else class="muted">Aún no registras vehículos.</p>

            <div v-if="selPlaca" id="veh-historial" class="veh-historial">
              <div class="title-row" style="margin-top:12px">
                <h3 style="margin:0">Historial — {{ selPlaca }}</h3>
                <button class="btn btn-ghost btn-sm" style="margin-left:auto" @click="selPlaca = ''; exp = null">Cerrar ✕</button>
              </div>
              <p v-if="hmsg" role="status">{{ hmsg }}</p>
              <div v-if="exp">
                <p class="muted" style="text-align:left;margin:4px 0">{{ exp.vehiculo.marca }} {{ exp.vehiculo.modelo }}<template v-if="exp.vehiculo.anioModelo"> · {{ exp.vehiculo.anioModelo }}</template><template v-if="exp.vehiculo.color"> · {{ exp.vehiculo.color }}</template></p>
                <p class="muted" style="text-align:left">Tu historial detallado de atenciones lo ves en el taller (MOODU).</p>
              </div>
            </div>
          </section>
        </template>
        <p v-else-if="!errCarga">Cargando…</p>
      </div>

      <aside class="perf-slider" aria-label="Slider lateral">
        <!-- ESQUELETO: reemplaza `sliderDemo` + `.slider-shell` por tu slider real aquí. -->
        <p class="eyebrow">Destacados</p>
        <h3>Slider</h3>
        <p class="muted slider-hint"><small>Esqueleto listo para configurar: cambia las slides y la navegación a gusto.</small></p>
        <div class="slider-shell">
          <div class="slider-viewport">
            <div v-for="(s, i) in sliderDemo" :key="i" class="slider-slide" :class="{ on: i === sliderIdx }">
              <div class="slider-ph" aria-hidden="true">🖼</div>
              <h4>{{ s.titulo }}</h4>
              <p>{{ s.texto }}</p>
            </div>
          </div>
          <div class="slider-nav">
            <button type="button" aria-label="Anterior" @click="moverSlider(-1)">‹</button>
            <button type="button" aria-label="Siguiente" @click="moverSlider(1)">›</button>
          </div>
          <div class="slider-dots">
            <button v-for="(s, i) in sliderDemo" :key="i" :class="{ on: i === sliderIdx }"
              :aria-label="`Slide ${i + 1}`" @click="sliderIdx = i"></button>
          </div>
        </div>
      </aside>
    </div>

    <section class="perf-tips shop-section" aria-label="Recomendaciones">
      <div class="wrap" style="text-align:center">
        <p class="eyebrow">Cuida tu vehículo</p>
        <h2 class="display display-md">Recomendaciones</h2>
        <p class="lead">Salud básica para tu vehículo, directo del taller.</p>
      </div>
      <div class="tips-nav">
        <button @click="moverTip(-1)" aria-label="Anterior">‹</button>
        <button @click="moverTip(1)" aria-label="Siguiente">›</button>
      </div>
      <div class="tips-row">
        <div v-for="(tip, i) in tips" :key="i" :ref="(el) => tipRefs[i] = el as HTMLElement"
          class="tip-card" :class="{ sel: i === selTip, dim: i !== selTip }"
          role="button" tabindex="0" :aria-label="tip.t"
          @click="elegirTip(i)" @keydown.enter="elegirTip(i)" @keydown.space.prevent="elegirTip(i)">
          <img v-if="tip.img" :src="tip.img" :alt="tip.t" loading="lazy" />
          <span class="tip-num">{{ i + 1 }}</span>
          <h3>{{ tip.t }}</h3>
          <p>{{ tip.d }}</p>
        </div>
      </div>
    </section>
  </div>
</template>
<style scoped>
.perf-layout { display: grid; grid-template-columns: 220px minmax(0, 1fr) 300px; gap: 24px; padding: 24px 16px 0; align-items: start; }
.perf-aside { position: sticky; top: 16px; align-self: start; display: flex; flex-direction: column; gap: 12px;
  border: 1px solid var(--color-line, #e5e5e5); border-radius: 14px; padding: 16px; background: var(--color-card, #fff); }
.perf-aside nav { display: flex; flex-direction: column; gap: 4px; }
.perf-aside nav button { text-align: left; background: none; border: none; padding: 10px 12px; border-radius: 10px;
  font-size: 15px; font-weight: 600; cursor: pointer; color: inherit; }
.perf-aside nav button:hover { background: var(--color-fog, #f3f4f6); }
.perf-aside nav button.on { background: var(--color-brand, #c8102e); color: #fff; }
.perf-mini { display: flex; flex-direction: column; gap: 6px; border-top: 1px solid var(--color-line, #eee); padding-top: 10px; }
.perf-body { min-width: 0; }
.perf-body section { scroll-margin-top: 16px; margin-top: 0; }
.perf-card { display: flex; gap: 16px; align-items: center; flex-wrap: wrap;
  background: var(--color-card, #fff); border: 1px solid var(--color-line, #e5e5e5);
  border-radius: 14px; padding: 18px; margin: 0 0 8px; }
.perf-avatar { width: 64px; height: 64px; border-radius: 50%; display: flex; align-items: center;
  justify-content: center; font-size: 28px; font-weight: 800; color: #fff;
  background: var(--color-brand, #c8102e); flex: none; }
.perf-main { flex: 1; min-width: 200px; }
.perf-main h2 { margin: 0 0 4px; font-size: 26px; }
.perf-side { display: flex; flex-direction: column; gap: 8px; align-items: flex-end; }
.pts-badge { font-weight: 800; font-size: 18px; color: #92400e; background: #fef3c7;
  border-radius: 999px; padding: 4px 14px; }

/* — Mis datos como campos con lápiz (dentro del contenedor del índice) — */
.datos-card { background: var(--color-card, #fff); border: 1px solid var(--color-line, #e5e5e5);
  border-radius: 14px; padding: 18px; margin-top: 12px; }
.datos-head h3 { margin: 0; }
.datos-head .muted { text-align: left; margin: 4px 0 12px; }
.datos-fields { list-style: none; margin: 0; padding: 0; display: flex; flex-direction: column; }
.campo-row { display: flex; align-items: flex-start; gap: 12px; padding: 12px 4px;
  border-top: 1px solid var(--color-line, #eee); }
.campo-row:first-child { border-top: none; }
.campo-info { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 4px; }
.campo-label { font-size: 13px; font-weight: 700; text-transform: uppercase; letter-spacing: .06em; color: #6b6b6b; }
.campo-valor { font-size: 16px; overflow-wrap: anywhere; }
.campo-edit { display: flex; flex-direction: column; gap: 8px; }
.campo-edit .inp { width: 100%; box-sizing: border-box; border: 1px solid var(--color-concrete, #bbb);
  border-radius: 8px; padding: 10px 12px; font-size: 16px; font-family: inherit; }
.campo-actions { display: flex; gap: 8px; }
.icon-pencil { flex: none; width: 36px; height: 36px; border-radius: 999px; cursor: pointer;
  border: 1px solid var(--color-line, #e5e5e5); background: #fff; font-size: 18px; line-height: 1;
  display: inline-flex; align-items: center; justify-content: center; }
.icon-pencil:hover { background: var(--color-carbon, #111); color: #fff; border-color: var(--color-carbon, #111); }
.campo-lock { color: #bbb; font-size: 18px; }

.perf-form { display: flex; gap: .6rem; flex-wrap: wrap; align-items: end;
  border: 1px dashed var(--color-line, #e5e5e5); border-radius: 12px; padding: 12px; margin: 10px 0; }
.perf-form label { display: flex; flex-direction: column; gap: 4px; }
.veh-grid { display: grid; gap: 10px; grid-template-columns: repeat(auto-fill, minmax(210px, 1fr)); margin-top: 10px; }
.veh-card { border: 1px solid var(--color-line, #e5e5e5); border-radius: 12px; padding: 12px;
  display: flex; flex-direction: column; gap: 4px; background: var(--color-card, #fff); }
.veh-card.sel { border-color: var(--color-brand, #c8102e); }
.veh-card strong { font-size: 18px; letter-spacing: .04em; }
.veh-historial { scroll-margin-top: 16px; border-top: 1px solid var(--color-line, #eee); margin-top: 12px; padding-top: 4px; }
.btn-sm { padding: 6px 12px; font-size: 14px; }
.chip-row { display: flex; gap: 8px; flex-wrap: wrap; margin: 10px 0; }
.chip { border: 1px solid var(--color-line, #e5e5e5); background: var(--color-card, #fff);
  border-radius: 999px; padding: 6px 16px; font-weight: 700; cursor: pointer; letter-spacing: .04em; }
.chip.on { background: var(--color-carbon, #111); color: #fff; border-color: var(--color-carbon, #111); }
.km10k { background: #eff6ff; border: 1px solid #3b82f6; border-left: 4px solid #1d4ed8;
  border-radius: 10px; padding: 12px 14px; margin: 10px 0; display: flex; flex-direction: column; gap: 2px; }
.pend-grid { display: grid; gap: 10px; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); margin: 8px 0; }
.pend-card { border: 1px solid #f59e0b; border-left: 4px solid #f59e0b; background: #fffbeb;
  border-radius: 10px; padding: 10px 12px; display: flex; flex-direction: column; gap: 2px; }
.aten-list { display: flex; flex-direction: column; gap: 10px; margin: 8px 0; }
.aten-card { border: 1px solid var(--color-line, #e5e5e5); border-radius: 12px; padding: 12px 14px;
  background: var(--color-card, #fff); }
.aten-card p { margin: 6px 0; }
.aten-top { display: flex; align-items: center; justify-content: space-between; gap: 8px; }
.os-estado { font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: .06em;
  padding: 3px 10px; border-radius: 999px; background: var(--color-fog, #eee); }
.os-estado.est-abierta { background: #dbeafe; color: #1d4ed8; }
.os-estado.est-en_proceso { background: #fef3c7; color: #92400e; }
.os-estado.est-finalizada { background: #dcfce7; color: #15803d; }
.os-estado.est-cerrada { background: var(--color-fog, #eee); color: #666; }
.canje-note { background: #fef3c7; border: 1px solid #f59e0b; border-radius: 10px; padding: 10px 14px; margin: 8px 0; }
.canje-list { list-style: none; padding: 0; }
.canje-list li { padding: 6px 0; border-bottom: 1px solid var(--color-line, #eee); }
.canje-est { font-size: 12px; font-weight: 700; text-transform: uppercase; padding: 2px 10px; border-radius: 999px; background: #eee; }
.canje-est.ce-por_recoger { background: #fef3c7; color: #92400e; }
.canje-est.ce-despachado { background: #dcfce7; color: #15803d; }
.canje-est.ce-cancelado { background: #fee2e2; color: #b91c1c; }
.canje-est.ce-rechazado { background: #ffedd5; color: #9a3412; }
.canje-est.ce-expirado { background: #f1f1f1; color: #6b6b6b; }
.canje-nota { font-size: 13px; color: #6b6b6b; background: #f8fafc; border-radius: 8px; padding: 6px 10px; margin: 4px 0 0; }
.val-card { border: 1px solid var(--color-line, #e5e5e5); border-radius: 12px; padding: 12px 14px; background: var(--color-card, #fff); }
.val-head { display: flex; align-items: center; gap: 8px; margin: 0 0 6px; }
.stars-static { color: #f59e0b; letter-spacing: 2px; }

/* — Panel derecho: esqueleto de slider — */
.perf-slider { position: sticky; top: 16px; align-self: start; display: flex; flex-direction: column; gap: 8px;
  border: 1px solid var(--color-line, #e5e5e5); border-radius: 14px; padding: 16px; background: var(--color-card, #fff); }
.perf-slider h3 { margin: 0; }
.slider-hint { text-align: left; margin: 0; }
.slider-shell { display: flex; flex-direction: column; gap: 8px; }
.slider-viewport { position: relative; overflow: hidden; border-radius: 12px; border: 1px dashed var(--color-concrete, #bbb);
  background: var(--color-fog, #f3f4f6); min-height: 260px; }
.slider-slide { position: absolute; inset: 0; opacity: 0; transition: opacity .4s ease;
  display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 6px;
  padding: 24px 16px; text-align: center; }
.slider-slide.on { opacity: 1; position: relative; }
.slider-slide h4 { margin: 0; }
.slider-slide p { margin: 0; font-size: 14px; color: #6b6b6b; }
.slider-ph { font-size: 40px; }
.slider-nav { display: flex; gap: 8px; justify-content: center; }
.slider-nav button { width: 40px; height: 40px; border-radius: 999px; border: 1px solid var(--color-carbon, #111);
  background: #fff; cursor: pointer; font-size: 20px; line-height: 1; }
.slider-nav button:hover { background: var(--color-carbon, #111); color: #fff; }
.slider-dots { display: flex; gap: 8px; justify-content: center; }
.slider-dots button { width: 28px; height: 20px; border: none; cursor: pointer; background: transparent;
  padding: 0; display: flex; align-items: center; }
.slider-dots button::after { content: ""; display: block; width: 100%; height: 4px; background: #ddd; border-radius: 2px; }
.slider-dots button.on::after { background: var(--color-brand, #c8102e); }

/* — Recomendaciones debajo de todo, horizontal — */
.perf-tips { padding: 0 16px; }
.perf-tips .tips-row { overflow-x: auto; }

/* — Compat con clases viejas — */
.btn-ghost { border: 1px solid var(--color-carbon, #111); padding: 6px 14px; border-radius: 8px; background: #fff; cursor: pointer; }
.btn-ghost:hover { background: var(--color-fog, #f3f4f6); }

@media (max-width: 1100px) {
  .perf-layout { grid-template-columns: 200px minmax(0, 1fr); }
  .perf-slider { grid-column: 1 / -1; position: static; }
  .slider-viewport { min-height: 200px; }
}
@media (max-width: 760px) {
  .perf-layout { grid-template-columns: 1fr; }
  .perf-aside { position: static; }
  .perf-aside nav { flex-direction: row; overflow-x: auto; }
  .perf-aside nav button { white-space: nowrap; }
  .perf-slider { display: none; }
}
</style>
