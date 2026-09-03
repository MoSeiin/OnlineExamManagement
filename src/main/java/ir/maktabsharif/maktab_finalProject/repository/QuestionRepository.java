package ir.maktabsharif.maktab_finalProject.repository;

import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.Question;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Long> {

    Optional<Question> findByQuestionCode(String questionCode);

    // this query uses for QuestionBank
    Page<Question> findByCourse_CourseCodeAndCourse_Professor_Id(
            String course_courseCode, Long course_professor_id, Pageable pageable
    );


}
