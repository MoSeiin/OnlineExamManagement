import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { examApi } from '../../api/professor.js'
import Spinner from '../../components/Spinner.jsx'
import EmptyState from '../../components/EmptyState.jsx'

export default function ProfessorCoursesPage() {
  const [courses, setCourses] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    examApi.getMyCourses().then(setCourses).catch((e) => setError(e.message))
  }, [])

  return (
    <div>
      <div className="page-header">
        <div>
          <span className="eyebrow">پنل استاد</span>
          <h2>دوره‌های من</h2>
          <p className="sub">دوره‌هایی که توسط مدیر سامانه به شما اختصاص داده شده‌اند</p>
        </div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {!courses ? (
        <Spinner label="در حال بارگذاری…" />
      ) : courses.length === 0 ? (
        <div className="card">
          <EmptyState
            glyph="۰"
            title="هنوز دوره‌ای به شما اختصاص داده نشده"
            hint="از مدیر سامانه بخواهید شما را به یک دوره اضافه کند."
          />
        </div>
      ) : (
        <div className="entity-grid">
          {courses.map((c) => (
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
                <Link className="btn btn-sm btn-outline" to={`/professor/exams?course=${c.courseCode}`}>
                  آزمون‌های این دوره
                </Link>
                <Link className="btn btn-sm btn-outline" to={`/professor/questions?course=${c.courseCode}`}>
                  بانک سوالات
                </Link>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
