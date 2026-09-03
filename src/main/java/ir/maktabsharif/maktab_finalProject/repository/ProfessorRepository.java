package ir.maktabsharif.maktab_finalProject.repository;

import ir.maktabsharif.maktab_finalProject.domain.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor,Long> {

    Optional<Professor> findByUserCode(String userCode);
}
