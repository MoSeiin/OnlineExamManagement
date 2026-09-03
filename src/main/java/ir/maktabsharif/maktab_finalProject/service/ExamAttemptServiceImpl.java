package ir.maktabsharif.maktab_finalProject.service;

import ir.maktabsharif.maktab_finalProject.config.SecurityUtils;
import ir.maktabsharif.maktab_finalProject.domain.*;
import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.ExamQuestion;
import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.MultipleChoiceQuestion;
import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.Question;
import ir.maktabsharif.maktab_finalProject.dto.request.ExamQuestionItemDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.ExamAttemptSummaryDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.ExamRunInfoResponseDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.StudentAnswerResponseDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.StudentExamListItemDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.StudentTempAnswerDTO;
import ir.maktabsharif.maktab_finalProject.exception.BadRequestException;
import ir.maktabsharif.maktab_finalProject.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExamAttemptServiceImpl implements ExamAttemptService {

    private final ExamRepository examRepository;
    private final StudentRepository studentRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final QuestionRepository questionRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final ProfessorRepository professorRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public ExamAttemptServiceImpl(
            ExamRepository examRepository,
            StudentRepository studentRepository,
            ExamAttemptRepository examAttemptRepository,
            QuestionRepository questionRepository,
            StudentAnswerRepository studentAnswerRepository,
            ProfessorRepository professorRepository,
            RedisTemplate<String, String> redisTemplate
    ) {
        this.examRepository = examRepository;
        this.studentRepository = studentRepository;
        this.examAttemptRepository = examAttemptRepository;
        this.questionRepository = questionRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.professorRepository = professorRepository;
        this.redisTemplate = redisTemplate;
    }

    private String redisKey(String attemptCode) {
        return "exam_attempt:" + attemptCode + ":answers";
    }

    // The RedisTemplate here is configured with a StringRedisSerializer for hash
    // values, so we must serialize/deserialize the answer payload to/from JSON
    // ourselves instead of storing a raw Map (which would throw a
    // ClassCastException at runtime when Spring Data Redis tries to serialize it).
    private String serializeAnswer(Integer mcq, String desc) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("mcq", mcq);
            map.put("desc", desc);
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            throw new BadRequestException("Could not save answer");
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> deserializeAnswer(String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    // ----------------------------------------------------------------------
    // STUDENT: START EXAM
    // ----------------------------------------------------------------------
    public ExamRunInfoResponseDTO startExam(String examCode) {

        String studentCode = SecurityUtils.getCurrentUserCode();

        Exam exam = examRepository.findByExamCode(examCode)
                .orElseThrow(() -> new BadRequestException("Exam not found"));

        Student student = studentRepository.findByUserCode(studentCode)
                .orElseThrow(() -> new BadRequestException("Student not found"));

        ExamAttempt attempt = examAttemptRepository.findByExamAndStudent(exam, student)
                .map(this::handleExistingAttempt)
                .orElseGet(() -> createNewAttempt(exam, student));

        return buildExamRunInfoDTO(exam, attempt);
    }

    private ExamAttempt handleExistingAttempt(ExamAttempt attempt) {

        if (attempt.getStatus() == AttemptStatus.FINISHED)
            throw new BadRequestException("This exam has already been submitted");

        if (LocalDateTime.now().isAfter(attempt.getEndTime())) {
            // Time ran out while the student was away (e.g. disconnected). Auto-finish
            // and grade using whatever answers were saved, instead of silently
            // discarding them and leaving the attempt stuck with no score.
            finalizeExpiredAttempt(attempt);
            throw new BadRequestException("Exam time has expired");
        }

        return attempt;
    }

    private void finalizeExpiredAttempt(ExamAttempt attempt) {
        Map<Object, Object> answers = redisTemplate.opsForHash().entries(redisKey(attempt.getExamAttemptCode()));
        double score = gradeExam(attempt, answers);
        attempt.setScore(score);
        attempt.setStatus(AttemptStatus.FINISHED);
        examAttemptRepository.save(attempt);
        redisTemplate.delete(redisKey(attempt.getExamAttemptCode()));
    }

    private ExamAttempt createNewAttempt(Exam exam, Student student) {

        LocalDateTime now = LocalDateTime.now();

        ExamAttempt attempt = new ExamAttempt();
        attempt.setExam(exam);
        attempt.setStudent(student);
        attempt.setStatus(AttemptStatus.IN_PROGRESS);
        attempt.setStartTime(now);
        attempt.setEndTime(now.plusMinutes(exam.getDurationMinutes()));

        examAttemptRepository.save(attempt);
        return attempt;
    }

    // ----------------------------------------------------------------------
    // STUDENT: SAVE TEMP ANSWER
    // ----------------------------------------------------------------------
    public void saveTempAnswer(String attemptCode, String questionCode, Integer mcq, String desc) {

        ExamAttempt attempt = findAttempt(attemptCode);
        validateStudentOwnership(attempt);

        if (attempt.getStatus() == AttemptStatus.FINISHED)
            throw new BadRequestException("Cannot modify a finished attempt");

        if (LocalDateTime.now().isAfter(attempt.getEndTime()))
            throw new BadRequestException("Exam time has expired");

        String key = redisKey(attemptCode);
        redisTemplate.opsForHash().put(key, questionCode, serializeAnswer(mcq, desc));
        // Keep temp answers around a bit longer than the exam duration in case of
        // a late resume, but don't let them live in Redis forever.
        redisTemplate.expire(key, Duration.between(LocalDateTime.now(), attempt.getEndTime()).plusHours(1));
    }

    private ExamAttempt findAttempt(String attemptCode) {
        return examAttemptRepository.findByExamAttemptCode(attemptCode)
                .orElseThrow(() -> new BadRequestException("Attempt not found"));
    }

    private void validateStudentOwnership(ExamAttempt attempt) {
        String currentUser = SecurityUtils.getCurrentUserCode();
        if (!attempt.getStudent().getUserCode().equals(currentUser))
            throw new AccessDeniedException("This attempt does not belong to the current student");
    }

    // ----------------------------------------------------------------------
    // STUDENT: SUBMIT EXAM
    // ----------------------------------------------------------------------
    public double submitExam(String attemptCode) {

        ExamAttempt attempt = findAttempt(attemptCode);
        validateStudentOwnership(attempt);

        if (attempt.getStatus() == AttemptStatus.FINISHED)
            throw new BadRequestException("Already submitted");

        Map<Object, Object> answers = redisTemplate.opsForHash().entries(redisKey(attemptCode));

        double score = gradeExam(attempt, answers);

        attempt.setScore(score);
        attempt.setStatus(AttemptStatus.FINISHED);
        examAttemptRepository.save(attempt);

        redisTemplate.delete(redisKey(attemptCode));

        return score;
    }

    private double gradeExam(ExamAttempt attempt, Map<Object, Object> answers) {

        double total = 0;

        for (ExamQuestion eq : attempt.getExam().getExamQuestions()) {

            Question q = (Question) Hibernate.unproxy(eq.getQuestion());
            Object raw = answers.get(q.getQuestionCode());

            StudentAnswer sa = new StudentAnswer();
            sa.setExamAttempt(attempt);
            sa.setQuestion(q);

            if (q instanceof MultipleChoiceQuestion mcqQ) {

                // فقط سوالات چهارگزینه‌ای به صورت خودکار نمره می‌گیرند
                sa.setAutoScore(0.0);

                if (raw != null) {
                    Map<String, Object> map = deserializeAnswer((String) raw);

                    Integer mcq = map.get("mcq") == null
                            ? null
                            : ((Number) map.get("mcq")).intValue();

                    sa.setMcqAnswer(mcq);

                    Integer correct = mcqQ.getCorrectAnswerIndex();

                    if (correct != null && mcq != null && correct.equals(mcq)) {
                        sa.setAutoScore(eq.getScore());
                        total += eq.getScore();
                    }
                }

            } else {

                // سوال تشریحی تا زمانی که استاد تصحیح نکرده، نمره ندارد
                sa.setAutoScore(null);

                if (raw != null) {
                    Map<String, Object> map = deserializeAnswer((String) raw);

                    String desc = (String) map.get("desc");
                    sa.setDescriptiveAnswer(desc);
                }
            }

            studentAnswerRepository.save(sa);
        }

        return total;
    }

    // ----------------------------------------------------------------------
    // BACKGROUND JOB: auto-finish attempts whose time ran out while the
    // student never came back to the app (so results show up for the
    // professor without waiting on the student to reopen the exam page).
    // ----------------------------------------------------------------------
    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void autoFinishExpiredAttempts() {
        List<ExamAttempt> expired =
                examAttemptRepository.findByStatusAndEndTimeBefore(AttemptStatus.IN_PROGRESS, LocalDateTime.now());
        for (ExamAttempt attempt : expired) {
            finalizeExpiredAttempt(attempt);
        }
    }

    // ----------------------------------------------------------------------
    // PROFESSOR: SECURITY VALIDATOR
    // ----------------------------------------------------------------------
    private void validateProfessorOwnsExam(Exam exam) {

        String currentUserCode = SecurityUtils.getCurrentUserCode();

        Professor professor = professorRepository.findByUserCode(currentUserCode)
                .orElseThrow(() -> new BadRequestException("Professor not found"));

        // بررسی اینکه استاد این درس رو دارد
        Course course = exam.getCourse();

        if (course == null
                || course.getProfessor() == null
                || !Objects.equals(course.getProfessor().getId(), professor.getId())) {
            throw new AccessDeniedException("You are not the professor of this course");
        }
    }

    // ----------------------------------------------------------------------
    // PROFESSOR: VIEW STUDENT ATTEMPTS
    // ----------------------------------------------------------------------
    @PreAuthorize("hasRole('PROFESSOR')")
    public List<ExamAttemptSummaryDTO> getExamParticipants(String examCode) {

        Exam exam = examRepository.findByExamCode(examCode)
                .orElseThrow(() -> new BadRequestException("Exam not found"));

        validateProfessorOwnsExam(exam);

        return examAttemptRepository.findByExam(exam).stream()
                .map(a -> new ExamAttemptSummaryDTO(
                        a.getExamAttemptCode(),
                        a.getStudent().getUserCode(),
                        a.getStudent().getFirstName(),
                        a.getStudent().getLastName(),
                        a.getStatus().name(),
                        a.getScore(),
                        a.getStartTime(),
                        a.getEndTime()
                ))
                .toList();
    }

    // ----------------------------------------------------------------------
    // PROFESSOR: VIEW STUDENT ANSWERS
    // ----------------------------------------------------------------------
    @PreAuthorize("hasRole('PROFESSOR')")
    public List<StudentAnswerResponseDTO> getStudentAnswers(String attemptCode) {

        ExamAttempt attempt = findAttempt(attemptCode);

        validateProfessorOwnsExam(attempt.getExam());

        return attempt.getStudentAnswers().stream()
                .map(ans -> {

                    Question q =
                            (Question) Hibernate.unproxy(ans.getQuestion());

                    Double maxScore = q.getExamQuestions().stream()
                            .filter(eq -> eq.getExam().getId().equals(attempt.getExam().getId()))
                            .map(ExamQuestion::getScore)
                            .findFirst()
                            .orElse(null);

                    if (q instanceof MultipleChoiceQuestion mcq) {

                        return new StudentAnswerResponseDTO(
                                ans.getStudentAnswerCode(),
                                q.getQuestionCode(),
                                q.getTitle(),
                                q.getText(),
                                "MULTIPLE_CHOICE",
                                mcq.getOptions(),
                                mcq.getCorrectAnswerIndex(),
                                ans.getMcqAnswer(),
                                null,
                                ans.getAutoScore(),
                                ans.getManualScore(),
                                maxScore
                        );
                    }

                    return new StudentAnswerResponseDTO(
                            ans.getStudentAnswerCode(),
                            q.getQuestionCode(),
                            q.getTitle(),
                            q.getText(),
                            "DESCRIPTIVE",
                            null,
                            null,
                            null,
                            ans.getDescriptiveAnswer(),
                            null,
                            ans.getManualScore(),
                            maxScore
                    );
                })
                .toList();
    }

    // ----------------------------------------------------------------------
    // PROFESSOR: MANUAL GRADING
    // ----------------------------------------------------------------------
    @PreAuthorize("hasRole('PROFESSOR')")
    public void gradeDescriptiveAnswer(String answerCode, Double score) {

        StudentAnswer answer = studentAnswerRepository.findByStudentAnswerCode(answerCode)
                .orElseThrow(() -> new BadRequestException("Answer not found"));

        Exam exam = answer.getExamAttempt().getExam();
        validateProfessorOwnsExam(exam);

        if (answer.getQuestion() instanceof MultipleChoiceQuestion) {
            throw new BadRequestException("Manual grading is not allowed for MCQ questions");
        }

        ExamQuestion eq = answer.getQuestion()
                .getExamQuestions()
                .stream()
                .filter(e -> e.getExam().equals(exam))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("ExamQuestion not found"));

        if (score < 0)
            throw new BadRequestException("Score cannot be negative");

        if (score > eq.getScore())
            throw new BadRequestException("Score exceeds question max score");

        answer.setManualScore(score);
        studentAnswerRepository.save(answer);

        recalculateExamScore(answer.getExamAttempt());
    }

    // ----------------------------------------------------------------------
    // RECALCULATE FINAL SCORE = auto + manual
    // ----------------------------------------------------------------------
    private void recalculateExamScore(ExamAttempt attempt) {

        double total = 0;

        for (StudentAnswer ans : attempt.getStudentAnswers()) {

            if (ans.getAutoScore() != null)
                total += ans.getAutoScore();

            if (ans.getManualScore() != null)
                total += ans.getManualScore();
        }

        attempt.setScore(total);
        examAttemptRepository.save(attempt);
    }

    // ----------------------------------------------------------------------
    // BUILD EXAM DTO FOR STUDENT
    // ----------------------------------------------------------------------
    public ExamRunInfoResponseDTO resumeExam(String attemptCode) {
        ExamAttempt attempt = findAttempt(attemptCode);
        validateStudentOwnership(attempt);

        if (attempt.getStatus() == AttemptStatus.FINISHED)
            throw new BadRequestException("This exam has already been submitted");

        if (LocalDateTime.now().isAfter(attempt.getEndTime())) {
            finalizeExpiredAttempt(attempt);
            throw new BadRequestException("Exam time has expired");
        }

        return buildExamRunInfoDTO(attempt.getExam(), attempt);
    }

    private ExamRunInfoResponseDTO buildExamRunInfoDTO(Exam exam, ExamAttempt attempt) {

        long remaining = Math.max(0,
                Duration.between(LocalDateTime.now(), attempt.getEndTime()).getSeconds());

        List<ExamQuestionItemDTO> items = exam.getExamQuestions()
                .stream()
                .map(eq -> {

                    Question question =
                            (Question) Hibernate.unproxy(eq.getQuestion());

                    if (question instanceof MultipleChoiceQuestion mcq) {

                        return new ExamQuestionItemDTO(
                                question.getQuestionCode(),
                                question.getTitle(),
                                question.getText(),
                                "MULTIPLE_CHOICE",
                                mcq.getOptions()
                        );
                    }

                    return new ExamQuestionItemDTO(
                            question.getQuestionCode(),
                            question.getTitle(),
                            question.getText(),
                            "DESCRIPTIVE",
                            null
                    );
                })
                .toList();

        Map<String, StudentTempAnswerDTO> saved =
                loadTempAnswers(attempt.getExamAttemptCode());

        return new ExamRunInfoResponseDTO(
                attempt.getExamAttemptCode(),
                exam.getExamCode(),
                exam.getTitle(),
                exam.getDurationMinutes(),
                attempt.getStartTime(),
                attempt.getEndTime(),
                remaining,
                items,
                saved
        );
    }

    private Map<String, StudentTempAnswerDTO> loadTempAnswers(String attemptCode) {

        Map<Object, Object> raw = redisTemplate.opsForHash().entries(redisKey(attemptCode));

        return raw.entrySet().stream().collect(Collectors.toMap(
                e -> (String) e.getKey(),
                e -> {
                    Map<String, Object> map = deserializeAnswer((String) e.getValue());
                    Integer mcq = map.get("mcq") == null ? null : ((Number) map.get("mcq")).intValue();
                    return new StudentTempAnswerDTO(
                            mcq,
                            (String) map.get("desc")
                    );
                }
        ));
    }

    // ----------------------------------------------------------------------
    // STUDENT: list exams available in a course, with eligibility/status
    // ----------------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<StudentExamListItemDTO> getExamsForStudent(String courseCode) {

        String studentCode = SecurityUtils.getCurrentUserCode();

        Student student = studentRepository.findByUserCode(studentCode)
                .orElseThrow(() -> new BadRequestException("Student not found"));

        // Confirm enrollment even if the course currently has no exams yet.
        boolean enrolled = student.getCourses() != null &&
                student.getCourses().stream().anyMatch(c -> c.getCourseCode().equals(courseCode));

        if (!enrolled) {
            throw new AccessDeniedException("You are not enrolled in this course");
        }

        List<Exam> exams = examRepository.findByCourse_CourseCode(courseCode);

        return exams.stream().map(exam -> {
            ExamAttempt attempt = examAttemptRepository.findByExamAndStudent(exam, student).orElse(null);

            String status;
            Double score = null;

            if (attempt == null) {
                status = "NOT_STARTED";
            } else if (attempt.getStatus() == AttemptStatus.FINISHED) {
                status = "FINISHED";
                score = attempt.getScore();
            } else {
                status = "IN_PROGRESS";
            }

            return new StudentExamListItemDTO(
                    exam.getExamCode(),
                    exam.getTitle(),
                    exam.getDescription(),
                    exam.getDurationMinutes(),
                    status,
                    score,
                    attempt == null ? null : attempt.getExamAttemptCode()
            );
        }).toList();
    }
}


