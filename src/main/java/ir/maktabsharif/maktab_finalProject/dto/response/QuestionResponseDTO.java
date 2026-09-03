package ir.maktabsharif.maktab_finalProject.dto.response;

import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.QuestionType;

import java.util.List;
import java.util.UUID;

public record QuestionResponseDTO(

        // we do not put correctAnswerIndex because its leaked the right answer

         String questionCode,

        String title,

        String text,

        QuestionType type,

        String courseCode,

        List<String> options

) {
}
