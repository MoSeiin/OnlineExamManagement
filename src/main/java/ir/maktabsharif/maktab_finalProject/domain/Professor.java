package ir.maktabsharif.maktab_finalProject.domain;

import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.Question;
import jakarta.persistence.*;


import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "professor")
public class Professor extends Person{

    @OneToMany(mappedBy = "professor")
    private List<Course> courses;

    @OneToMany(mappedBy = "creator")
    private List<Exam> createdExams = new ArrayList<>();

    @OneToMany(mappedBy = "professor")
    private List<Question> createdQuestions = new ArrayList<>();



}
