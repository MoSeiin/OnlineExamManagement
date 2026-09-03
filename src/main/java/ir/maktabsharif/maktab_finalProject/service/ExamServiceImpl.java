package ir.maktabsharif.maktab_finalProject.service;

import ir.maktabsharif.maktab_finalProject.config.SecurityUtils;
import ir.maktabsharif.maktab_finalProject.domain.Course;
import ir.maktabsharif.maktab_finalProject.domain.Exam;
import ir.maktabsharif.maktab_finalProject.domain.Professor;
import ir.maktabsharif.maktab_finalProject.dto.request.ExamCreateRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.request.UpdateExamRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.ExamFullResponseDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.ExamResponseDTO;
import ir.maktabsharif.maktab_finalProject.exception.BadRequestException;
import ir.maktabsharif.maktab_finalProject.exception.ResourceNotFoundException;
import ir.maktabsharif.maktab_finalProject.mapper.ExamMapper;
import ir.maktabsharif.maktab_finalProject.repository.CourseRepository;
import ir.maktabsharif.maktab_finalProject.repository.ExamRepository;
import ir.maktabsharif.maktab_finalProject.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExamServiceImpl implements ExamService {


    private final ExamRepository examRepository;
    private final CourseRepository courseRepository;
    private final ProfessorRepository professorRepository;
    private final ExamMapper examMapper;

    @Autowired
    public ExamServiceImpl(ExamRepository examRepository,
                           CourseRepository courseRepository,
                           ProfessorRepository professorRepository, ExamMapper examMapper) {
        this.examRepository = examRepository;
        this.courseRepository = courseRepository;
        this.professorRepository = professorRepository;
        this.examMapper = examMapper;
    }

    @Override
    public ExamResponseDTO createExam(ExamCreateRequestDTO dto) {
        Course course = findCourseByCourseCode(dto.courseCode());
        String professorCode = getProfessorCode();
        Professor professor = findProfessorByUserCode(professorCode);
        validateCourseOwner(course, professorCode);
        Exam exam = examMapper.toEntity(dto);
        exam.setCourse(course);
        exam.setCreator(professor);
        examRepository.save(exam);
        return examMapper.toResponseDTO(exam);

    }


    @Override
    public ExamResponseDTO updateExam(String examCode, UpdateExamRequestDTO dto) {
        String professorCode = getProfessorCode();
        Exam exam = findExamByExamCode(examCode);
        validateExamOwner(exam, professorCode);
        examMapper.updateExamFromDto(dto, exam);
        return examMapper.toResponseDTO(exam);
    }

    @Override
    public void deleteExam(String examCode) {
        String professorCode = getProfessorCode();
        Exam exam = findExamByExamCode(examCode);
        validateExamOwner(exam, professorCode);
        examRepository.delete(exam);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResponseDTO getExam(String examCode) {
        Exam exam = findExamByExamCode(examCode);
        return examMapper.toResponseDTO(exam);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponseDTO> getCourseExams(String courseCode) {
        return examRepository.findByCourse_CourseCode(courseCode)
                .stream()
                .map(examMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<ExamFullResponseDTO> getProfessorExams() {
        String professorCode = getProfessorCode();
        List<Exam> exams = examRepository.findByCreator_UserCode(professorCode);

        return exams.stream()
                .map(examMapper::toExamDetailsResponseDTO)
                .toList();
    }

    //helper methods :

    private String getProfessorCode() {
        return SecurityUtils.getCurrentUserCode();
    }


    private Professor findProfessorByUserCode(String userCode) {
        return professorRepository.findByUserCode(userCode).orElseThrow(() ->
                new ResourceNotFoundException("professor ", "userCode", userCode));
    }

    private Course findCourseByCourseCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode).orElseThrow(() ->
                new ResourceNotFoundException("course ", "code", courseCode));
    }

    private Exam findExamByExamCode(String examCode) {
        return examRepository.findByExamCode(examCode).orElseThrow(() ->
                new ResourceNotFoundException("exam ", "code", examCode));
    }

    private void validateCourseOwner(Course course, String professorCode) {
        if (course.getProfessor() == null || course.getProfessor().getUserCode() == null) {
            throw new BadRequestException("This course has no assigned professor");
        }

        if (!course.getProfessor().getUserCode().equals(professorCode)) {
            throw new BadRequestException("You are not the owner of this course");
        }
    }

    private void validateExamOwner(Exam exam, String professorCode) {
        if (exam.getCreator() == null || exam.getCreator().getUserCode() == null) {
            throw new BadRequestException("This exam has no creator");
        }

        if (!exam.getCreator().getUserCode().equals(professorCode)) {
            throw new BadRequestException("You are not the creator of this exam");
        }
    }


}
