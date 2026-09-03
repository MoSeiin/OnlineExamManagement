package ir.maktabsharif.maktab_finalProject.repository;

import ir.maktabsharif.maktab_finalProject.domain.AttemptStatus;
import ir.maktabsharif.maktab_finalProject.domain.Exam;
import ir.maktabsharif.maktab_finalProject.domain.ExamAttempt;
import ir.maktabsharif.maktab_finalProject.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    Optional<ExamAttempt> findByExamAttemptCode(String code);

    Optional<ExamAttempt> findByExamAndStudent(Exam exam, Student student);

    List<ExamAttempt> findByExam(Exam exam);

    List<ExamAttempt> findByStatusAndEndTimeBefore(AttemptStatus status, LocalDateTime time);

}
