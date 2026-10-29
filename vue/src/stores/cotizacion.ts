import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// Cotización por WhatsApp: el cliente acumula productos/servicios con "+ Cotizar"
// y los envía en un solo mensaje junto a los datos de su vehículo.
export interface CotizacionItem {
  tipo: 'producto' | 'servicio'
  id: string
  nombre: string
  detalle?: string
}

export const useCotizacion = defineStore('cotizacion', () => {
  const items = ref<CotizacionItem[]>(JSON.parse(localStorage.getItem('cotizacion') ?? '[]'))

  const count = computed(() => items.value.length)

  function guardar() { localStorage.setItem('cotizacion', JSON.stringify(items.value)) }
  function tiene(tipo: string, id: string) {
    return items.value.some((x) => x.tipo === tipo && x.id === id)
  }
  function agregar(item: CotizacionItem) {
    if (!tiene(item.tipo, item.id)) { items.value.push(item); guardar() }
  }
  function quitar(tipo: string, id: string) {
    items.value = items.value.filter((x) => !(x.tipo === tipo && x.id === id))
    guardar()
  }
  function limpiar() { items.value = []; guardar() }

  /** Mensaje multi-ítem + vehículo para WhatsApp. */
  function mensaje(vehiculo: string): string {
    const lineas = items.value.map((x) =>
      x.tipo === 'producto' ? `• ${x.nombre}${x.detalle ? ` (${x.detalle})` : ''}` : `• Servicio: ${x.nombre}`)
    const partes = ['Hola, quiero cotizar:', ...lineas]
    if (vehiculo.trim()) partes.push(`Mi vehículo: ${vehiculo.trim()}`)
    return partes.join('\n')
  }

  return { items, count, tiene, agregar, quitar, limpiar, mensaje }
})
