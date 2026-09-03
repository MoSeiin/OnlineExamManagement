package ir.maktabsharif.maktab_finalProject.dto.request;

import java.util.List;

public record ExamQuestionItemDTO(
        String questionCode,
        String title,
        String text,
        String questionType,
        List<String> options
) {
}
