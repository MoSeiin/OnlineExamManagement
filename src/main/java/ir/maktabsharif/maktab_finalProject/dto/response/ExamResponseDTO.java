package ir.maktabsharif.maktab_finalProject.dto.response;

public record ExamResponseDTO(
        String examCode,
        String title,
        String description,
        Integer durationMinutes,
        String courseCode,
        String creatorCode
) {
}
