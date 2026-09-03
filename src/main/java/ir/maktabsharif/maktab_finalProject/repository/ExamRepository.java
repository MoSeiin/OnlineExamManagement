package ir.maktabsharif.maktab_finalProject.repository;

import ir.maktabsharif.maktab_finalProject.domain.Course;
import ir.maktabsharif.maktab_finalProject.domain.Exam;
import ir.maktabsharif.maktab_finalProject.domain.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamRepository extends JpaRepository<Exam,Long> {

    Optional<Exam> findByExamCode(String examCode);

    List<Exam> findByCourse_CourseCode(String courseCode);

    List<Exam> findByCreator_UserCode(String userCode);



}
