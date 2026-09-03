package ir.maktabsharif.maktab_finalProject.dto.response;

public record StudentAnswerResponseDTO(
        String studentAnswerCode,
        String questionCode,
        String questionTitle,
        String questionText,
        String questionType, // MULTIPLE_CHOICE / DESCRIPTIVE
        java.util.List<String> options, // فقط برای چندگزینه‌ای
        Integer correctAnswerIndex, // فقط برای چندگزینه‌ای؛ برای مرجع استاد
        Integer mcqAnswer,
        String descriptiveAnswer,
        Double autoScore,
        Double manualScore,
        Double maxScore
) {
}
