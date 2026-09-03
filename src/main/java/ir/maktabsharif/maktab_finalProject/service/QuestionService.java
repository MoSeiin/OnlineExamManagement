package ir.maktabsharif.maktab_finalProject.service;

import ir.maktabsharif.maktab_finalProject.dto.request.AddQuestionToExamRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.request.QuestionCreateRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.request.QuestionUpdateRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.QuestionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface QuestionService {
    QuestionResponseDTO createQuestion(QuestionCreateRequestDTO request);

    Page<QuestionResponseDTO> getQuestionBank(String courseCode, Pageable pageable);



    void addQuestionToExam(String examCode, AddQuestionToExamRequestDTO request);

    QuestionResponseDTO updateQuestion(String questionCode, QuestionUpdateRequestDTO request);

    Double calculateScore(String examCode);

    java.util.List<ir.maktabsharif.maktab_finalProject.dto.response.ExamQuestionResponseDTO> getExamQuestions(String examCode);

    void removeQuestionFromExam(String examCode, String questionCode);

    void updateQuestionScoreInExam(String examCode, String questionCode, Double score);

}
