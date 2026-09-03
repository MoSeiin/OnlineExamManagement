package ir.maktabsharif.maktab_finalProject.repository;

import ir.maktabsharif.maktab_finalProject.domain.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {

        Optional<StudentAnswer> findByStudentAnswerCode(String studentAnswerCode);

}
