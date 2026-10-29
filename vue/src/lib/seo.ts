// SEO centralizado + helpers de búsqueda por etiquetas.
// SPA: Google indexa el HTML inicial, por eso index.html lleva los defaults
// y cada ruta los sobrescribe vía applySeo() en el router.

export interface SeoMeta {
  title?: string
  description?: string
  keywords?: string
  canonical?: string
  ogType?: string
  noindex?: boolean
}

export const SITE = {
  nombre: 'Rapillantas Huancayo',
  baseUrl: 'https://rapillantas.pe',
  telefono: '+51 964 123 456',
  direccion: 'Huancayo, Junín, Perú',
}

export const SEO_DEFAULTS: Required<Pick<SeoMeta, 'title' | 'description' | 'keywords'>> = {
  title: 'Rapillantas Huancayo — Neumáticos, refacciones y taller',
  description:
    'Rapillantas Huancayo: venta de neumáticos, refacciones, alineamiento, balanceo, frenos y mantenimiento vehicular en Huancayo. Cotiza por WhatsApp y recoge en tienda.',
  keywords:
    'neumáticos Huancayo, llantas Huancayo, refacciones, repuestos auto Huancayo, alineamiento, balanceo, frenos, cambio de aceite, taller mecánico Huancayo, Rapillantas',
}

function upsertMeta(selector: string, create: () => HTMLElement): HTMLElement {
  let el = document.head.querySelector(selector) as HTMLElement | null
  if (!el) {
    el = create()
    document.head.appendChild(el)
  }
  return el
}

export function applySeo(meta: SeoMeta = {}): void {
  const title = meta.title ?? SEO_DEFAULTS.title
  const description = meta.description ?? SEO_DEFAULTS.description
  const keywords = meta.keywords ?? SEO_DEFAULTS.keywords
  document.title = title

  const desc = upsertMeta('meta[name="description"]', () => {
    const m = document.createElement('meta')
    m.setAttribute('name', 'description')
    return m
  }) as HTMLMetaElement
  desc.content = description

  const kw = upsertMeta('meta[name="keywords"]', () => {
    const m = document.createElement('meta')
    m.setAttribute('name', 'keywords')
    return m
  }) as HTMLMetaElement
  kw.content = keywords

  const robots = upsertMeta('meta[name="robots"]', () => {
    const m = document.createElement('meta')
    m.setAttribute('name', 'robots')
    return m
  }) as HTMLMetaElement
  robots.content = meta.noindex ? 'noindex, nofollow' : 'index, follow'

  const setProp = (prop: string, content: string) => {
    const el = upsertMeta(`meta[property="${prop}"]`, () => {
      const m = document.createElement('meta')
      m.setAttribute('property', prop)
      return m
    }) as HTMLMetaElement
    el.content = content
  }
  setProp('og:title', title)
  setProp('og:description', description)
  setProp('og:type', meta.ogType ?? 'website')
  setProp('og:site_name', SITE.nombre)

  if (meta.canonical) {
    let link = document.head.querySelector('link[rel="canonical"]') as HTMLLinkElement | null
    if (!link) {
      link = document.createElement('link')
      link.setAttribute('rel', 'canonical')
      document.head.appendChild(link)
    }
    link.href = meta.canonical
  }
}

// ---- Búsqueda conectada a etiquetas ----

/** "hilux, Toyota,  frenos" -> ["hilux","toyota","frenos"] */
export const tagsDe = (s: string | null | undefined): string[] =>
  (s ?? '')
    .split(',')
    .map((t) => t.trim().toLowerCase())
    .filter(Boolean)

const norm = (s: string | null | undefined): string =>
  (s ?? '')
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, ' ')

/** Texto buscable de un producto: incluye etiquetas + descripción. */
export function textoProducto(p: any): string {
  const tags = Array.isArray(p?.etiquetas)
    ? (p.etiquetas as string[]).join(' ')
    : (p?.etiquetas ?? '')
  return norm(
    `${p?.nombre ?? ''} ${p?.marca ?? ''} ${p?.modelo ?? ''} ${p?.medida ?? ''} ${p?.categoria ?? ''} ${p?.descripcion ?? ''} ${tags}`,
  )
}

/** Texto buscable de un servicio: incluye etiquetas. */
export function textoServicio(s: any): string {
  return norm(`${s?.nombre ?? ''} ${s?.descripcion ?? ''} ${s?.etiquetas ?? ''}`)
}

/**
 * Coincidencia por tokens (AND): "pastilla hilux" exige ambas palabras
 * en el texto. Más tolerante que includes() con la frase completa y
 * aprovecha las etiquetas como sinónimos/keywords.
 */
export function coincide(haystackNorm: string, query: string): boolean {
  const tokens = norm(query).split(/\s+/).filter(Boolean)
  if (!tokens.length) return false
  return tokens.every((t) => haystackNorm.includes(t))
}
