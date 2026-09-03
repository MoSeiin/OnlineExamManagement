import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { studentApi } from '../../api/student.js'
import Spinner from '../../components/Spinner.jsx'
import EmptyState from '../../components/EmptyState.jsx'

export default function StudentCoursesPage() {
  const [courses, setCourses] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    studentApi.getMyCourses().then(setCourses).catch((e) => setError(e.message))
  }, [])

  return (
    <div>
      <div className="page-header">
        <div>
          <span className="eyebrow">پنل دانشجو</span>
          <h2>دوره‌های من</h2>
          <p className="sub">دوره‌هایی که در آن‌ها ثبت‌نام شده‌اید</p>
        </div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {!courses ? (
        <Spinner label="در حال بارگذاری…" />
      ) : courses.length === 0 ? (
        <div className="card">
          <EmptyState
            glyph="۰"
            title="هنوز در دوره‌ای عضو نیستید"
            hint="از مدیر سامانه بخواهید شما را به یک دوره اضافه کند."
          />
        </div>
      ) : (
        <div className="entity-grid">
          {courses.map((c) => (
            <Link className="entity-card" key={c.courseCode} to={`/student/courses/${c.courseCode}/exams`}>
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
                <span className="btn btn-sm btn-outline">مشاهده آزمون‌ها</span>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}
