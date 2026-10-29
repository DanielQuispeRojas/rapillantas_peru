import { ref } from 'vue'

// Anti-doble-clic: bloquea reentradas mientras la acción está en curso.
// Uso: const g = useSubmit(); <button :disabled="g.ocupado.value" @click="g.ejecutar(guardar)">
export function useSubmit() {
  const ocupado = ref(false)
  async function ejecutar<T>(fn: () => Promise<T>): Promise<T | undefined> {
    if (ocupado.value) return undefined
    ocupado.value = true
    try {
      return await fn()
    } finally {
      ocupado.value = false
    }
  }
  return { ocupado, ejecutar }
}
