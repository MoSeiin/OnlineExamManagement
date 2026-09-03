import { api } from './client.js'

export const adminApi = {
  // users
  getUsers: (page = 0, size = 10) => api.get('/admin/users', { page, size }),
  searchUsers: (filters, page = 0, size = 10) =>
    api.get('/admin/users/search', { ...filters, page, size }),
  approveUser: (userCode) => api.put(`/admin/user/${userCode}/approve`),
  rejectUser: (userCode) => api.put(`/admin/user/${userCode}/reject`),
  updateUser: (userCode, payload) => api.put(`/admin/user/${userCode}`, payload),

  // courses
  getCourses: (page = 0, size = 12) => api.get('/admin/courses', { page, size }),
  addCourse: (payload) => api.post('/admin/courses', payload),
  updateCourse: (courseCode, payload) => api.put(`/admin/courses/${courseCode}`, payload),
  deleteCourse: (courseCode) => api.del(`/admin/courses/${courseCode}`),
  getCourseDetails: (courseCode) => api.get(`/admin/courses/${courseCode}`),

  // course membership
  assignPersonToCourse: (courseCode, userCode) =>
    api.put(`/admin/course/${courseCode}/users/${userCode}`),
  changeCourseProfessor: (courseCode, userCode) =>
    api.patch(`/admin/course/${courseCode}/updatedProfessor/${userCode}`),
  removePersonFromCourse: (courseCode, userCode) =>
    api.del(`/admin/course/${courseCode}/users/${userCode}`),
}
