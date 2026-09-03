package ir.maktabsharif.maktab_finalProject.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SaveTempAnswerRequestDTO(
        @NotBlank(message = "examAttemptCode is required")
        String examAttemptCode,

        @NotBlank(message = "questionCode is required")
        String questionCode,

        Integer mcqAnswer,
        String descriptiveAnswer
) {
}
