import React, { useEffect, useState, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { adminApi } from '../../api/admin.js'
import Spinner from '../../components/Spinner.jsx'
import EmptyState from '../../components/EmptyState.jsx'

export default function AdminCoursesPage() {
  const [page, setPage] = useState(0)
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showCreate, setShowCreate] = useState(false)
  const [editing, setEditing] = useState(null)

  const load = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      const res = await adminApi.getCourses(page, 12)
      setData(res)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }, [page])

  useEffect(() => {
    load()
  }, [load])

  async function handleDelete(courseCode) {
    if (!confirm('این دوره حذف شود؟ این عملیات قابل بازگشت نیست.')) return
    try {
      await adminApi.deleteCourse(courseCode)
      load()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div>
      <div className="page-header">
        <div>
          <span className="eyebrow">پنل مدیر</span>
          <h2>دوره‌ها</h2>
          <p className="sub">تعریف دوره جدید و تخصیص استاد و دانشجو به هر دوره</p>
        </div>
        <button className="btn btn-brass" onClick={() => setShowCreate(true)}>
          + دوره جدید
        </button>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {loading ? (
        <Spinner label="در حال بارگذاری دوره‌ها…" />
      ) : !data || data.content.length === 0 ? (
        <div className="card">
          <EmptyState glyph="۰" title="هنوز دوره‌ای تعریف نشده" hint="با دکمه «دوره جدید» اولین دوره را بسازید." />
        </div>
      ) : (
        <>
          <div className="entity-grid">
            {data.content.map((c) => (
              <div className="entity-card" key={c.courseCode}>
                <div className="entity-card-top">
                  <div className="entity-badge">{c.title?.[0] || '؟'}</div>
                  <div>
                    <h3>{c.title}</h3>
                    <div className="code ltr">{c.courseCode}</div>
                  </div>
                </div>
                <div className="meta">
                  <span>شروع: {c.startDate || '—'}</span>
                  <span>پایان: {c.endDate || '—'}</span>
                </div>
                <div className="entity-card-actions">
                  <Link className="btn btn-sm btn-outline" to={`/admin/courses/${c.courseCode}`}>
                    جزئیات و اعضا
                  </Link>
                  <button className="btn btn-sm btn-outline" onClick={() => setEditing(c)}>
                    ویرایش
                  </button>
                  <button className="btn btn-sm btn-danger" onClick={() => handleDelete(c.courseCode)}>
                    حذف
                  </button>
                </div>
              </div>
            ))}
          </div>

          {data.totalPages > 1 && (
            <div style={{ display: 'flex', justifyContent: 'center', gap: 8, marginTop: 20 }}>
              <button className="btn btn-sm btn-outline" disabled={page === 0} onClick={() => setPage((p) => p - 1)}>
                قبلی
              </button>
              <span style={{ alignSelf: 'center', fontSize: 13, color: 'var(--graphite)' }}>
                صفحه {page + 1} از {data.totalPages}
              </span>
              <button
                className="btn btn-sm btn-outline"
                disabled={page + 1 >= data.totalPages}
                onClick={() => setPage((p) => p + 1)}
              >
                بعدی
              </button>
            </div>
          )}
        </>
      )}

      {showCreate && (
        <CourseFormModal
          title="ایجاد دوره جدید"
          onClose={() => setShowCreate(false)}
          onSave={async (payload) => {
            await adminApi.addCourse(payload)
            setShowCreate(false)
            load()
          }}
        />
      )}

      {editing && (
        <CourseFormModal
          title="ویرایش دوره"
          initial={editing}
          onClose={() => setEditing(null)}
          onSave={async (payload) => {
            await adminApi.updateCourse(editing.courseCode, payload)
            setEditing(null)
            load()
          }}
        />
      )}
    </div>
  )
}

function CourseFormModal({ title, initial, onClose, onSave }) {
  const [form, setForm] = useState({
    title: initial?.title || '',
    startDate: initial?.startDate || '',
    endDate: initial?.endDate || '',
  })
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  async function handleSubmit(e) {
    e.preventDefault()
    setSaving(true)
    setError('')
    try {
      await onSave(form)
    } catch (err) {
      setError(err.message)
      setSaving(false)
    }
  }

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-box" onClick={(e) => e.stopPropagation()}>
        <h3>{title}</h3>
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="field">
            <label>عنوان دوره</label>
            <input
              required
              value={form.title}
              onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))}
            />
          </div>
          <div className="row">
            <div className="field">
              <label>تاریخ شروع</label>
              <input
                type="date"
                value={form.startDate}
                onChange={(e) => setForm((f) => ({ ...f, startDate: e.target.value }))}
              />
            </div>
            <div className="field">
              <label>تاریخ پایان</label>
              <input
                type="date"
                value={form.endDate}
                onChange={(e) => setForm((f) => ({ ...f, endDate: e.target.value }))}
              />
            </div>
          </div>
          <div className="modal-actions">
            <button type="button" className="btn btn-outline" onClick={onClose}>
              انصراف
            </button>
            <button type="submit" className="btn btn-brass" disabled={saving}>
              {saving ? 'در حال ذخیره…' : 'ذخیره'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
