package ir.maktabsharif.maktab_finalProject.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "exam_attempts")
public class ExamAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exam_attempt_seq")
    @SequenceGenerator(name = "exam_attempt_seq", allocationSize = 10, sequenceName = "exam_attempt_seq")
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String examAttemptCode;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private AttemptStatus status;

    private Double score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;


    @ManyToOne(fetch = FetchType.LAZY)
    private Exam exam;

    @OneToMany(mappedBy = "examAttempt", cascade = CascadeType.ALL)
    private List<StudentAnswer> studentAnswers = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (examAttemptCode == null)
            this.examAttemptCode = "EA-" + UUID.randomUUID();
    }

    public Long getId() {
        return id;
    }

    public String getExamAttemptCode() {
        return examAttemptCode;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public AttemptStatus getStatus() {
        return status;
    }

    public void setStatus(AttemptStatus status) {
        this.status = status;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public List<StudentAnswer> getStudentAnswers() {
        return studentAnswers;
    }
}
