package ir.maktabsharif.maktab_finalProject.dto.response;

import java.time.LocalDateTime;

public record ExamAttemptSummaryDTO(
        String examAttemptCode,
        String studentCode,
        String studentFirstName,
        String studentLastName,
        String status,
        Double score,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
