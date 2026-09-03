package ir.maktabsharif.maktab_finalProject.dto.request;

public record UpdateExamRequestDTO(
        String description ,
        String title ,
        Integer durationMinutes
) {
}
