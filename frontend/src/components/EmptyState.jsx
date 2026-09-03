import React from 'react'

export default function EmptyState({ glyph = '؟', title, hint }) {
  return (
    <div className="empty-state">
      <div className="glyph">{glyph}</div>
      <p style={{ fontWeight: 700, color: 'var(--ink)', marginBottom: 6 }}>{title}</p>
      {hint && <p style={{ fontSize: 13 }}>{hint}</p>}
    </div>
  )
}
