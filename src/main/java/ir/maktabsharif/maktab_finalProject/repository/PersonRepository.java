package ir.maktabsharif.maktab_finalProject.repository;

import ir.maktabsharif.maktab_finalProject.domain.Person;
import ir.maktabsharif.maktab_finalProject.dto.request.AdminUserSearchDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.UserListResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person,Long> , JpaSpecificationExecutor<Person> {

    Optional<Person> findByUserNameIgnoreCase(String userName);

    Optional<Person> findByUserCode(String userCode);

    Optional<Person> findByEmail(String email);


    Optional<Person> findByUserName(String userName);
}
