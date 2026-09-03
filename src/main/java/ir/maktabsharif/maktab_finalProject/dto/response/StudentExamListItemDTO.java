package ir.maktabsharif.maktab_finalProject.dto.response;

public record StudentExamListItemDTO(
        String examCode,
        String title,
        String description,
        Integer durationMinutes,
        // NOT_STARTED, IN_PROGRESS, or FINISHED (already submitted -> not allowed to retake)
        String attemptStatus,
        Double score, // only populated once FINISHED
        String examAttemptCode // populated when IN_PROGRESS or FINISHED, used to resume/view
) {
}
