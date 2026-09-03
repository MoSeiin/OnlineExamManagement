import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { authApi } from '../api/auth.js'

export default function RegisterPage() {
  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    userName: '',
    email: '',
    password: '',
    age: '',
    role: 'STUDENT',
  })
  const [error, setError] = useState('')
  const [done, setDone] = useState(false)
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  function update(field, value) {
    setForm((f) => ({ ...f, [field]: value }))
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await authApi.register({
        ...form,
        age: form.age ? Number(form.age) : null,
      })
      setDone(true)
    } catch (err) {
      setError(err.message || 'ثبت‌نام ناموفق بود')
    } finally {
      setLoading(false)
    }
  }

  if (done) {
    return (
      <div className="auth-shell">
        <section className="auth-visual">
          <div className="auth-visual-bubbles">
            {Array.from({ length: 24 }).map((_, i) => (
              <span key={i} className={i % 4 === 0 ? 'filled' : ''} />
            ))}
          </div>
          <div className="auth-visual-text">
            <h2>ثبت‌نام شما با موفقیت انجام شد.</h2>
            <p>حساب شما اکنون در وضعیت «در انتظار تایید» است.</p>
          </div>
        </section>
        <section className="auth-form-side">
          <div className="auth-box">
            <div className="seal seal-pending" style={{ marginBottom: 18 }}>
              <span className="dot" /> در انتظار تایید مدیر
            </div>
            <h1>یک قدم مانده!</h1>
            <p className="lead">
              حساب شما ثبت شد، اما تا زمانی که مدیر سامانه آن را تایید نکند، امکان ورود
              نخواهید داشت. لطفاً بعداً دوباره تلاش کنید.
            </p>
            <Link to="/login" className="btn btn-primary" style={{ width: '100%' }}>
              بازگشت به صفحه ورود
            </Link>
          </div>
        </section>
      </div>
    )
  }

  return (
    <div className="auth-shell">
      <section className="auth-visual">
        <div className="auth-visual-bubbles">
          {Array.from({ length: 24 }).map((_, i) => (
            <span key={i} className={i % 6 === 0 ? 'filled' : ''} />
          ))}
        </div>
        <div className="auth-visual-text">
          <h2>به عنوان استاد یا دانشجو ثبت‌نام کنید.</h2>
          <p>پس از ثبت‌نام، حساب شما توسط مدیر سامانه بررسی و تایید می‌شود.</p>
        </div>
      </section>

      <section className="auth-form-side">
        <div className="auth-box">
          <h1>ایجاد حساب کاربری</h1>
          <p className="lead">اطلاعات زیر را تکمیل کنید.</p>

          {error && <div className="alert alert-error">{error}</div>}

          <form onSubmit={handleSubmit}>
            <div className="row">
              <div className="field">
                <label>نام</label>
                <input value={form.firstName} onChange={(e) => update('firstName', e.target.value)} />
              </div>
              <div className="field">
                <label>نام خانوادگی</label>
                <input value={form.lastName} onChange={(e) => update('lastName', e.target.value)} />
              </div>
            </div>

            <div className="field">
              <label>نام کاربری</label>
              <input required value={form.userName} onChange={(e) => update('userName', e.target.value)} />
            </div>

            <div className="field">
              <label>ایمیل</label>
              <input
                type="email"
                required
                value={form.email}
                onChange={(e) => update('email', e.target.value)}
              />
            </div>

            <div className="row">
              <div className="field">
                <label>رمز عبور</label>
                <input
                  type="password"
                  required
                  value={form.password}
                  onChange={(e) => update('password', e.target.value)}
                />
              </div>
              <div className="field">
                <label>سن</label>
                <input type="number" value={form.age} onChange={(e) => update('age', e.target.value)} />
              </div>
            </div>

            <div className="field">
              <label>ثبت‌نام به عنوان</label>
              <select value={form.role} onChange={(e) => update('role', e.target.value)}>
                <option value="STUDENT">دانشجو</option>
                <option value="PROFESSOR">استاد</option>
              </select>
            </div>

            <button className="btn btn-brass" type="submit" disabled={loading} style={{ width: '100%' }}>
              {loading ? 'در حال ثبت‌نام…' : 'ثبت‌نام'}
            </button>
          </form>

          <div className="auth-switch">
            قبلاً ثبت‌نام کرده‌اید؟ <Link to="/login">وارد شوید</Link>
          </div>
        </div>
      </section>
    </div>
  )
}
