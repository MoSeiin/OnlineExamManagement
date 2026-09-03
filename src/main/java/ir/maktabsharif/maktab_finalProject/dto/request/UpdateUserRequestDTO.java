package ir.maktabsharif.maktab_finalProject.dto.request;

import ir.maktabsharif.maktab_finalProject.domain.Role;
import ir.maktabsharif.maktab_finalProject.domain.Status;

public record UpdateUserRequestDTO(
        String firstName,
        String lastName,
        String email,
        Integer age,
        Role role,
        Status status
) {
}
