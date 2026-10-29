// Nombres de meses en español para selectores y filtros (siempre nombres, nunca números).
export const MESES = [
  'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
  'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre',
]
export const nombreMes = (m: number | null | undefined) =>
  m != null && m >= 1 && m <= 12 ? MESES[m - 1] : '—'
