package ir.maktabsharif.maktab_finalProject.repository;

import ir.maktabsharif.maktab_finalProject.domain.Exam;
import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {

    @Query("SELECT COALESCE (SUM(eq.score), 0) FROM ExamQuestion eq WHERE eq.exam.examCode = :examCode")
    Double calculateTotalScoreByExamCode(@Param("examCode") String examCode);

    boolean existsByExam_ExamCodeAndQuestion_QuestionCode(String exam_examCode, String question_questionCode);

    List<ExamQuestion> findByExam(Exam exam);

    Optional<ExamQuestion> findByExamAndQuestion_QuestionCode(Exam exam, String questionCode);
}
