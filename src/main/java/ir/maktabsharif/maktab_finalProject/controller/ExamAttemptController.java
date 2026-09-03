package ir.maktabsharif.maktab_finalProject.controller;

import ir.maktabsharif.maktab_finalProject.dto.request.StartExamRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.request.SubmitExamRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.CourseResponseDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.ExamRunInfoResponseDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.StudentExamListItemDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.StudentTempAnswerDTO;
import ir.maktabsharif.maktab_finalProject.service.CourseService;
import ir.maktabsharif.maktab_finalProject.service.ExamAttemptService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student/exams")

public class ExamAttemptController {

    private final ExamAttemptService examAttemptService;
    private final CourseService courseService;

    @Autowired
    public ExamAttemptController(ExamAttemptService examAttemptService, CourseService courseService) {
        this.examAttemptService = examAttemptService;
        this.courseService = courseService;
    }

    // STUDENT: list courses I'm enrolled in
    @GetMapping("/my-courses")
    public ResponseEntity<List<CourseResponseDTO>> getMyCourses() {
        return ResponseEntity.ok(courseService.getMyEnrolledCourses());
    }

    // STUDENT: list exams in one of my courses, with whether I can still take
    // each one (NOT_STARTED / IN_PROGRESS / FINISHED)
    @GetMapping("/course/{courseCode}")
    public ResponseEntity<List<StudentExamListItemDTO>> getExamsForCourse(
            @PathVariable String courseCode
    ) {
        return ResponseEntity.ok(examAttemptService.getExamsForStudent(courseCode));
    }

    @PostMapping("/start")
    public ResponseEntity<ExamRunInfoResponseDTO> startExam(
            @RequestBody StartExamRequestDTO request
    ) {
        ExamRunInfoResponseDTO response =
                examAttemptService.startExam(request.examCode());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{attemptCode}/resume")
    public ResponseEntity<ExamRunInfoResponseDTO> resumeExam(
            @PathVariable String attemptCode
    ) {
        return ResponseEntity.ok(
                examAttemptService.resumeExam(attemptCode)
        );
    }


    @PostMapping("/{attemptCode}/questions/{questionCode}/save")
    public ResponseEntity<Void> saveTempAnswer(
            @PathVariable String attemptCode,
            @PathVariable String questionCode,
            @RequestBody StudentTempAnswerDTO request
    ) {
        examAttemptService.saveTempAnswer(
                attemptCode,
                questionCode,
                request.mcqAnswer(),
                request.descriptiveAnswer()
        );

        return ResponseEntity.ok().build();
    }


    @PostMapping("/submit")
    public ResponseEntity<Double> submitExam(
            @RequestBody SubmitExamRequestDTO request
    ) {
        double finalScore =
                examAttemptService.submitExam(request.examAttemptCode());

        return ResponseEntity.ok(finalScore);
    }
}
