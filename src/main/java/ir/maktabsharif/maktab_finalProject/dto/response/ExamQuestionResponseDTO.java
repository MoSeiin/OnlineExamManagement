package ir.maktabsharif.maktab_finalProject.dto.response;

public record ExamQuestionResponseDTO(
        String questionCode,
        String title,
        String text,
        String type, // MULTIPLE_CHOICE | DESCRIPTIVE
        Double score
) {
}
