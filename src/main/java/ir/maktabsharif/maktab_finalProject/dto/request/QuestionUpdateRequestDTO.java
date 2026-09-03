package ir.maktabsharif.maktab_finalProject.dto.request;

import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.QuestionType;

import java.util.List;

public record QuestionUpdateRequestDTO(
        String title,
        String text,
        QuestionType type,
        List<String> options,
        Integer correctAnswerIndex,
        String courseCode
) {
}
