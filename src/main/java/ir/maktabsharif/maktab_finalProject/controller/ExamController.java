package ir.maktabsharif.maktab_finalProject.controller;

import ir.maktabsharif.maktab_finalProject.dto.request.ExamCreateRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.request.UpdateExamRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.CourseResponseDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.ExamFullResponseDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.ExamResponseDTO;
import ir.maktabsharif.maktab_finalProject.service.CourseService;
import ir.maktabsharif.maktab_finalProject.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exams")
public class ExamController {

    private final ExamService examService;
    private final CourseService courseService;

    @Autowired
    public ExamController(ExamService examService, CourseService courseService) {
        this.examService = examService;
        this.courseService = courseService;
    }

    @PostMapping()
    public ResponseEntity<ExamResponseDTO> createExam(@Valid @RequestBody ExamCreateRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(examService.createExam(dto));
    }

    @PutMapping("/{examCode}")
    public ResponseEntity<Void> updateExam(@PathVariable String examCode, @RequestBody UpdateExamRequestDTO dto) {

        examService.updateExam(examCode, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{examCode}")
    public ResponseEntity<Void> deleteExam(@PathVariable String examCode) {

        examService.deleteExam(examCode);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{examCode}")
    public ExamResponseDTO getExam(@PathVariable String examCode) {

        return examService.getExam(examCode);
    }

    @GetMapping("/course/{courseCode}")
    public List<ExamResponseDTO> getCourseExams(@PathVariable String courseCode) {

        return examService.getCourseExams(courseCode);
    }

    @GetMapping("/my-courses")
    public ResponseEntity<List<CourseResponseDTO>> getMyCourses() {
        return ResponseEntity.ok(courseService.getCoursesByProfessorCode());
    }

    @GetMapping("/my-exams")
    public ResponseEntity<List<ExamFullResponseDTO>> getMyExams() {
        return ResponseEntity.ok(examService.getProfessorExams());
    }


}
