import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { authApi } from '../api/auth.js'
import { useAuth } from '../context/AuthContext.jsx'

const ROLE_HOME = {
  ADMIN: '/admin/users',
  PROFESSOR: '/professor/courses',
  STUDENT: '/student/courses',
}

export default function LoginPage() {
  const [userName, setUserName] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const { login } = useAuth()
  const navigate = useNavigate()

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const res = await authApi.login(userName.trim(), password)
      login(res)
      navigate(ROLE_HOME[res.role] || '/')
    } catch (err) {
      setError(err.message || 'ورود ناموفق بود')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-shell">
      <section className="auth-visual">
        <div className="auth-visual-bubbles">
          {Array.from({ length: 24 }).map((_, i) => (
            <span key={i} className={i % 5 === 0 ? 'filled' : ''} />
          ))}
        </div>
        <div className="auth-visual-text">
          <h2>یک پاسخنامهٔ دیجیتال، برای هر دوره‌ای که تدریس یا مطالعه می‌کنید.</h2>
          <p>
            مدیریت ثبت‌نام، برگزاری آزمون آنلاین و نمره‌دهی خودکار و دستی — همه در یک
            سامانه.
          </p>
        </div>
      </section>

      <section className="auth-form-side">
        <div className="auth-box">
          <h1>ورود به سامانه</h1>
          <p className="lead">با نام کاربری و رمز عبور خود وارد شوید.</p>

          {error && <div className="alert alert-error">{error}</div>}

          <form onSubmit={handleSubmit}>
            <div className="field">
              <label>نام کاربری</label>
              <input
                value={userName}
                onChange={(e) => setUserName(e.target.value)}
                required
                autoFocus
              />
            </div>
            <div className="field">
              <label>رمز عبور</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
            <button className="btn btn-brass" type="submit" disabled={loading} style={{ width: '100%' }}>
              {loading ? 'در حال ورود…' : 'ورود'}
            </button>
          </form>

          <div className="auth-switch">
            حساب کاربری ندارید؟ <Link to="/register">ثبت‌نام کنید</Link>
          </div>
        </div>
      </section>
    </div>
  )
}
