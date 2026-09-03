package ir.maktabsharif.maktab_finalProject.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SubmitExamRequestDTO(

        @NotBlank(message = "examAttemptCode is required")
        String examAttemptCode
) {
}
