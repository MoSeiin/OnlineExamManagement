const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

let onUnauthorized = () => {}
export function setUnauthorizedHandler(fn) {
  onUnauthorized = fn
}

function getToken() {
  return localStorage.getItem('token')
}

export function setToken(token) {
  if (token) localStorage.setItem('token', token)
  else localStorage.removeItem('token')
}

/**
 * فراخوانی عمومی API. مسیرهایی که با ? شامل پارامتر هستند مستقیم پاس داده می‌شوند.
 */
async function request(path, { method = 'GET', body, params, isForm = false } = {}) {
  let url = `${BASE_URL}${path}`

  if (params) {
    const usp = new URLSearchParams()
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        usp.append(key, value)
      }
    })
    const qs = usp.toString()
    if (qs) url += (url.includes('?') ? '&' : '?') + qs
  }

  const headers = {}
  const token = getToken()
  if (token) headers['Authorization'] = `Bearer ${token}`
  if (body !== undefined && !isForm) headers['Content-Type'] = 'application/json'

  const res = await fetch(url, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  })

  if (res.status === 401) {
    onUnauthorized()
  }

  if (res.status === 204) return null

  const text = await res.text()
  const data = text ? safeJson(text) : null

  if (!res.ok) {
    const message =
      (data && (data.message || data.error)) || `خطای ناشناخته (کد ${res.status})`
    const err = new Error(message)
    err.status = res.status
    err.data = data
    throw err
  }

  return data
}

function safeJson(text) {
  try {
    return JSON.parse(text)
  } catch {
    return text
  }
}

export const api = {
  get: (path, params) => request(path, { method: 'GET', params }),
  post: (path, body, params) => request(path, { method: 'POST', body, params }),
  put: (path, body, params) => request(path, { method: 'PUT', body, params }),
  patch: (path, body, params) => request(path, { method: 'PATCH', body, params }),
  del: (path, params) => request(path, { method: 'DELETE', params }),
}
