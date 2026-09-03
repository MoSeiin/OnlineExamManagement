package ir.maktabsharif.maktab_finalProject.dto.response;

public record ExamFullResponseDTO(

        String examCode,
        String title,
        String description,
        Integer durationMinutes,
        String courseCode,
        String courseTitle,
        String creatorCode,
        String creatorName,
        String createdAt
)

 {
}
