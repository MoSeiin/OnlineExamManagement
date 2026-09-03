package ir.maktabsharif.maktab_finalProject.service;

import ir.maktabsharif.maktab_finalProject.domain.Exam;
import ir.maktabsharif.maktab_finalProject.domain.ExamAttempt;
import ir.maktabsharif.maktab_finalProject.domain.Student;
import ir.maktabsharif.maktab_finalProject.domain.StudentAnswer;
import ir.maktabsharif.maktab_finalProject.dto.request.SaveTempAnswerRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.request.StartExamRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.ExamAttemptSummaryDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.ExamRunInfoResponseDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.StudentAnswerResponseDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.StudentExamListItemDTO;
import ir.maktabsharif.maktab_finalProject.dto.request.SubmitExamRequestDTO;

import java.util.List;

public interface ExamAttemptService {

    ExamRunInfoResponseDTO startExam(String examCode);

    ExamRunInfoResponseDTO resumeExam(String attemptCode);

    void saveTempAnswer(
            String attemptCode,
            String questionCode,
            Integer mcq,
            String desc
    );


    double submitExam(String attemptCode);

    List<ExamAttemptSummaryDTO> getExamParticipants(String examCode);

    List<StudentAnswerResponseDTO> getStudentAnswers(String attemptCode);

    void gradeDescriptiveAnswer(String studentAnswerCode, Double score);

    // STUDENT: list exams in a course the student is enrolled in, along with
    // whether they still can take it, are mid-attempt, or already finished.
    List<StudentExamListItemDTO> getExamsForStudent(String courseCode);

}
