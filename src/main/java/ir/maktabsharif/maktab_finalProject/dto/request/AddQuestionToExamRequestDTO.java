package ir.maktabsharif.maktab_finalProject.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddQuestionToExamRequestDTO(
        @NotNull(message = "questionCode required")
        String questionCode,

        @NotNull(message = "score required")
        Double score
) {
}
