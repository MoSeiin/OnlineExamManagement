import React, { createContext, useContext, useEffect, useState, useCallback } from 'react'
import { setToken, setUnauthorizedHandler } from '../api/client.js'

const AuthContext = createContext(null)

const STORAGE_KEY = 'exam_app_user'

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const raw = localStorage.getItem(STORAGE_KEY)
      return raw ? JSON.parse(raw) : null
    } catch {
      return null
    }
  })

  const login = useCallback((authResponse) => {
    // authResponse: { token, userCode, userName, role, status }
    setToken(authResponse.token)
    localStorage.setItem(STORAGE_KEY, JSON.stringify(authResponse))
    setUser(authResponse)
  }, [])

  const logout = useCallback(() => {
    setToken(null)
    localStorage.removeItem(STORAGE_KEY)
    setUser(null)
  }, [])

  useEffect(() => {
    setUnauthorizedHandler(() => {
      setToken(null)
      localStorage.removeItem(STORAGE_KEY)
      setUser(null)
    })
  }, [])

  return (
    <AuthContext.Provider value={{ user, login, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
