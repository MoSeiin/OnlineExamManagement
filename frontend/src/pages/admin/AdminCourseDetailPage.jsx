import React, { useEffect, useState, useCallback } from 'react'
import { useParams, Link } from 'react-router-dom'
import { adminApi } from '../../api/admin.js'
import Spinner from '../../components/Spinner.jsx'

export default function AdminCourseDetailPage() {
  const { courseCode } = useParams()
  const [course, setCourse] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [userCodeInput, setUserCodeInput] = useState('')
  const [busy, setBusy] = useState(false)

  const load = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      const res = await adminApi.getCourseDetails(courseCode)
      setCourse(res)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }, [courseCode])

  useEffect(() => {
    load()
  }, [load])

  async function handleAssign(e) {
    e.preventDefault()
    if (!userCodeInput.trim()) return
    setBusy(true)
    setError('')
    setNotice('')
    try {
      await adminApi.assignPersonToCourse(courseCode, userCodeInput.trim())
      setNotice('کاربر با موفقیت به دوره اضافه شد.')
      setUserCodeInput('')
      load()
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  async function handleRemove(userCode) {
    if (!confirm('این کاربر از دوره حذف شود؟')) return
    setError('')
    try {
      await adminApi.removePersonFromCourse(courseCode, userCode)
      load()
    } catch (err) {
      setError(err.message)
    }
  }

  if (loading) return <Spinner label="در حال بارگذاری…" />
  if (!course) return <div className="alert alert-error">{error || 'دوره یافت نشد'}</div>

  return (
    <div>
      <div className="page-header">
        <div>
          <span className="eyebrow">
            <Link to="/admin/courses">← بازگشت به دوره‌ها</Link>
          </span>
          <h2>{course.title}</h2>
          <p className="sub mono ltr">{course.courseCode}</p>
        </div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}
      {notice && <div className="alert alert-success">{notice}</div>}

      <div className="card">
        <h3 style={{ marginBottom: 14, fontSize: 15 }}>افزودن عضو به دوره</h3>
        <p className="small-note" style={{ marginBottom: 12 }}>
          کد کاربری استاد یا دانشجوی تاییدشده را وارد کنید (مثال: <span className="mono">P-xxxxxxxx</span>).
        </p>
        <form onSubmit={handleAssign} style={{ display: 'flex', gap: 10 }}>
          <input
            className="ltr"
            style={{ flex: 1, border: '1px solid var(--line-strong)', borderRadius: 6, padding: '10px 12px' }}
            placeholder="کد کاربری (userCode)"
            value={userCodeInput}
            onChange={(e) => setUserCodeInput(e.target.value)}
          />
          <button className="btn btn-brass" disabled={busy}>
            افزودن
          </button>
        </form>
      </div>

      <div className="card">
        <h3 style={{ marginBottom: 14, fontSize: 15 }}>استاد دوره</h3>
        {course.professor ? (
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <strong>{course.professor.firstName} {course.professor.lastName}</strong>
              <div className="small-note mono ltr">{course.professor.userCode} · {course.professor.email}</div>
            </div>
            <button className="btn btn-sm btn-danger" onClick={() => handleRemove(course.professor.userCode)}>
              حذف از دوره
            </button>
          </div>
        ) : (
          <p className="small-note">هنوز استادی به این دوره اختصاص نیافته است.</p>
        )}
      </div>

      <div className="card">
        <h3 style={{ marginBottom: 14, fontSize: 15 }}>
          دانشجویان دوره <span className="small-note">({course.students?.length || 0} نفر)</span>
        </h3>
        {!course.students || course.students.length === 0 ? (
          <p className="small-note">هنوز دانشجویی به این دوره اضافه نشده است.</p>
        ) : (
          <div className="table-wrap">
            <table className="data-table">
              <thead>
                <tr>
                  <th>نام</th>
                  <th>کد کاربری</th>
                  <th>ایمیل</th>
                  <th>عملیات</th>
                </tr>
              </thead>
              <tbody>
                {course.students.map((s) => (
                  <tr key={s.userCode}>
                    <td>{s.firstName} {s.lastName}</td>
                    <td className="mono ltr">{s.userCode}</td>
                    <td className="mono ltr">{s.email}</td>
                    <td>
                      <button className="btn btn-sm btn-danger" onClick={() => handleRemove(s.userCode)}>
                        حذف
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}
