import React, { useEffect, useState, useCallback } from 'react'
import { useParams, Link } from 'react-router-dom'
import { examApi, questionApi } from '../../api/professor.js'
import Spinner from '../../components/Spinner.jsx'
import EmptyState from '../../components/EmptyState.jsx'

export default function ExamEditorPage() {
  const { examCode } = useParams()
  const [exam, setExam] = useState(null)
  const [examQuestions, setExamQuestions] = useState(null)
  const [bank, setBank] = useState([])
  const [totalScore, setTotalScore] = useState(null)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [showAddFromBank, setShowAddFromBank] = useState(false)
  const [showNewQuestion, setShowNewQuestion] = useState(false)

  const load = useCallback(async () => {
    setError('')
    try {
      const examData = await examApi.getExam(examCode)
      setExam(examData)

      const [eq, bankRes, score] = await Promise.all([
        questionApi.getExamQuestions(examCode),
        questionApi.getQuestionBank(examData.courseCode, 0, 200),
        questionApi.getTotalScore(examCode),
      ])
      setExamQuestions(eq)
      setBank(bankRes.content)
      setTotalScore(score)
    } catch (err) {
      setError(err.message)
    }
  }, [examCode])

  useEffect(() => {
    load()
  }, [load])

  const inExamCodes = new Set((examQuestions || []).map((q) => q.questionCode))
  const availableFromBank = bank.filter((q) => !inExamCodes.has(q.questionCode))

  async function handleRemove(questionCode) {
    if (!confirm('این سوال از آزمون حذف شود؟')) return
    setError('')
    try {
      await questionApi.removeQuestionFromExam(examCode, questionCode)
      load()
    } catch (err) {
      setError(err.message)
    }
  }

  async function handleScoreChange(questionCode, score) {
    setError('')
    try {
      await questionApi.updateQuestionScoreInExam(examCode, questionCode, Number(score))
      load()
    } catch (err) {
      setError(err.message)
    }
  }

  if (!exam) return error ? <div className="alert alert-error">{error}</div> : <Spinner label="در حال بارگذاری…" />

  return (
    <div>
      <div className="page-header">
        <div>
          <span className="eyebrow">
            <Link to="/professor/exams">← بازگشت به آزمون‌های من</Link>
          </span>
          <h2>{exam.title}</h2>
          <p className="sub">
            سوالات آزمون · مدت زمان {exam.durationMinutes} دقیقه ·{' '}
            <span className="score-chip">سقف نمره: {totalScore ?? '—'}</span>
          </p>
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <button className="btn btn-outline" onClick={() => setShowAddFromBank(true)}>
            افزودن از بانک سوالات
          </button>
          <button className="btn btn-brass" onClick={() => setShowNewQuestion(true)}>
            + سوال کاملاً جدید
          </button>
        </div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}
      {notice && <div className="alert alert-success">{notice}</div>}

      {!examQuestions ? (
        <Spinner label="در حال بارگذاری سوالات…" />
      ) : examQuestions.length === 0 ? (
        <div className="card">
          <EmptyState glyph="۰" title="هنوز سوالی به این آزمون اضافه نشده" hint="از دکمه‌های بالا استفاده کنید." />
        </div>
      ) : (
        <div className="table-wrap card">
          <table className="data-table">
            <thead>
              <tr>
                <th>عنوان</th>
                <th>متن</th>
                <th>نوع</th>
                <th>نمره در این آزمون</th>
                <th>عملیات</th>
              </tr>
            </thead>
            <tbody>
              {examQuestions.map((q) => (
                <tr key={q.questionCode}>
                  <td>{q.title}</td>
                  <td style={{ maxWidth: 300 }}>{q.text}</td>
                  <td>{q.type === 'MULTIPLE_CHOICE' ? 'چندگزینه‌ای' : 'تشریحی'}</td>
                  <td>
                    <input
                      type="number"
                      min={0.25}
                      step={0.25}
                      defaultValue={q.score}
                      className="mono"
                      style={{ width: 80, border: '1px solid var(--line-strong)', borderRadius: 6, padding: '6px 8px' }}
                      onBlur={(e) => {
                        if (Number(e.target.value) !== q.score) handleScoreChange(q.questionCode, e.target.value)
                      }}
                    />
                  </td>
                  <td>
                    <button className="btn btn-sm btn-danger" onClick={() => handleRemove(q.questionCode)}>
                      حذف از آزمون
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {showAddFromBank && (
        <AddFromBankModal
          questions={availableFromBank}
          onClose={() => setShowAddFromBank(false)}
          onAdd={async (questionCode, score) => {
            await questionApi.addQuestionToExam(examCode, { questionCode, score: Number(score) })
            setShowAddFromBank(false)
            load()
          }}
        />
      )}

      {showNewQuestion && (
        <NewQuestionModal
          courseCode={exam.courseCode}
          onClose={() => setShowNewQuestion(false)}
          onCreate={async (questionPayload, score) => {
            const created = await questionApi.createQuestion({ ...questionPayload, courseCode: exam.courseCode })
            await questionApi.addQuestionToExam(examCode, { questionCode: created.questionCode, score: Number(score) })
            setShowNewQuestion(false)
            load()
          }}
        />
      )}
    </div>
  )
}

function AddFromBankModal({ questions, onClose, onAdd }) {
  const [selected, setSelected] = useState(questions[0]?.questionCode || '')
  const [score, setScore] = useState(1)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  async function handleSubmit(e) {
    e.preventDefault()
    if (!selected) return
    setSaving(true)
    setError('')
    try {
      await onAdd(selected, score)
    } catch (err) {
      setError(err.message)
      setSaving(false)
    }
  }

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-box" onClick={(e) => e.stopPropagation()}>
        <h3>افزودن سوال از بانک سوالات</h3>
        {error && <div className="alert alert-error">{error}</div>}
        {questions.length === 0 ? (
          <EmptyState glyph="۰" title="سوال دیگری در بانک نیست" hint="همه سوالات این دوره قبلاً به آزمون اضافه شده‌اند." />
        ) : (
          <form onSubmit={handleSubmit}>
            <div className="field">
              <label>سوال</label>
              <select value={selected} onChange={(e) => setSelected(e.target.value)}>
                {questions.map((q) => (
                  <option key={q.questionCode} value={q.questionCode}>
                    {q.title} — {q.type === 'MULTIPLE_CHOICE' ? 'چندگزینه‌ای' : 'تشریحی'}
                  </option>
                ))}
              </select>
            </div>
            <div className="field">
              <label>نمره پیشفرض برای این سوال در این آزمون</label>
              <input type="number" min={0.25} step={0.25} value={score} onChange={(e) => setScore(e.target.value)} />
            </div>
            <div className="modal-actions">
              <button type="button" className="btn btn-outline" onClick={onClose}>انصراف</button>
              <button type="submit" className="btn btn-brass" disabled={saving}>
                {saving ? 'در حال افزودن…' : 'افزودن به آزمون'}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  )
}

function NewQuestionModal({ courseCode, onClose, onCreate }) {
  const [type, setType] = useState('MULTIPLE_CHOICE')
  const [qTitle, setQTitle] = useState('')
  const [text, setText] = useState('')
  const [options, setOptions] = useState(['', ''])
  const [correctIndex, setCorrectIndex] = useState(0)
  const [score, setScore] = useState(1)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  function updateOption(i, value) {
    setOptions((opts) => opts.map((o, idx) => (idx === i ? value : o)))
  }
  function addOption() {
    setOptions((opts) => [...opts, ''])
  }
  function removeOption(i) {
    setOptions((opts) => opts.filter((_, idx) => idx !== i))
    setCorrectIndex((ci) => (ci === i ? 0 : ci > i ? ci - 1 : ci))
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')

    if (type === 'MULTIPLE_CHOICE') {
      const cleanOptions = options.map((o) => o.trim()).filter(Boolean)
      if (cleanOptions.length < 2) {
        setError('برای سوال چندگزینه‌ای حداقل دو گزینه لازم است.')
        return
      }
    }

    setSaving(true)
    try {
      await onCreate(
        {
          title: qTitle,
          text,
          type,
          options: type === 'MULTIPLE_CHOICE' ? options.map((o) => o.trim()).filter(Boolean) : null,
          correctAnswerIndex: type === 'MULTIPLE_CHOICE' ? correctIndex : null,
        },
        score
      )
    } catch (err) {
      setError(err.message)
      setSaving(false)
    }
  }

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-box" onClick={(e) => e.stopPropagation()}>
        <h3>سوال کاملاً جدید</h3>
        <p className="small-note" style={{ marginBottom: 14 }}>
          این سوال به‌صورت خودکار به بانک سوالات این دوره هم اضافه می‌شود.
        </p>
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="field">
            <label>عنوان سوال</label>
            <input required value={qTitle} onChange={(e) => setQTitle(e.target.value)} />
          </div>
          <div className="field">
            <label>نوع سوال</label>
            <select value={type} onChange={(e) => setType(e.target.value)}>
              <option value="MULTIPLE_CHOICE">چندگزینه‌ای</option>
              <option value="DESCRIPTIVE">تشریحی</option>
            </select>
          </div>
          <div className="field">
            <label>متن سوال</label>
            <textarea required value={text} onChange={(e) => setText(e.target.value)} />
          </div>

          {type === 'MULTIPLE_CHOICE' && (
            <div className="field">
              <label>گزینه‌ها (پاسخ صحیح را انتخاب کنید)</label>
              {options.map((opt, i) => (
                <div key={i} style={{ display: 'flex', gap: 8, marginBottom: 8, alignItems: 'center' }}>
                  <input type="radio" name="correct-new" checked={correctIndex === i} onChange={() => setCorrectIndex(i)} />
                  <input
                    style={{ flex: 1 }}
                    value={opt}
                    placeholder={`گزینه ${i + 1}`}
                    onChange={(e) => updateOption(i, e.target.value)}
                  />
                  {options.length > 2 && (
                    <button type="button" className="btn btn-sm btn-danger" onClick={() => removeOption(i)}>
                      حذف
                    </button>
                  )}
                </div>
              ))}
              <button type="button" className="btn btn-sm btn-outline" onClick={addOption}>
                + افزودن گزینه
              </button>
            </div>
          )}

          <div className="field">
            <label>نمره این سوال در این آزمون</label>
            <input type="number" min={0.25} step={0.25} value={score} onChange={(e) => setScore(e.target.value)} />
          </div>

          <div className="modal-actions">
            <button type="button" className="btn btn-outline" onClick={onClose}>انصراف</button>
            <button type="submit" className="btn btn-brass" disabled={saving}>
              {saving ? 'در حال ذخیره…' : 'ساخت و افزودن به آزمون'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
