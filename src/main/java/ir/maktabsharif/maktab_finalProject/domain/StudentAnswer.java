package ir.maktabsharif.maktab_finalProject.domain;

import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.Question;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "Student_answers")
public class StudentAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "StudentAnswer-seq")
    @SequenceGenerator(name = "StudentAnswer-seq", allocationSize = 10, sequenceName = "StudentAnswer-seq")
    private Long id;

    private String studentAnswerCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_attempt_id")
    private ExamAttempt examAttempt;

    public String getStudentAnswerCode() {
        return studentAnswerCode;
    }

    public void setStudentAnswerCode(String studentAnswerCode) {
        this.studentAnswerCode = studentAnswerCode;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;


    private Integer mcqAnswer;
    private String descriptiveAnswer;

    private Double manualScore;
    private Double autoScore;


    @PrePersist
    public void prePersist() {
        if (studentAnswerCode == null)
            this.studentAnswerCode = "SA-" + UUID.randomUUID();
    }

    public Long getId() {
        return id;
    }

    public Double getManualScore() {
        return manualScore;
    }

    public Double getAutoScore() {
        return autoScore;
    }

    public void setAutoScore(Double autoScore) {
        this.autoScore = autoScore;
    }

    public void setManualScore(Double manualScore) {
        this.manualScore = manualScore;
    }

    public ExamAttempt getExamAttempt() {
        return examAttempt;
    }

    public void setExamAttempt(ExamAttempt examAttempt) {
        this.examAttempt = examAttempt;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Integer getMcqAnswer() {
        return mcqAnswer;
    }

    public void setMcqAnswer(Integer mcqAnswer) {
        this.mcqAnswer = mcqAnswer;
    }

    public String getDescriptiveAnswer() {
        return descriptiveAnswer;
    }

    public void setDescriptiveAnswer(String descriptiveAnswer) {
        this.descriptiveAnswer = descriptiveAnswer;
    }
}
