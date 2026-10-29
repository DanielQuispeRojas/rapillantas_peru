import { ref, onMounted } from 'vue'
import { api, imgSrc } from '../lib/api'

export interface Tip {
  t: string
  d: string
  img: string | null
}

// Recomendaciones del taller: se editan en Admin → Portada (slider "recomendaciones").
// Si el slider está vacío, se usan las de respaldo.
const RESPALDO: Tip[] = [
  { t: 'Presión cada 15 días', d: 'Rodar bajo inflama el consumo y desgasta los bordes. Revisa la presión en frío según tu medida.', img: null },
  { t: 'Rotación cada 10.000 km', d: 'Rota las llantas para un desgaste parejo y alarga su vida útil hasta un 20%.', img: null },
  { t: 'Alineamiento anual', d: 'Si el timón jala a un lado o el desgaste es disparejo, toca alinear.', img: null },
  { t: 'Frenos sin chillidos', d: 'Chillido o vibración al frenar: pastillas al límite. No esperes al metal contra metal.', img: null },
  { t: 'Aceite a tiempo', d: 'Respeta el kilometraje del cambio de aceite; el motor lo agradece en consumo y potencia.', img: null },
  { t: 'Luces y batería', d: 'Antes de un viaje largo revisa luces, batería y llanta de repuesto.', img: null },
]

export function useRecomendaciones() {
  const tips = ref<Tip[]>([...RESPALDO])
  onMounted(async () => {
    const { data } = await api.get('/portada', { params: { clave: 'recomendaciones' } }).catch(() => ({ data: [] }))
    if (Array.isArray(data) && data.length) {
      tips.value = data.map((s: any) => ({
        t: s.titulo ?? '',
        d: s.subtitulo ?? '',
        img: s.imagenUrl ? imgSrc(s.imagenUrl) : null,
      })).filter((t: Tip) => t.t)
    }
  })
  return { tips }
}
