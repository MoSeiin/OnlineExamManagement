import React, { useEffect, useState, useCallback } from 'react'
import { adminApi } from '../../api/admin.js'
import StatusBadge from '../../components/StatusBadge.jsx'
import Spinner from '../../components/Spinner.jsx'
import EmptyState from '../../components/EmptyState.jsx'

const ROLE_LABEL = { ADMIN: 'مدیر', PROFESSOR: 'استاد', STUDENT: 'دانشجو' }

export default function AdminUsersPage() {
  const [page, setPage] = useState(0)
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [busyCode, setBusyCode] = useState(null)
  const [editing, setEditing] = useState(null)

  const [filters, setFilters] = useState({ role: '', status: '', firstName: '', lastName: '', userName: '' })
  const hasFilters = Object.values(filters).some(Boolean)

  const load = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      const res = hasFilters
        ? await adminApi.searchUsers(filters, page, 10)
        : await adminApi.getUsers(page, 10)
      setData(res)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, JSON.stringify(filters)])

  useEffect(() => {
    load()
  }, [load])

  async function act(fn, userCode) {
    setBusyCode(userCode)
    try {
      await fn(userCode)
      await load()
    } catch (err) {
      setError(err.message)
    } finally {
      setBusyCode(null)
    }
  }

  return (
    <div>
      <div className="page-header">
        <div>
          <span className="eyebrow">پنل مدیر</span>
          <h2>کاربران</h2>
          <p className="sub">تایید ثبت‌نام کاربران، جستجو و ویرایش اطلاعات آن‌ها</p>
        </div>
      </div>

      <div className="card" style={{ marginBottom: 16 }}>
        <div className="row" style={{ marginBottom: 0 }}>
          <div className="field">
            <label>نام</label>
            <input
              value={filters.firstName}
              onChange={(e) => { setPage(0); setFilters((f) => ({ ...f, firstName: e.target.value })) }}
            />
          </div>
          <div className="field">
            <label>نام خانوادگی</label>
            <input
              value={filters.lastName}
              onChange={(e) => { setPage(0); setFilters((f) => ({ ...f, lastName: e.target.value })) }}
            />
          </div>
          <div className="field">
            <label>نام کاربری</label>
            <input
              value={filters.userName}
              onChange={(e) => { setPage(0); setFilters((f) => ({ ...f, userName: e.target.value })) }}
            />
          </div>
          <div className="field">
            <label>نقش</label>
            <select
              value={filters.role}
              onChange={(e) => { setPage(0); setFilters((f) => ({ ...f, role: e.target.value })) }}
            >
              <option value="">همه</option>
              <option value="STUDENT">دانشجو</option>
              <option value="PROFESSOR">استاد</option>
              <option value="ADMIN">مدیر</option>
            </select>
          </div>
          <div className="field">
            <label>وضعیت</label>
            <select
              value={filters.status}
              onChange={(e) => { setPage(0); setFilters((f) => ({ ...f, status: e.target.value })) }}
            >
              <option value="">همه</option>
              <option value="PENDING">در انتظار تایید</option>
              <option value="APPROVED">تاییدشده</option>
              <option value="REJECTED">ردشده</option>
            </select>
          </div>
        </div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      <div className="card">
        {loading ? (
          <Spinner label="در حال بارگذاری کاربران…" />
        ) : !data || data.content.length === 0 ? (
          <EmptyState glyph="۰" title="کاربری یافت نشد" hint="فیلترها را تغییر دهید یا صبر کنید تا کسی ثبت‌نام کند." />
        ) : (
          <>
            <div className="table-wrap">
              <table className="data-table">
                <thead>
                <tr>
                  <th>نام</th>
                  <th>کد کاربری</th>
                  <th>ایمیل</th>
                  <th>نقش</th>
                  <th>سن</th>
                  <th>وضعیت</th>
                  <th>عملیات</th>
                </tr>
                </thead>
                <tbody>
                {data.content.map((u) => (
                    <tr key={u.userCode}>
                      <td>{u.firstName} {u.lastName}</td>
                      <td className="mono ltr">{u.userCode}</td>
                      <td className="mono ltr">{u.email}</td>
                      <td>{ROLE_LABEL[u.role] || u.role}</td>
                      <td>{u.age ?? '—'}</td>
                      <td><StatusBadge status={u.status} /></td>
                      <td>
                        <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
                          {u.status !== 'APPROVED' && (
                              <button
                                  className="btn btn-sm btn-brass"
                                  disabled={busyCode === u.userCode}
                                  onClick={() => act(adminApi.approveUser, u.userCode)}
                              >
                                تایید
                              </button>
                          )}
                          {u.status !== 'REJECTED' && (
                              <button
                                  className="btn btn-sm btn-danger"
                                  disabled={busyCode === u.userCode}
                                  onClick={() => act(adminApi.rejectUser, u.userCode)}
                              >
                                رد
                              </button>
                          )}
                          <button className="btn btn-sm btn-outline" onClick={() => setEditing(u)}>
                            ویرایش
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <Pagination page={page} setPage={setPage} totalPages={data.totalPages} />
          </>
        )}
      </div>

      {editing && (
        <EditUserModal
          user={editing}
          onClose={() => setEditing(null)}
          onSaved={() => { setEditing(null); load() }}
        />
      )}
    </div>
  )
}

function Pagination({ page, setPage, totalPages }) {
  if (totalPages <= 1) return null
  return (
    <div style={{ display: 'flex', justifyContent: 'center', gap: 8, marginTop: 18 }}>
      <button className="btn btn-sm btn-outline" disabled={page === 0} onClick={() => setPage((p) => p - 1)}>
        قبلی
      </button>
      <span style={{ alignSelf: 'center', fontSize: 13, color: 'var(--graphite)' }}>
        صفحه {page + 1} از {totalPages}
      </span>
      <button
        className="btn btn-sm btn-outline"
        disabled={page + 1 >= totalPages}
        onClick={() => setPage((p) => p + 1)}
      >
        بعدی
      </button>
    </div>
  )
}

function EditUserModal({ user, onClose, onSaved }) {
  const [form, setForm] = useState({
    firstName: user.firstName || '',
    lastName: user.lastName || '',
    email: user.email || '',
    age: user.age || '',
    role: user.role,
    status: user.status,
  })
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  async function handleSave() {
    setSaving(true)
    setError('')
    try {
      await adminApi.updateUser(user.userCode, { ...form, age: form.age ? Number(form.age) : null })
      onSaved()
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-box" onClick={(e) => e.stopPropagation()}>
        <h3>ویرایش کاربر <span className="mono small-note ltr">{user.userCode}</span></h3>
        {error && <div className="alert alert-error">{error}</div>}
        <div className="row">
          <div className="field">
            <label>نام</label>
            <input value={form.firstName} onChange={(e) => setForm((f) => ({ ...f, firstName: e.target.value }))} />
          </div>
          <div className="field">
            <label>نام خانوادگی</label>
            <input value={form.lastName} onChange={(e) => setForm((f) => ({ ...f, lastName: e.target.value }))} />
          </div>
        </div>
        <div className="field">
          <label>ایمیل</label>
          <input value={form.email} onChange={(e) => setForm((f) => ({ ...f, email: e.target.value }))} />
        </div>
        <div className="row">
          <div className="field">
            <label>سن</label>
            <input type="number" value={form.age} onChange={(e) => setForm((f) => ({ ...f, age: e.target.value }))} />
          </div>
          <div className="field">
            <label>نقش</label>
            <select
              value={form.role}
              disabled={user.role === 'ADMIN'}
              onChange={(e) => setForm((f) => ({ ...f, role: e.target.value }))}
            >
              <option value="STUDENT">دانشجو</option>
              <option value="PROFESSOR">استاد</option>
              <option value="ADMIN">مدیر</option>
            </select>
          </div>
        </div>
        <div className="field">
          <label>وضعیت</label>
          <select value={form.status} onChange={(e) => setForm((f) => ({ ...f, status: e.target.value }))}>
            <option value="PENDING">در انتظار تایید</option>
            <option value="APPROVED">تاییدشده</option>
            <option value="REJECTED">ردشده</option>
          </select>
        </div>
        <div className="modal-actions">
          <button className="btn btn-outline" onClick={onClose}>انصراف</button>
          <button className="btn btn-brass" disabled={saving} onClick={handleSave}>
            {saving ? 'در حال ذخیره…' : 'ذخیره تغییرات'}
          </button>
        </div>
      </div>
    </div>
  )
}
