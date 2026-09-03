package ir.maktabsharif.maktab_finalProject.dto.request;

import jakarta.validation.constraints.NotBlank;

public record StartExamRequestDTO(
        @NotBlank(message = "examCode is required")
        String examCode
) {
}
