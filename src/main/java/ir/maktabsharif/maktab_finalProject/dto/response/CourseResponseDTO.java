package ir.maktabsharif.maktab_finalProject.dto.response;

import java.time.LocalDate;

public record CourseResponseDTO(
        String courseCode,
        String title ,
        LocalDate startDate ,
        LocalDate endDate
) {
}
