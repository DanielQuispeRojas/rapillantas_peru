import axios from 'axios'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api/v1',
})

export const waLink = (msg: string) =>
  `https://wa.me/51999999999?text=${encodeURIComponent(msg)}`
