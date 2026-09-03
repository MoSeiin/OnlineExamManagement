import React from 'react'
import { NavLink } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

const NAV_BY_ROLE = {
  ADMIN: [
    { to: '/admin/users', label: 'کاربران', icon: '۱' },
    { to: '/admin/courses', label: 'دوره‌ها', icon: '۲' },
  ],
  PROFESSOR: [
    { to: '/professor/courses', label: 'دوره‌های من', icon: '۱' },
    { to: '/professor/exams', label: 'آزمون‌های من', icon: '۲' },
    { to: '/professor/questions', label: 'بانک سوالات', icon: '۳' },
  ],
  STUDENT: [
    { to: '/student/courses', label: 'دوره‌های من', icon: '۱' },
  ],
}

const ROLE_LABEL = {
  ADMIN: 'مدیر سامانه',
  PROFESSOR: 'استاد',
  STUDENT: 'دانشجو',
}

export default function AppShell({ children }) {
  const { user, logout } = useAuth()
  const links = NAV_BY_ROLE[user?.role] || []

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="sidebar-brand">
          <div className="brand-seal">OC</div>
          <div className="sidebar-brand-text">
            <h1>Online Course & Exam Management</h1>
          </div>
        </div>

        <nav className="sidebar-nav">
          {links.map((l) => (
            <NavLink
              key={l.to}
              to={l.to}
              className={({ isActive }) => (isActive ? 'active' : '')}
            >
              {l.label}
            </NavLink>
          ))}
        </nav>

        <div className="sidebar-footer">
          <div className="sidebar-user">
            <strong>{ROLE_LABEL[user?.role] || user?.role}</strong>
            <span className="ltr">{user?.userName}</span>
          </div>
          <button className="logout-btn" onClick={logout}>
            خروج از حساب
          </button>
        </div>
      </aside>

      <main className="main-area">{children}</main>
    </div>
  )
}
