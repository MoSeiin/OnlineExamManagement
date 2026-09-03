package ir.maktabsharif.maktab_finalProject.domain.QuestionEntity;

import ir.maktabsharif.maktab_finalProject.domain.Course;
import ir.maktabsharif.maktab_finalProject.domain.Professor;
import ir.maktabsharif.maktab_finalProject.domain.StudentAnswer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)

@DiscriminatorColumn(name = "question_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "questions")
public abstract class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Question-seq")
    @SequenceGenerator(name = "Question-seq", allocationSize = 10, sequenceName = "Question-seq")
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank @Column(columnDefinition = "TEXT")
    private String text;

    @Column(unique = true, nullable = false, updatable = false)
    private String questionCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    private Professor professor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamQuestion> examQuestions = new ArrayList<>();

    @OneToMany(mappedBy = "question")
    private List<StudentAnswer> studentAnswers = new ArrayList<>();

    @PrePersist
    private void generateCode() {
        if (questionCode == null) {
            questionCode = "Q-" + UUID.randomUUID();

        }
    }

    public List<ExamQuestion> getExamQuestions() {
        return examQuestions;
    }

    public void setExamQuestions(List<ExamQuestion> examQuestions) {
        this.examQuestions = examQuestions;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getQuestionCode() {
        return questionCode;
    }

    public void setQuestionCode(String questionCode) {
        this.questionCode = questionCode;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<StudentAnswer> getStudentAnswers() {
        return studentAnswers;
    }
}
