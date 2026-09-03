import React from 'react'
import { Link } from 'react-router-dom'

export default function NotFoundPage() {
  return (
    <div className="center-page" style={{ flexDirection: 'column', gap: 14 }}>
      <div className="brand-seal" style={{ borderColor: 'var(--clay)', color: 'var(--clay)', width: 60, height: 60, fontSize: 20 }}>
        ۴۰۴
      </div>
      <h2>این صفحه پیدا نشد</h2>
      <Link to="/" className="btn btn-outline">
        بازگشت به خانه
      </Link>
    </div>
  )
}
