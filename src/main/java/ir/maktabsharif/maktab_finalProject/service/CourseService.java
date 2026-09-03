package ir.maktabsharif.maktab_finalProject.service;

import ir.maktabsharif.maktab_finalProject.domain.Course;
import ir.maktabsharif.maktab_finalProject.dto.request.CourseRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.CourseResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface CourseService {
    CourseResponseDTO addCourse(CourseRequestDTO dto);
    CourseResponseDTO updateCourse(String courseCode, CourseRequestDTO dto);
    void deleteCourse(String courseCode);

    Page<CourseResponseDTO> getAllCourse(Pageable pageable);

    List<CourseResponseDTO> getCoursesByProfessorCode();

    // STUDENT: courses the currently logged-in student is enrolled in
    List<CourseResponseDTO> getMyEnrolledCourses();



}
