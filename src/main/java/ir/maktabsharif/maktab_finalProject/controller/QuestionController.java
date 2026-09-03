package ir.maktabsharif.maktab_finalProject.controller;

import ir.maktabsharif.maktab_finalProject.dto.request.AddQuestionToExamRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.request.QuestionCreateRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.request.QuestionUpdateRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.QuestionResponseDTO;
import ir.maktabsharif.maktab_finalProject.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    //✅
    @PostMapping
    public ResponseEntity<QuestionResponseDTO> createQuestion(
            @RequestBody @Valid QuestionCreateRequestDTO request
    ) {

        QuestionResponseDTO response = questionService.createQuestion(request);

        return ResponseEntity.ok(response);
    }

    //✅
    @GetMapping("/bank")
    public ResponseEntity<Page<QuestionResponseDTO>> getQuestionBank(
            @RequestParam String courseCode,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                questionService.getQuestionBank((courseCode), pageable)
        );
    }


    @PutMapping("/{questionCode}")
    public ResponseEntity<QuestionResponseDTO> updateQuestion(
            @PathVariable String questionCode,
            @RequestBody @Valid QuestionUpdateRequestDTO request
    ) {
        return ResponseEntity.ok(
                questionService.updateQuestion(questionCode, request)
        );


    }

    @PostMapping("/exam/{examCode}")
    public ResponseEntity<Void> addQuestionToExam(
            @PathVariable String examCode,
            @RequestBody @Valid AddQuestionToExamRequestDTO request
    ) {

        questionService.addQuestionToExam(examCode, request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{examCode}/total-score")

    public ResponseEntity<Double> getTotalScore(@PathVariable String examCode) {

        Double totalScore = questionService.calculateScore(examCode);

        return ResponseEntity.ok(totalScore);
    }

    @GetMapping("/exam/{examCode}")
    public ResponseEntity<java.util.List<ir.maktabsharif.maktab_finalProject.dto.response.ExamQuestionResponseDTO>> getExamQuestions(
            @PathVariable String examCode
    ) {
        return ResponseEntity.ok(questionService.getExamQuestions(examCode));
    }

    @DeleteMapping("/exam/{examCode}/{questionCode}")
    public ResponseEntity<Void> removeQuestionFromExam(
            @PathVariable String examCode,
            @PathVariable String questionCode
    ) {
        questionService.removeQuestionFromExam(examCode, questionCode);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/exam/{examCode}/{questionCode}/score")
    public ResponseEntity<Void> updateQuestionScoreInExam(
            @PathVariable String examCode,
            @PathVariable String questionCode,
            @RequestParam Double score
    ) {
        questionService.updateQuestionScoreInExam(examCode, questionCode, score);
        return ResponseEntity.noContent().build();
    }
}