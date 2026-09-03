package ir.maktabsharif.maktab_finalProject.dto.request;

import java.time.LocalDate;

public record CourseRequestDTO(
        String title,
        LocalDate startDate,
        LocalDate endDate
) {
}
