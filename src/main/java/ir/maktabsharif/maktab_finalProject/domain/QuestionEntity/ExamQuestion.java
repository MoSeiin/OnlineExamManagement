package ir.maktabsharif.maktab_finalProject.domain.QuestionEntity;

import ir.maktabsharif.maktab_finalProject.domain.Exam;
import jakarta.persistence.*;


@Entity
@Table(name = "exam_questions",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"exam_id", "question_id"})}
)
public class ExamQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exam_question_seq")
    @SequenceGenerator(name = "exam_question_seq", sequenceName = "exam_question_seq", allocationSize = 10)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false)
    private Double score;

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
