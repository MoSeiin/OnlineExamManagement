import React, { useEffect, useState, useCallback } from 'react'
import { useSearchParams } from 'react-router-dom'
import { examApi, questionApi } from '../../api/professor.js'
import Spinner from '../../components/Spinner.jsx'
import EmptyState from '../../components/EmptyState.jsx'

export default function QuestionBankPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [courses, setCourses] = useState([])
  const [courseCode, setCourseCode] = useState(searchParams.get('course') || '')
  const [questions, setQuestions] = useState(null)
  const [error, setError] = useState('')
  const [showCreate, setShowCreate] = useState(false)
  const [editing, setEditing] = useState(null)

  useEffect(() => {
    examApi.getMyCourses().then((list) => {
      setCourses(list)
      if (!courseCode && list.length > 0) {
        setCourseCode(list[0].courseCode)
      }
    }).catch((e) => setError(e.message))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const load = useCallback(async () => {
    if (!courseCode) return
    setError('')
    try {
      const res = await questionApi.getQuestionBank(courseCode)
      setQuestions(res.content)
    } catch (err) {
      setError(err.message)
    }
  }, [courseCode])

  useEffect(() => {
    load()
    if (courseCode) setSearchParams({ course: courseCode })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [courseCode])

  return (
    <div>
      <div className="page-header">
        <div>
          <span className="eyebrow">پنل استاد</span>
          <h2>بانک سوالات</h2>
          <p className="sub">سوالاتی که برای هر دوره طراحی کرده‌اید — قابل استفاده در چند آزمون</p>
        </div>
        {courseCode && (
          <button className="btn btn-brass" onClick={() => setShowCreate(true)}>
            + سوال جدید
          </button>
        )}
      </div>

      {courses.length > 0 && (
        <div className="pill-select" style={{ marginBottom: 18 }}>
          {courses.map((c) => (
            <button
              key={c.courseCode}
              className={c.courseCode === courseCode ? 'active' : ''}
              onClick={() => setCourseCode(c.courseCode)}
            >
              {c.title}
            </button>
          ))}
        </div>
      )}

      {error && <div className="alert alert-error">{error}</div>}

      {!courseCode ? (
        <div className="card">
          <EmptyState glyph="؟" title="ابتدا یک دوره داشته باشید" hint="بانک سوالات به‌ازای هر دوره جداست." />
        </div>
      ) : !questions ? (
        <Spinner label="در حال بارگذاری…" />
      ) : questions.length === 0 ? (
        <div className="card">
          <EmptyState glyph="۰" title="این بانک هنوز خالی است" hint="با «سوال جدید» اولین سوال این دوره را بسازید." />
        </div>
      ) : (
        <div className="table-wrap card">
          <table className="data-table">
            <thead>
              <tr>
                <th>عنوان</th>
                <th>متن سوال</th>
                <th>نوع</th>
                <th>عملیات</th>
              </tr>
            </thead>
            <tbody>
              {questions.map((q) => (
                <tr key={q.questionCode}>
                  <td>{q.title}</td>
                  <td style={{ maxWidth: 340 }}>{q.text}</td>
                  <td>{q.type === 'MULTIPLE_CHOICE' ? 'چندگزینه‌ای' : 'تشریحی'}</td>
                  <td>
                    <button className="btn btn-sm btn-outline" onClick={() => setEditing(q)}>
                      ویرایش
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {showCreate && (
        <QuestionFormModal
          title="سوال جدید"
          courseCode={courseCode}
          onClose={() => setShowCreate(false)}
          onSave={async (payload) => {
            await questionApi.createQuestion({ ...payload, courseCode })
            setShowCreate(false)
            load()
          }}
        />
      )}

      {editing && (
        <QuestionFormModal
          title="ویرایش سوال"
          initial={editing}
          courseCode={courseCode}
          onClose={() => setEditing(null)}
          onSave={async (payload) => {
            await questionApi.updateQuestion(editing.questionCode, { ...payload, courseCode })
            setEditing(null)
            load()
          }}
        />
      )}
    </div>
  )
}

function QuestionFormModal({ title, initial, courseCode, onClose, onSave }) {
  const [type, setType] = useState(initial?.type || 'MULTIPLE_CHOICE')
  const [qTitle, setQTitle] = useState(initial?.title || '')
  const [text, setText] = useState(initial?.text || '')
  const [options, setOptions] = useState(initial?.options?.length ? initial.options : ['', ''])
  const [correctIndex, setCorrectIndex] = useState(0)
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
      if (correctIndex >= cleanOptions.length) {
        setError('گزینه صحیح را مشخص کنید.')
        return
      }
    }

    setSaving(true)
    try {
      await onSave({
        title: qTitle,
        text,
        type,
        options: type === 'MULTIPLE_CHOICE' ? options.map((o) => o.trim()).filter(Boolean) : null,
        correctAnswerIndex: type === 'MULTIPLE_CHOICE' ? correctIndex : null,
      })
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
            <label>عنوان (فیلد اجباری، برای شناسایی سریع در بانک سوالات)</label>
            <input required value={qTitle} onChange={(e) => setQTitle(e.target.value)} />
          </div>

          <div className="field">
            <label>نوع سوال</label>
            <select value={type} onChange={(e) => setType(e.target.value)} disabled={!!initial}>
              <option value="MULTIPLE_CHOICE">چندگزینه‌ای</option>
              <option value="DESCRIPTIVE">تشریحی</option>
            </select>
            {initial && <span className="small-note">نوع سوال پس از ساخت قابل تغییر نیست.</span>}
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
                  <input
                    type="radio"
                    name="correct"
                    checked={correctIndex === i}
                    onChange={() => setCorrectIndex(i)}
                  />
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

          <div className="modal-actions">
            <button type="button" className="btn btn-outline" onClick={onClose}>
              انصراف
            </button>
            <button type="submit" className="btn btn-brass" disabled={saving}>
              {saving ? 'در حال ذخیره…' : 'ذخیره سوال'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
