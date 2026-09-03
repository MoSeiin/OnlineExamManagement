package ir.maktabsharif.maktab_finalProject.repository;

import ir.maktabsharif.maktab_finalProject.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course,Long> {
    Optional<Course> findByCourseCode(String courseCode);

    List<Course> findByProfessor_UserCode(String professorCode);

    boolean existsByCourseCodeAndStudentsUserCode(String courseCode, String studentCode);

    List<Course> findByStudents_UserCode(String studentCode);
}
