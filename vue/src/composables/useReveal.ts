import type { Directive } from 'vue'

// v-reveal: fundido al hacer scroll (los objetos aparecen al bajar).
// Uso: v-reveal en secciones; v-reveal="i * 60" para cascada en grillas.
// Sin JS de animación: solo agrega .is-visible; el CSS hace el resto.
let observer: IntersectionObserver | null = null
function getObserver() {
  if (!observer) {
    observer = new IntersectionObserver(
      (entries) => {
        for (const e of entries) {
          if (e.isIntersecting) {
            e.target.classList.add('is-visible')
            observer?.unobserve(e.target)
          }
        }
      },
      { threshold: 0.1, rootMargin: '0px 0px -6% 0px' },
    )
  }
  return observer
}

export const vReveal: Directive<HTMLElement, number | undefined> = {
  mounted(el, binding) {
    el.classList.add('reveal')
    const delay = Number(binding.value ?? 0)
    if (delay > 0) el.style.transitionDelay = `${Math.min(delay, 600)}ms`
    getObserver().observe(el)
  },
  unmounted(el) {
    getObserver().unobserve(el)
  },
}
