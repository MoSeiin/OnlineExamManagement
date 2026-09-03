package ir.maktabsharif.maktab_finalProject.dto.response;

import ir.maktabsharif.maktab_finalProject.dto.request.ExamQuestionItemDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record ExamRunInfoResponseDTO(
        String examAttemptCode,
        String examCode,
        String examTitle,
        int durationMinutes,
        LocalDateTime startTime,
        LocalDateTime endTime,
        long remainingSeconds,
        List<ExamQuestionItemDTO> questions,
        // پاسخ‌های فعلی دانشجو (برای resume)
       Map<String, StudentTempAnswerDTO> currentAnswers
) {
}
