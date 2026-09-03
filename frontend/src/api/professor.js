import { api } from './client.js'

export const examApi = {
  // professor scope
  getMyCourses: () => api.get('/exams/my-courses'),
  getMyExams: () => api.get('/exams/my-exams'),
  getCourseExams: (courseCode) => api.get(`/exams/course/${courseCode}`),
  getExam: (examCode) => api.get(`/exams/${examCode}`),
  createExam: (payload) => api.post('/exams', payload),
  updateExam: (examCode, payload) => api.put(`/exams/${examCode}`, payload),
  deleteExam: (examCode) => api.del(`/exams/${examCode}`),
}

export const questionApi = {
  createQuestion: (payload) => api.post('/questions', payload),
  updateQuestion: (questionCode, payload) => api.put(`/questions/${questionCode}`, payload),
  getQuestionBank: (courseCode, page = 0, size = 50) =>
    api.get('/questions/bank', { courseCode, page, size }),
  addQuestionToExam: (examCode, payload) => api.post(`/questions/exam/${examCode}`, payload),
  getTotalScore: (examCode) => api.get(`/questions/${examCode}/total-score`),
  getExamQuestions: (examCode) => api.get(`/questions/exam/${examCode}`),
  removeQuestionFromExam: (examCode, questionCode) =>
    api.del(`/questions/exam/${examCode}/${questionCode}`),
  updateQuestionScoreInExam: (examCode, questionCode, score) =>
    api.put(`/questions/exam/${examCode}/${questionCode}/score`, undefined, { score }),
}

export const professorGradingApi = {
  getParticipants: (examCode) => api.get(`/professor/exams/${examCode}/participants`),
  getAnswers: (attemptCode) => api.get(`/professor/exams/attempt/${attemptCode}/answers`),
  gradeAnswer: (answerCode, score) =>
    api.post(`/professor/exams/answers/${answerCode}/grade`, undefined, { score }),
}
