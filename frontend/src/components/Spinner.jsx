import React from 'react'

export default function Spinner({ label }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 10, color: 'var(--graphite)' }}>
      <span className="spinner" />
      {label && <span style={{ fontSize: 13.5 }}>{label}</span>}
    </div>
  )
}

export function FullPageSpinner() {
  return (
    <div className="center-page">
      <Spinner label="در حال بارگذاری…" />
    </div>
  )
}
