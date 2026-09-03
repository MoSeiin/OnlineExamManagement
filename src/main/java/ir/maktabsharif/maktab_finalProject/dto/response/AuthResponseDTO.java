package ir.maktabsharif.maktab_finalProject.dto.response;

import ir.maktabsharif.maktab_finalProject.domain.Role;
import ir.maktabsharif.maktab_finalProject.domain.Status;

public record AuthResponseDTO(
        String token,
        String userCode,
        String userName,
        Role role,
        Status status
) {
}
