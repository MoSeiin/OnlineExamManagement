import React, { useEffect, useState, useCallback } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { examApi } from '../../api/professor.js'
import Spinner from '../../components/Spinner.jsx'
import EmptyState from '../../components/EmptyState.jsx'

export default function ProfessorExamsPage() {
  const [searchParams] = useSearchParams()
  const courseFilter = searchParams.get('course') || ''

  const [exams, setExams] = useState(null)
  const [courses, setCourses] = useState([])
  const [error, setError] = useState('')
  const [showCreate, setShowCreate] = useState(false)
  const [editing, setEditing] = useState(null)

  const load = useCallback(async () => {
    setError('')
    try {
      const [examList, courseList] = await Promise.all([
        examApi.getMyExams(),
        examApi.getMyCourses(),
      ])
      setExams(examList)
      setCourses(courseList)
    } catch (err) {
      setError(err.message)
    }
  }, [])

  useEffect(() => {
    load()
  }, [load])

  async function handleDelete(examCode) {
    if (!confirm('این آزمون حذف شود؟')) return
    try {
      await examApi.deleteExam(examCode)
      load()
    } catch (err) {
      setError(err.message)
    }
  }

  const visibleExams = courseFilter ? exams?.filter((e) => e.courseCode === courseFilter) : exams

  return (
    <div>
      <div className="page-header">
        <div>
          <span className="eyebrow">پنل استاد</span>
          <h2>آزمون‌های من</h2>
          <p className="sub">
            {courseFilter ? 'آزمون‌های این دوره' : 'همه آزمون‌هایی که برای دوره‌های خود ساخته‌اید'}
          </p>
        </div>
        {courses.length > 0 && (
          <button className="btn btn-brass" onClick={() => setShowCreate(true)}>
            + آزمون جدید
          </button>
        )}
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {!visibleExams ? (
        <Spinner label="در حال بارگذاری…" />
      ) : visibleExams.length === 0 ? (
        <div className="card">
          <EmptyState glyph="۰" title="آزمونی یافت نشد" hint="با دکمه «آزمون جدید» یک آزمون بسازید." />
        </div>
      ) : (
        <div className="entity-grid">
          {visibleExams.map((e) => (
            <div className="entity-card" key={e.examCode}>
              <div className="entity-card-top">
                <div className="entity-badge">{e.title?.[0] || '؟'}</div>
                <div>
                  <h3>{e.title}</h3>
                  <div className="code ltr">{e.examCode}</div>
                </div>
              </div>
              <div className="meta">
                <span>دوره: {e.courseTitle}</span>
                <span>{e.durationMinutes} دقیقه</span>
              </div>
              {e.description && (
                <p style={{ fontSize: 13, color: 'var(--graphite)' }}>{e.description}</p>
              )}
              <div className="entity-card-actions">
                <Link className="btn btn-sm btn-brass" to={`/professor/exams/${e.examCode}`}>
                  سوالات
                </Link>
                <Link className="btn btn-sm btn-outline" to={`/professor/exams/${e.examCode}/results`}>
                  نتایج
                </Link>
                <button className="btn btn-sm btn-outline" onClick={() => setEditing(e)}>
                  ویرایش
                </button>
                <button className="btn btn-sm btn-danger" onClick={() => handleDelete(e.examCode)}>
                  حذف
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {showCreate && (
        <ExamFormModal
          title="ایجاد آزمون جدید"
          courses={courses}
          defaultCourseCode={courseFilter}
          onClose={() => setShowCreate(false)}
          onSave={async (payload) => {
            await examApi.createExam(payload)
            setShowCreate(false)
            load()
          }}
        />
      )}

      {editing && (
        <ExamFormModal
          title="ویرایش آزمون"
          initial={editing}
          editMode
          onClose={() => setEditing(null)}
          onSave={async (payload) => {
            await examApi.updateExam(editing.examCode, payload)
            setEditing(null)
            load()
          }}
        />
      )}
    </div>
  )
}

function ExamFormModal({ title, courses, defaultCourseCode, initial, editMode, onClose, onSave }) {
  const [form, setForm] = useState({
    courseCode: initial?.courseCode || defaultCourseCode || (courses?.[0]?.courseCode ?? ''),
    title: initial?.title || '',
    description: initial?.description || '',
    durationMinutes: initial?.durationMinutes || 60,
  })
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  async function handleSubmit(e) {
    e.preventDefault()
    setSaving(true)
    setError('')
    try {
      if (editMode) {
        await onSave({
          title: form.title,
          description: form.description,
          durationMinutes: Number(form.durationMinutes),
        })
      } else {
        await onSave({
          ...form,
          durationMinutes: Number(form.durationMinutes),
        })
      }
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
          {!editMode && (
            <div className="field">
              <label>دوره</label>
              <select
                value={form.courseCode}
                onChange={(e) => setForm((f) => ({ ...f, courseCode: e.target.value }))}
                required
              >
                {courses?.map((c) => (
                  <option key={c.courseCode} value={c.courseCode}>
                    {c.title}
                  </option>
                ))}
              </select>
            </div>
          )}
          <div className="field">
            <label>عنوان آزمون</label>
            <input
              required
              value={form.title}
              onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))}
            />
          </div>
          <div className="field">
            <label>توضیحات</label>
            <textarea
              value={form.description}
              onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))}
            />
          </div>
          <div className="field">
            <label>مدت زمان (دقیقه)</label>
            <input
              type="number"
              min={1}
              required
              value={form.durationMinutes}
              onChange={(e) => setForm((f) => ({ ...f, durationMinutes: e.target.value }))}
            />
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
