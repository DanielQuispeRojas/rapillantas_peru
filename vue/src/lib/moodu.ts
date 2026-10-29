import axios from 'axios'

// Cliente del backend MOODU (catalogo, taller y almacen viven ahi).
// Base configurable: VITE_MOODU_URL (localhost) o la URL del host en Render.
// El overlay propio del sitio (fotos, compatibilidad, puntos, ofertas) sigue
// en el backend Rapillantas (`api` de lib/api.ts), keyeado por SKU.
export const moodu = axios.create({
  baseURL: (import.meta.env.VITE_MOODU_URL as string | undefined) ?? 'http://localhost:8080',
  headers: (import.meta.env.VITE_MOODU_TOKEN as string | undefined)
    ? { 'X-Moodu-Api-Key': import.meta.env.VITE_MOODU_TOKEN as string }
    : undefined,
})

// Envelope RespuestaApi de MOODU: { ok, mensaje, payload }.
// 403 = API apagada en MOODU (activarla en MOODU > Configuracion > Web).
// 404 = no existe (ej. placa sin expediente). Nunca lanza: devuelve null.
async function pub<T>(path: string, params?: any): Promise<T | null> {
  try {
    const { data } = await moodu.get(path, { params })
    if (!data?.ok) return null
    return data.payload as T
  } catch {
    return null
  }
}

export interface MooduMercaderia {
  sku: string; nombre: string; descripcion?: string; precio: number; moneda?: string
  stock?: number; marca?: string; modelo?: string; categoria?: string
  dimensiones?: string; unidadMedida?: string; estado?: string
}

export interface MooduServicio {
  sku: string; nombre: string; precioBase?: number; precio?: number
  etiquetas?: string; categorias?: string[]; estado?: string
}

export const mMercaderias = async (q?: string, limite = 50): Promise<MooduMercaderia[]> => {
  const r = await pub<{ mercaderias: MooduMercaderia[] }>('/pub/core/mercaderias', { q, limite })
  return r?.mercaderias ?? []
}

export const mMercaderiaPorSku = async (sku: string): Promise<MooduMercaderia | null> => {
  const r = await pub<{ mercaderia: MooduMercaderia }>('/pub/core/mercaderias', { sku })
  return r?.mercaderia ?? null
}

export const mServicios = async (q?: string, limite = 50): Promise<MooduServicio[]> => {
  const r = await pub<{ servicios: MooduServicio[] }>('/pub/core/servicios', { q, limite })
  return r?.servicios ?? []
}

export const mExpediente = async (placa: string): Promise<any | null> => {
  const r = await pub<{ expediente: any }>('/pub/r/expedientes', { placa })
  return r?.expediente ?? null
}

export const mOt = async (codigo: string): Promise<any | null> => {
  const r = await pub<{ ot: any }>('/pub/r/ot', { codigo })
  return r?.ot ?? null
}

// Adapters al shape que espera la tienda (ProductCard, Cotizador, Buscar).
// id = sku (las rutas /producto/:id pasan a llevar el SKU).
export function aMercaderia(m: MooduMercaderia): any {
  return {
    id: m.sku, sku: m.sku, nombre: m.nombre, descripcion: m.descripcion ?? '',
    marca: m.marca ?? '', modelo: m.modelo ?? '', medida: m.dimensiones ?? '',
    categoria: m.categoria ?? '', precio: Number(m.precio ?? 0),
    precioDescuento: undefined, stockActual: Number(m.stock ?? 0),
    imagenUrl: '', etiquetas: '', moneda: m.moneda ?? 'PEN',
  }
}

export function aServicio(s: MooduServicio, puntos = 0): any {
  const base = Number(s.precioBase ?? s.precio ?? 0)
  const vig = Number(s.precio ?? base)
  return {
    id: s.sku, sku: s.sku, nombre: s.nombre, descripcion: '',
    etiquetas: s.etiquetas ?? '', precioRef: base,
    precioDescuento: vig < base ? vig : undefined,
    duracionMin: null, puntos,
  }
}
