import { api } from './client.js'

export const authApi = {
  login: (userName, password) => api.post('/auth/login', { userName, password }),
  register: (payload) => api.post('/auth/register', payload),
}
