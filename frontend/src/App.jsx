import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './context/AuthContext.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'

import LoginPage from './pages/LoginPage.jsx'
import RegisterPage from './pages/RegisterPage.jsx'
import NotFoundPage from './pages/NotFoundPage.jsx'

import AdminUsersPage from './pages/admin/AdminUsersPage.jsx'
import AdminCoursesPage from './pages/admin/AdminCoursesPage.jsx'
import AdminCourseDetailPage from './pages/admin/AdminCourseDetailPage.jsx'

import ProfessorCoursesPage from './pages/professor/ProfessorCoursesPage.jsx'
import ProfessorExamsPage from './pages/professor/ProfessorExamsPage.jsx'
import ExamEditorPage from './pages/professor/ExamEditorPage.jsx'
import ExamResultsPage from './pages/professor/ExamResultsPage.jsx'
import QuestionBankPage from './pages/professor/QuestionBankPage.jsx'

import StudentCoursesPage from './pages/student/StudentCoursesPage.jsx'
import StudentExamsPage from './pages/student/StudentExamsPage.jsx'
import TakeExamPage from './pages/student/TakeExamPage.jsx'

const ROLE_HOME = {
  ADMIN: '/admin/users',
  PROFESSOR: '/professor/courses',
  STUDENT: '/student/courses',
}

function HomeRedirect() {
  const { user, isAuthenticated } = useAuth()
  if (!isAuthenticated) return <Navigate to="/login" replace />
  return <Navigate to={ROLE_HOME[user.role] || '/login'} replace />
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/" element={<HomeRedirect />} />

      {/* Admin */}
      <Route
        path="/admin/users"
        element={
          <ProtectedRoute roles={['ADMIN']}>
            <AdminUsersPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/courses"
        element={
          <ProtectedRoute roles={['ADMIN']}>
            <AdminCoursesPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/courses/:courseCode"
        element={
          <ProtectedRoute roles={['ADMIN']}>
            <AdminCourseDetailPage />
          </ProtectedRoute>
        }
      />

      {/* Professor */}
      <Route
        path="/professor/courses"
        element={
          <ProtectedRoute roles={['PROFESSOR']}>
            <ProfessorCoursesPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/professor/exams"
        element={
          <ProtectedRoute roles={['PROFESSOR']}>
            <ProfessorExamsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/professor/exams/:examCode"
        element={
          <ProtectedRoute roles={['PROFESSOR']}>
            <ExamEditorPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/professor/exams/:examCode/results"
        element={
          <ProtectedRoute roles={['PROFESSOR']}>
            <ExamResultsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/professor/questions"
        element={
          <ProtectedRoute roles={['PROFESSOR']}>
            <QuestionBankPage />
          </ProtectedRoute>
        }
      />

      {/* Student */}
      <Route
        path="/student/courses"
        element={
          <ProtectedRoute roles={['STUDENT']}>
            <StudentCoursesPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/student/courses/:courseCode/exams"
        element={
          <ProtectedRoute roles={['STUDENT']}>
            <StudentExamsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/student/exams/take/:attemptCode"
        element={
          <ProtectedRoute roles={['STUDENT']}>
            <TakeExamPage />
          </ProtectedRoute>
        }
      />

      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  )
}
