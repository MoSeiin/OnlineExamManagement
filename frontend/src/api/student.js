import { api } from './client.js'

export const studentApi = {
  getMyCourses: () => api.get('/student/exams/my-courses'),
  getCourseExams: (courseCode) => api.get(`/student/exams/course/${courseCode}`),
  startExam: (examCode) => api.post('/student/exams/start', { examCode }),
  resumeExam: (attemptCode) => api.get(`/student/exams/${attemptCode}/resume`),
  saveAnswer: (attemptCode, questionCode, mcqAnswer, descriptiveAnswer) =>
    api.post(`/student/exams/${attemptCode}/questions/${questionCode}/save`, {
      mcqAnswer,
      descriptiveAnswer,
    }),
  submitExam: (examAttemptCode) => api.post('/student/exams/submit', { examAttemptCode }),
}
