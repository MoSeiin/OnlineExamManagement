package ir.maktabsharif.maktab_finalProject.service;

import ir.maktabsharif.maktab_finalProject.config.SecurityUtils;
import ir.maktabsharif.maktab_finalProject.domain.Course;
import ir.maktabsharif.maktab_finalProject.domain.Professor;
import ir.maktabsharif.maktab_finalProject.dto.request.CourseRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.CourseResponseDTO;
import ir.maktabsharif.maktab_finalProject.exception.ResourceNotFoundException;
import ir.maktabsharif.maktab_finalProject.mapper.CourseMapper;
import ir.maktabsharif.maktab_finalProject.repository.CourseRepository;
import ir.maktabsharif.maktab_finalProject.repository.ProfessorRepository;
import ir.maktabsharif.maktab_finalProject.util.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final ProfessorRepository professorRepository;

    public CourseServiceImpl(CourseRepository courseRepository, CourseMapper courseMapper , ProfessorRepository professorRepository) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
        this.professorRepository = professorRepository;
    }

    @Override
    public CourseResponseDTO addCourse(CourseRequestDTO dto) {
        Course course = courseMapper.toResponse(dto);
        Course saved = courseRepository.save(course);
        return courseMapper.toCourseResponseDTO(saved);
    }

    @Override
    public CourseResponseDTO updateCourse(String courseCode, CourseRequestDTO dto) {
        Course course = courseRepository.findByCourseCode(courseCode).orElseThrow(() ->
                new ResourceNotFoundException("Course", "courseCode", courseCode));
        courseMapper.updateCourse(dto, course);
        return courseMapper.toCourseResponseDTO(course);
    }

    @Override
    public void deleteCourse(String courseCode) {
        Course course = courseRepository.findByCourseCode(courseCode).orElseThrow(() ->
                new ResourceNotFoundException("Course", "courseCode", courseCode));
        courseRepository.delete(course);
    }


    @Override
    public Page<CourseResponseDTO> getAllCourse(Pageable pageable) {
        Page<Course> coursePage = courseRepository.findAll(pageable);
        return coursePage.map(courseMapper::toCourseResponseDTO);
    }

    @Override
    public List<CourseResponseDTO> getCoursesByProfessorCode() {

        String professorCode = SecurityUtils.getCurrentUserCode();

        List<Course> courses = courseRepository.findByProfessor_UserCode(professorCode);

        return courses.stream()
                .map(courseMapper::toCourseResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getMyEnrolledCourses() {

        String studentCode = SecurityUtils.getCurrentUserCode();

        List<Course> courses = courseRepository.findByStudents_UserCode(studentCode);

        return courses.stream()
                .map(courseMapper::toCourseResponseDTO)
                .toList();
    }










}
