package ir.maktabsharif.maktab_finalProject.dto.request;

public record ExamCreateRequestDTO(
        String courseCode ,
        String title ,
        String description,
        Integer durationMinutes
) {
}
