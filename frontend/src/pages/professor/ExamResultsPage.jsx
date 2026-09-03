import React, { useEffect, useState, useCallback } from 'react'
import { useParams, Link } from 'react-router-dom'
import { professorGradingApi } from '../../api/professor.js'
import Spinner from '../../components/Spinner.jsx'
import EmptyState from '../../components/EmptyState.jsx'
import StatusBadge from '../../components/StatusBadge.jsx'

export default function ExamResultsPage() {
  const { examCode } = useParams()
  const [participants, setParticipants] = useState(null)
  const [error, setError] = useState('')
  const [selected, setSelected] = useState(null)

  const load = useCallback(async () => {
    setError('')
    try {
      const res = await professorGradingApi.getParticipants(examCode)
      setParticipants(res)
    } catch (err) {
      setError(err.message)
    }
  }, [examCode])

  useEffect(() => {
    load()
  }, [load])

  return (
    <div>
      <div className="page-header">
        <div>
          <span className="eyebrow">
            <Link to="/professor/exams">← بازگشت به آزمون‌های من</Link>
          </span>
          <h2>نتایج آزمون</h2>
          <p className="sub mono ltr">{examCode}</p>
        </div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {!participants ? (
        <Spinner label="در حال بارگذاری…" />
      ) : participants.length === 0 ? (
        <div className="card">
          <EmptyState glyph="۰" title="هنوز دانشجویی در این آزمون شرکت نکرده" />
        </div>
      ) : (
        <div className="table-wrap card">
          <table className="data-table">
            <thead>
              <tr>
                <th>دانشجو</th>
                <th>کد کاربری</th>
                <th>وضعیت</th>
                <th>نمره</th>
                <th>عملیات</th>
              </tr>
            </thead>
            <tbody>
              {participants.map((p) => (
                <tr key={p.examAttemptCode}>
                  <td>{p.studentFirstName} {p.studentLastName}</td>
                  <td className="mono ltr">{p.studentCode}</td>
                  <td><StatusBadge status={p.status} /></td>
                  <td className="mono">{p.score ?? '—'}</td>
                  <td>
                    <button className="btn btn-sm btn-outline" onClick={() => setSelected(p.examAttemptCode)}>
                      مشاهده و نمره‌دهی
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {selected && (
        <AnswersPanel attemptCode={selected} onClose={() => setSelected(null)} onGraded={load} />
      )}
    </div>
  )
}

function AnswersPanel({ attemptCode, onClose, onGraded }) {
  const [answers, setAnswers] = useState(null)
  const [error, setError] = useState('')
  const [savingCode, setSavingCode] = useState(null)

  const load = useCallback(async () => {
    setError('')
    try {
      const res = await professorGradingApi.getAnswers(attemptCode)
      setAnswers(res)
    } catch (err) {
      setError(err.message)
    }
  }, [attemptCode])

  useEffect(() => {
    load()
  }, [load])

  async function handleGrade(answerCode, score, maxScore) {
    if (score === '' || score === null) return
    if (Number(score) > maxScore) {
      setError(`نمره نمی‌تواند از سقف نمره این سوال (${maxScore}) بیشتر باشد.`)
      return
    }
    setSavingCode(answerCode)
    setError('')
    try {
      await professorGradingApi.gradeAnswer(answerCode, Number(score))
      await load()
      onGraded()
    } catch (err) {
      setError(err.message)
    } finally {
      setSavingCode(null)
    }
  }

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-box" style={{ maxWidth: 640 }} onClick={(e) => e.stopPropagation()}>
        <h3>پاسخ‌های دانشجو</h3>
        {error && <div className="alert alert-error">{error}</div>}

        {!answers ? (
          <Spinner label="در حال بارگذاری…" />
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            {answers.map((a) => (
              <div key={a.studentAnswerCode} className="card" style={{ margin: 0 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                  <strong>{a.questionTitle}</strong>
                  <span className="score-chip">سقف: {a.maxScore ?? '—'}</span>
                </div>
                <p style={{ fontSize: 13.5, color: 'var(--graphite)', marginBottom: 10 }}>{a.questionText}</p>

                {a.questionType === 'MULTIPLE_CHOICE' ? (
                  <div>
                    {a.options?.map((opt, i) => (
                      <div
                        key={i}
                        style={{
                          padding: '6px 10px',
                          borderRadius: 6,
                          marginBottom: 4,
                          fontSize: 13.5,
                          background:
                            i === a.mcqAnswer && i === a.correctAnswerIndex
                              ? 'var(--sage-tint)'
                              : i === a.mcqAnswer
                              ? 'var(--clay-tint)'
                              : i === a.correctAnswerIndex
                              ? 'var(--brass-tint)'
                              : 'transparent',
                        }}
                      >
                        {opt}
                        {i === a.correctAnswerIndex && '  ✓ (پاسخ صحیح)'}
                        {i === a.mcqAnswer && i !== a.correctAnswerIndex && '  ← پاسخ دانشجو'}
                      </div>
                    ))}
                    <div className="small-note" style={{ marginTop: 6 }}>
                      نمره خودکار: <strong className="mono">{a.autoScore ?? 0}</strong>
                    </div>
                  </div>
                ) : (
                  <div>
                    <div
                      style={{
                        background: 'var(--ink-tint)',
                        borderRadius: 8,
                        padding: 12,
                        fontSize: 13.5,
                        marginBottom: 10,
                        minHeight: 50,
                      }}
                    >
                      {a.descriptiveAnswer || <span className="small-note">پاسخی ثبت نشده است.</span>}
                    </div>
                    <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
                      <label className="small-note">نمره:</label>
                      <input
                        type="number"
                        min={0}
                        max={a.maxScore}
                        step={0.25}
                        defaultValue={a.manualScore ?? ''}
                        className="mono"
                        style={{ width: 90, border: '1px solid var(--line-strong)', borderRadius: 6, padding: '6px 8px' }}
                        onBlur={(e) => handleGrade(a.studentAnswerCode, e.target.value, a.maxScore)}
                      />
                      {savingCode === a.studentAnswerCode && <span className="small-note">در حال ذخیره…</span>}
                    </div>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}

        <div className="modal-actions">
          <button className="btn btn-outline" onClick={onClose}>بستن</button>
        </div>
      </div>
    </div>
  )
}
