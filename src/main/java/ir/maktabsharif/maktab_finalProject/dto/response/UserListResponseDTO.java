package ir.maktabsharif.maktab_finalProject.dto.response;

import ir.maktabsharif.maktab_finalProject.domain.Role;
import ir.maktabsharif.maktab_finalProject.domain.Status;

public record UserListResponseDTO(
        String userCode,
        String firstName ,
        String lastName,
        String email,
        Role role,
        Status status,
        Integer age
) {
}
