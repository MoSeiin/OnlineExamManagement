import React, { useEffect, useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { studentApi } from '../../api/student.js'
import Spinner from '../../components/Spinner.jsx'
import EmptyState from '../../components/EmptyState.jsx'
import StatusBadge from '../../components/StatusBadge.jsx'

export default function StudentExamsPage() {
  const { courseCode } = useParams()
  const [exams, setExams] = useState(null)
  const [error, setError] = useState('')
  const [startingCode, setStartingCode] = useState(null)
  const navigate = useNavigate()

  useEffect(() => {
    studentApi.getCourseExams(courseCode).then(setExams).catch((e) => setError(e.message))
  }, [courseCode])

  async function handleStart(examCode) {
    setStartingCode(examCode)
    setError('')
    try {
      const run = await studentApi.startExam(examCode)
      navigate(`/student/exams/take/${run.examAttemptCode}`)
    } catch (err) {
      setError(err.message)
      setStartingCode(null)
    }
  }

  function handleResume(attemptCode) {
    navigate(`/student/exams/take/${attemptCode}`)
  }

  return (
    <div>
      <div className="page-header">
        <div>
          <span className="eyebrow">
            <Link to="/student/courses">← بازگشت به دوره‌های من</Link>
          </span>
          <h2>آزمون‌های این دوره</h2>
        </div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {!exams ? (
        <Spinner label="در حال بارگذاری…" />
      ) : exams.length === 0 ? (
        <div className="card">
          <EmptyState glyph="۰" title="هنوز آزمونی برای این دوره تعریف نشده" />
        </div>
      ) : (
        <div className="entity-grid">
          {exams.map((e) => (
            <div className="entity-card" key={e.examCode}>
              <div className="entity-card-top">
                <div className="entity-badge">{e.title?.[0] || '؟'}</div>
                <div>
                  <h3>{e.title}</h3>
                  <StatusBadge status={e.attemptStatus} />
                </div>
              </div>
              {e.description && <p style={{ fontSize: 13, color: 'var(--graphite)' }}>{e.description}</p>}
              <div className="meta">
                <span>مدت زمان: {e.durationMinutes} دقیقه</span>
                {e.attemptStatus === 'FINISHED' && (
                  <span className="score-chip">نمره: {e.score ?? '—'}</span>
                )}
              </div>
              <div className="entity-card-actions">
                {e.attemptStatus === 'NOT_STARTED' && (
                  <button
                    className="btn btn-sm btn-brass"
                    disabled={startingCode === e.examCode}
                    onClick={() => handleStart(e.examCode)}
                  >
                    {startingCode === e.examCode ? 'در حال شروع…' : 'شروع آزمون'}
                  </button>
                )}
                {e.attemptStatus === 'IN_PROGRESS' && (
                  <button className="btn btn-sm btn-brass" onClick={() => handleResume(e.examAttemptCode)}>
                    ادامه آزمون
                  </button>
                )}
                {e.attemptStatus === 'FINISHED' && (
                  <span className="small-note">این آزمون به پایان رسیده است.</span>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
