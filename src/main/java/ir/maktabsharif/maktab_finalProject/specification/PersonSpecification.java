package ir.maktabsharif.maktab_finalProject.specification;

import ir.maktabsharif.maktab_finalProject.domain.Person;
import ir.maktabsharif.maktab_finalProject.dto.request.AdminUserSearchDTO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class PersonSpecification {
    public static Specification<Person> search(AdminUserSearchDTO dto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (dto.firstName() != null) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("firstName")),
                                "%" + dto.firstName().toLowerCase() + "%"
                        )
                );
            }
            if (dto.lastName() != null) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("lastName")),
                                "%" + dto.lastName().toLowerCase() + "%"
                        )
                );
            }
            if (dto.userName() != null) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("userName")),
                                "%" + dto.userName().toLowerCase() + "%"
                        )
                );
            }
            if (dto.role() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("role"), dto.role())
                );
            }

            if (dto.status() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("status"), dto.status())
                );
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
