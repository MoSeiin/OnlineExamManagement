import React from 'react'

const MAP = {
  APPROVED: { cls: 'seal-approved', text: 'تاییدشده' },
  PENDING: { cls: 'seal-pending', text: 'در انتظار تایید' },
  REJECTED: { cls: 'seal-rejected', text: 'ردشده' },
  IN_PROGRESS: { cls: 'seal-progress', text: 'در حال انجام' },
  FINISHED: { cls: 'seal-approved', text: 'پایان‌یافته' },
  NOT_STARTED: { cls: 'seal-pending', text: 'شروع نشده' },
}

export default function StatusBadge({ status }) {
  const info = MAP[status] || { cls: 'seal-progress', text: status }
  return (
    <span className={`seal ${info.cls}`}>
      <span className="dot" />
      {info.text}
    </span>
  )
}
