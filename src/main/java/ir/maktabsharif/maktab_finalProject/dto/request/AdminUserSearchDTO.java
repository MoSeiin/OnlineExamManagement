package ir.maktabsharif.maktab_finalProject.dto.request;

import ir.maktabsharif.maktab_finalProject.domain.Role;
import ir.maktabsharif.maktab_finalProject.domain.Status;

public record AdminUserSearchDTO(
        Role role,
        String firstName,
        String lastName,
        String userName,
        Status status
) {
}
