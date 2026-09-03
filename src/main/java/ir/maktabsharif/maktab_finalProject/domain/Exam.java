package ir.maktabsharif.maktab_finalProject.domain;

import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.ExamQuestion;
import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.Question;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "exams")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exam-seq")
    @SequenceGenerator(name = "exam-seq", allocationSize = 10, sequenceName = "exam-seq")
    private Long id;
    @Column(nullable = false, unique = true , updatable = false)
    private String examCode;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Integer durationMinutes;


    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;


    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private Professor creator;


    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamQuestion> examQuestions = new ArrayList<>();

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamAttempt> examAttempts = new ArrayList<>();



    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        if (this.examCode == null) {
            this.examCode = "E-" +  UUID.randomUUID();
        }
    }





    public Exam() {
    }

    public void setExamCode(String examCode) {
        this.examCode = examCode;
    }

    public List<ExamQuestion> getExamQuestions() {
        return examQuestions;
    }

    public void setExamQuestions(List<ExamQuestion> examQuestions) {
        this.examQuestions = examQuestions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getExamCode() {
        return examCode;
    }


    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Professor getCreator() {
        return creator;
    }

    public void setCreator(Professor creator) {
        this.creator = creator;
    }
}
