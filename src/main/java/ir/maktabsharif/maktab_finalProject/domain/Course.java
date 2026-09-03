package ir.maktabsharif.maktab_finalProject.domain;

import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.Question;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String courseCode;
    @Column(nullable = false, unique = true)
    private String title;

    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    private Professor professor;

    @ManyToMany
    @JoinTable(
            name = "course_student",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> students = new ArrayList<>();


    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Exam> exams = new ArrayList<>();

    @OneToMany(mappedBy = "course" ,  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();


    @PrePersist
    public void prePersist() {
        if (this.courseCode == null)
            this.courseCode ="C-" + UUID.randomUUID();
    }


    public Course(String courseCode, String title, LocalDate startDate, LocalDate endDate, Professor professor, List<Student> students) {
        this.courseCode = courseCode;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.professor = professor;
        this.students = students;
    }

    public Course(Long id, String courseCode, String title, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.courseCode = courseCode;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Course() {
    }

    public Long getId() {
        return id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    //helper methods
    public void removeProfessor() {
        this.professor = null;
    }


}

