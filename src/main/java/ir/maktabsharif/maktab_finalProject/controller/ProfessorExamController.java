package ir.maktabsharif.maktab_finalProject.controller;

import ir.maktabsharif.maktab_finalProject.dto.response.ExamAttemptSummaryDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.StudentAnswerResponseDTO;
import ir.maktabsharif.maktab_finalProject.service.ExamAttemptService;
import ir.maktabsharif.maktab_finalProject.service.ExamAttemptServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professor/exams")
public class ProfessorExamController {

    private final ExamAttemptService service;

    public ProfessorExamController(ExamAttemptServiceImpl service) {
        this.service = service;
    }

    @GetMapping("/{examCode}/participants")
    public List<ExamAttemptSummaryDTO> participants(@PathVariable String examCode) {
        return service.getExamParticipants(examCode);
    }

    @GetMapping("/attempt/{attemptCode}/answers")
    public List<StudentAnswerResponseDTO> answers(@PathVariable String attemptCode) {
        return service.getStudentAnswers(attemptCode);
    }

    @PostMapping("/answers/{answerCode}/grade")
    public void grade(
            @PathVariable String answerCode,
            @RequestParam Double score
    ) {
        service.gradeDescriptiveAnswer(answerCode, score);
    }
}

