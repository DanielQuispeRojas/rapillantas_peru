import { ref, onMounted, onUnmounted } from 'vue'
import { api, imgSrc } from '../lib/api'

// Bloque reutilizable: <SliderBlock clave="portada" /> — solo se indica la clave
// del slider creado en Admin → Portada y se incrusta donde corresponda.
export function useSlider(clave: string, intervaloMs = 6000, fallback = '/banners/banner-1.jpg') {
  const slides = ref<any[]>([])
  const idx = ref(0)
  let timer: any = null
  onMounted(async () => {
    const { data } = await api.get('/portada', { params: { clave } }).catch(() => ({ data: [] }))
    slides.value = (data?.length ? data : [{ imagenUrl: fallback, titulo: '', subtitulo: '', mensajeWsp: '' }])
      .map((s: any) => ({ src: imgSrc(s.imagenUrl) || fallback, titulo: s.titulo, subtitulo: s.subtitulo, mensaje: s.mensajeWsp }))
    if (slides.value.length > 1) {
      timer = setInterval(() => { idx.value = (idx.value + 1) % slides.value.length }, intervaloMs)
    }
  })
  onUnmounted(() => { if (timer) clearInterval(timer) })
  return { slides, idx }
}
