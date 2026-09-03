package ir.maktabsharif.maktab_finalProject.dto.request;

import ir.maktabsharif.maktab_finalProject.domain.Role;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDTO(

        String firstName,

        String lastName,

        @NotNull
        String userName,

        @NotNull
        String email,

        @NotNull
        String password,

        Integer age,

        @NotNull
        Role role
) {
}
