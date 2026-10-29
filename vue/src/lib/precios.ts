// Helpers de precio compartidos (antes la lógica vivía duplicada en cada vista).
export function tieneDscto(p: any): boolean {
  return !!p?.precioDescuento && Number(p.precioDescuento) < Number(p.precio)
}

// Mensaje de WhatsApp sin duplicar la medida: muchos nombres ya la traen
// (ej. "Llanta 205/55R16") y antes salía "…205/55R16 205/55R16".
export function mensajeProducto(p: any, prefijo = 'Hola, me interesa'): string {
  const nombre = String(p?.nombre ?? '').trim()
  const base = `${prefijo} ${nombre}`.trim()
  const med = String(p?.medida ?? '').trim()
  if (med && !nombre.toLowerCase().includes(med.toLowerCase())) return `${base} ${med}`
  return base
}
