package ir.maktabsharif.maktab_finalProject.dto.request;

import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;


public record QuestionCreateRequestDTO(
        @NotBlank(message = "title required")
        String title,

        @NotBlank(message = "text required")
        String text,

        @NotNull(message = "type required")
        QuestionType type,

        String courseCode,

        List<String> options,

        Integer correctAnswerIndex
) {

}
