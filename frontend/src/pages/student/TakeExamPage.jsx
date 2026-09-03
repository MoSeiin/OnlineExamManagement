import React, { useEffect, useState, useRef, useCallback } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { studentApi } from '../../api/student.js'
import { FullPageSpinner } from '../../components/Spinner.jsx'

function formatTime(totalSeconds) {
  const s = Math.max(0, totalSeconds)
  const h = Math.floor(s / 3600)
  const m = Math.floor((s % 3600) / 60)
  const sec = s % 60
  const pad = (n) => String(n).padStart(2, '0')
  return h > 0 ? `${pad(h)}:${pad(m)}:${pad(sec)}` : `${pad(m)}:${pad(sec)}`
}

export default function TakeExamPage() {
  const { attemptCode } = useParams()
  const navigate = useNavigate()

  const [run, setRun] = useState(null)
  const [error, setError] = useState('')
  const [answers, setAnswers] = useState({}) // questionCode -> { mcqAnswer, descriptiveAnswer }
  const [currentIndex, setCurrentIndex] = useState(0)
  const [remaining, setRemaining] = useState(0)
  const [submitting, setSubmitting] = useState(false)
  const [result, setResult] = useState(null)
  const [savedFlash, setSavedFlash] = useState(false)

  const saveTimers = useRef({})

  useEffect(() => {
    studentApi
      .resumeExam(attemptCode)
      .then((data) => {
        setRun(data)
        setRemaining(data.remainingSeconds)
        const initial = {}
        Object.entries(data.currentAnswers || {}).forEach(([qCode, a]) => {
          initial[qCode] = { mcqAnswer: a.mcqAnswer, descriptiveAnswer: a.descriptiveAnswer }
        })
        setAnswers(initial)
      })
      .catch((err) => setError(err.message))
  }, [attemptCode])

  // countdown
  useEffect(() => {
    if (!run || result) return
    if (remaining <= 0) {
      handleSubmit(true)
      return
    }
    const id = setInterval(() => {
      setRemaining((r) => {
        if (r <= 1) {
          clearInterval(id)
          handleSubmit(true)
          return 0
        }
        return r - 1
      })
    }, 1000)
    return () => clearInterval(id)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [run, result])

  // warn before leaving the tab mid-exam
  useEffect(() => {
    function beforeUnload(e) {
      if (run && !result) {
        e.preventDefault()
        e.returnValue = ''
      }
    }
    window.addEventListener('beforeunload', beforeUnload)
    return () => window.removeEventListener('beforeunload', beforeUnload)
  }, [run, result])

  const persistAnswer = useCallback(
    (questionCode, mcqAnswer, descriptiveAnswer) => {
      studentApi
        .saveAnswer(attemptCode, questionCode, mcqAnswer ?? null, descriptiveAnswer ?? null)
        .then(() => {
          setSavedFlash(true)
          setTimeout(() => setSavedFlash(false), 900)
        })
        .catch(() => {
          /* حین شمارش معکوس، خطای موقتی شبکه را بی‌صدا نادیده می‌گیریم؛ ذخیره در تلاش بعدی انجام می‌شود */
        })
    },
    [attemptCode]
  )

  function handleMcqSelect(questionCode, optionIndex) {
    setAnswers((prev) => ({ ...prev, [questionCode]: { mcqAnswer: optionIndex, descriptiveAnswer: null } }))
    persistAnswer(questionCode, optionIndex, null)
  }

  function handleDescriptiveChange(questionCode, text) {
    setAnswers((prev) => ({ ...prev, [questionCode]: { mcqAnswer: null, descriptiveAnswer: text } }))
    clearTimeout(saveTimers.current[questionCode])
    saveTimers.current[questionCode] = setTimeout(() => {
      persistAnswer(questionCode, null, text)
    }, 900)
  }

  async function handleSubmit(auto = false) {
    if (submitting) return
    if (!auto && !confirm('آیا از ثبت نهایی آزمون مطمئن هستید؟ پس از ثبت امکان تغییر پاسخ‌ها وجود ندارد.')) return
    setSubmitting(true)
    try {
      const score = await studentApi.submitExam(attemptCode)
      setResult({ score, auto })
    } catch (err) {
      setError(err.message)
      setSubmitting(false)
    }
  }

  if (error && !run) return <div className="alert alert-error">{error}</div>
  if (!run) return <FullPageSpinner />

  if (result) {
    return (
      <div className="center-page" style={{ flexDirection: 'column', gap: 16 }}>
        <div className="brand-seal" style={{ width: 64, height: 64, fontSize: 22 }}>✓</div>
        <h2>{result.auto ? 'زمان آزمون به پایان رسید و پاسخ‌های شما ثبت شد' : 'آزمون با موفقیت ثبت شد'}</h2>
        <p className="score-chip" style={{ fontSize: 16, padding: '8px 18px' }}>
          نمره شما: {result.score}
        </p>
        <button className="btn btn-primary" onClick={() => navigate('/student/courses')}>
          بازگشت به دوره‌های من
        </button>
      </div>
    )
  }

  const questions = run.questions || []
  const q = questions[currentIndex]
  const currentAnswer = answers[q?.questionCode] || {}
  const answeredCount = questions.filter((qq) => {
    const a = answers[qq.questionCode]
    return a && (a.mcqAnswer !== null && a.mcqAnswer !== undefined || (a.descriptiveAnswer || '').trim())
  }).length

  const isLow = remaining <= 60

  return (
    <div>
      <div className="page-header">
        <div>
          <span className="eyebrow">در حال برگزاری آزمون</span>
          <h2>{run.examTitle}</h2>
          <p className="sub">
            {answeredCount} از {questions.length} سوال پاسخ داده شده
            {savedFlash && <span style={{ color: 'var(--sage)', marginRight: 10 }}>· ذخیره شد</span>}
          </p>
        </div>
        <button className="btn btn-brass" onClick={() => handleSubmit(false)} disabled={submitting}>
          {submitting ? 'در حال ثبت…' : 'پایان و ثبت آزمون'}
        </button>
      </div>

      <div className="exam-runner">
        {q && (
          <div className="answer-sheet-card">
            <div className="q-title mono">
              سوال {currentIndex + 1} از {questions.length} · {q.title}
            </div>
            <div className="q-text">{q.text}</div>

            {q.questionType === 'MULTIPLE_CHOICE' ? (
              <div className="mcq-options">
                {q.options?.map((opt, i) => (
                  <div
                    key={i}
                    className={`mcq-option ${currentAnswer.mcqAnswer === i ? 'selected' : ''}`}
                    onClick={() => handleMcqSelect(q.questionCode, i)}
                  >
                    <span className="mcq-bubble">
                      <span className="fill" />
                    </span>
                    <span>{opt}</span>
                  </div>
                ))}
              </div>
            ) : (
              <textarea
                style={{ width: '100%', minHeight: 180, border: '1.5px solid var(--line-strong)', borderRadius: 10, padding: 14, fontSize: 14.5 }}
                placeholder="پاسخ خود را اینجا بنویسید…"
                value={currentAnswer.descriptiveAnswer || ''}
                onChange={(e) => handleDescriptiveChange(q.questionCode, e.target.value)}
              />
            )}

            <div className="exam-nav-buttons">
              <button
                className="btn btn-outline"
                disabled={currentIndex === 0}
                onClick={() => setCurrentIndex((i) => Math.max(0, i - 1))}
              >
                سوال قبلی
              </button>
              <button
                className="btn btn-primary"
                disabled={currentIndex === questions.length - 1}
                onClick={() => setCurrentIndex((i) => Math.min(questions.length - 1, i + 1))}
              >
                سوال بعدی
              </button>
            </div>
          </div>
        )}

        <div className={`exam-timer ${isLow ? 'low' : ''}`}>
          <div className="label">زمان باقی‌مانده</div>
          <div className="value mono">{formatTime(remaining)}</div>
          <div className="question-nav">
            {questions.map((qq, i) => {
              const a = answers[qq.questionCode]
              const answered = a && (a.mcqAnswer !== null && a.mcqAnswer !== undefined || (a.descriptiveAnswer || '').trim())
              return (
                <button
                  key={qq.questionCode}
                  className={`${answered ? 'answered' : ''} ${i === currentIndex ? 'current' : ''}`}
                  onClick={() => setCurrentIndex(i)}
                >
                  {i + 1}
                </button>
              )
            })}
          </div>
        </div>
      </div>
    </div>
  )
}
