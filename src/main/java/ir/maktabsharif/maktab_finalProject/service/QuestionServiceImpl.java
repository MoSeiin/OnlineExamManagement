package ir.maktabsharif.maktab_finalProject.service;

import ir.maktabsharif.maktab_finalProject.config.SecurityUtils;
import ir.maktabsharif.maktab_finalProject.domain.Course;
import ir.maktabsharif.maktab_finalProject.domain.Exam;
import ir.maktabsharif.maktab_finalProject.domain.Professor;
import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.*;
import ir.maktabsharif.maktab_finalProject.dto.request.AddQuestionToExamRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.request.QuestionCreateRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.request.QuestionUpdateRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.QuestionResponseDTO;
import ir.maktabsharif.maktab_finalProject.mapper.QuestionMapper;
import ir.maktabsharif.maktab_finalProject.repository.*;
import ir.maktabsharif.maktab_finalProject.util.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;

import java.util.UUID;

@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;
    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final QuestionMapper questionMapper;
    private final ProfessorRepository professorRepository;


    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository, CourseRepository courseRepository,
                               ExamRepository examRepository, ExamQuestionRepository examQuestionRepository,
                               QuestionMapper questionMapper,  ProfessorRepository professorRepository) {
        this.questionRepository = questionRepository;
        this.courseRepository = courseRepository;
        this.examRepository = examRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.questionMapper = questionMapper;
        this.professorRepository = professorRepository;
    }

    @Override
    public QuestionResponseDTO createQuestion(QuestionCreateRequestDTO request) {
        Course course = courseRepository.findByCourseCode(((request.courseCode()))).orElseThrow();

        String professorCode = SecurityUtils.getCurrentUserCode();

        Professor professor = professorRepository
                .findByUserCode(professorCode)
                .orElseThrow(() -> new RuntimeException("Professor not found"));

        if (course.getProfessor() == null || !course.getProfessor().getId().equals(professor.getId()))
            throw new IllegalStateException("You do not teach this course");

        Question question;

        if (request.type() == QuestionType.MULTIPLE_CHOICE) {

            MultipleChoiceQuestion mcq = new MultipleChoiceQuestion();


            if (request.options() == null || request.options().size() < 2)
                throw new RuntimeException("MCQ must have at least 2 options");

            if (request.correctAnswerIndex() == null)
                throw new RuntimeException("Correct answer index is required");

            if (request.correctAnswerIndex() >= request.options().size())
                throw new RuntimeException("Correct index invalid");

            mcq.setOptions(request.options());
            mcq.setCorrectAnswerIndex(request.correctAnswerIndex());
            mcq.setProfessor(professor);

            question = mcq;

        } else {

            question = new DescriptiveQuestion();
        }


        question.setTitle(request.title());
        question.setText(request.text());
        question.setCourse(course);
        question.setProfessor(professor);
        questionRepository.save(question);

        return questionMapper.toDto(question);
    }



    @Override
    public void addQuestionToExam(String examCode, AddQuestionToExamRequestDTO request) {
        Exam exam = examRepository.findByExamCode(examCode)
                .orElseThrow();

        String professorCode = SecurityUtils.getCurrentUserCode();
        if (exam.getCreator() == null || !exam.getCreator().getUserCode().equals(professorCode))
            throw new IllegalStateException("You are not the creator of this exam");

        Question question = questionRepository.findByQuestionCode(request.questionCode())
                .orElseThrow();

        if (question.getCourse() == null || exam.getCourse() == null
                || !question.getCourse().getId().equals(exam.getCourse().getId()))
            throw new IllegalStateException("This question does not belong to the exam's course");

        if (examQuestionRepository.existsByExam_ExamCodeAndQuestion_QuestionCode(examCode, request.questionCode()))
            throw new IllegalStateException("This question is already part of this exam");

        if (request.score() == null || request.score() <= 0)
            throw new IllegalArgumentException("Score must be a positive number");

        ExamQuestion eq = new ExamQuestion();
        eq.setExam(exam);
        eq.setQuestion(question);
        eq.setScore(request.score());

        examQuestionRepository.save(eq);
    }

    @Override
    public QuestionResponseDTO updateQuestion(String questionCode, QuestionUpdateRequestDTO request) {

        Question question = questionRepository
                .findByQuestionCode(questionCode)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        String professorCode = SecurityUtils.getCurrentUserCode();
        if (question.getProfessor() == null || !question.getProfessor().getUserCode().equals(professorCode))
            throw new IllegalStateException("You are not the owner of this question");

        if (request.title() != null)
            question.setTitle(request.title());

        if (request.text() != null)
            question.setText(request.text());

        if (request.courseCode() != null) {

            Course course = courseRepository
                    .findByCourseCode(request.courseCode())
                    .orElseThrow(() -> new EntityNotFoundException("Course not found"));

            question.setCourse(course);
        }

        if (question instanceof MultipleChoiceQuestion mcq) {

            if (request.options() != null) {

                if (request.options().size() < 2)
                    throw new IllegalArgumentException("MCQ must have at least 2 options");

                mcq.setOptions(request.options());
            }

            if (request.correctAnswerIndex() != null)
                mcq.setCorrectAnswerIndex(request.correctAnswerIndex());
        }

        Question saved = questionRepository.save(question);

        return questionMapper.toDto(saved);
    }


    @Override
    public Double calculateScore(String examCode) {
        return examQuestionRepository.calculateTotalScoreByExamCode(examCode);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<ir.maktabsharif.maktab_finalProject.dto.response.ExamQuestionResponseDTO> getExamQuestions(String examCode) {
        Exam exam = examRepository.findByExamCode(examCode)
                .orElseThrow(() -> new EntityNotFoundException("Exam not found"));

        return examQuestionRepository.findByExam(exam).stream()
                .map(eq -> {

                    Question question = (Question) Hibernate.unproxy(eq.getQuestion());

                    String type = question instanceof MultipleChoiceQuestion
                            ? "MULTIPLE_CHOICE"
                            : "DESCRIPTIVE";

                    return new ir.maktabsharif.maktab_finalProject.dto.response.ExamQuestionResponseDTO(
                            question.getQuestionCode(),
                            question.getTitle(),
                            question.getText(),
                            type,
                            eq.getScore()
                    );
                })
                .toList();
    }

    @Override
    public void removeQuestionFromExam(String examCode, String questionCode) {
        Exam exam = examRepository.findByExamCode(examCode)
                .orElseThrow(() -> new EntityNotFoundException("Exam not found"));

        String professorCode = SecurityUtils.getCurrentUserCode();
        if (exam.getCreator() == null || !exam.getCreator().getUserCode().equals(professorCode))
            throw new IllegalStateException("You are not the creator of this exam");

        ExamQuestion eq = examQuestionRepository.findByExamAndQuestion_QuestionCode(exam, questionCode)
                .orElseThrow(() -> new EntityNotFoundException("This question is not part of the exam"));

        examQuestionRepository.delete(eq);
    }

    @Override
    public void updateQuestionScoreInExam(String examCode, String questionCode, Double score) {
        if (score == null || score <= 0)
            throw new IllegalArgumentException("Score must be a positive number");

        Exam exam = examRepository.findByExamCode(examCode)
                .orElseThrow(() -> new EntityNotFoundException("Exam not found"));

        String professorCode = SecurityUtils.getCurrentUserCode();
        if (exam.getCreator() == null || !exam.getCreator().getUserCode().equals(professorCode))
            throw new IllegalStateException("You are not the creator of this exam");

        ExamQuestion eq = examQuestionRepository.findByExamAndQuestion_QuestionCode(exam, questionCode)
                .orElseThrow(() -> new EntityNotFoundException("This question is not part of the exam"));

        eq.setScore(score);
        examQuestionRepository.save(eq);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDTO> getQuestionBank(String courseCode, Pageable pageable) {

        Long professorId = getLoggedPersonId();

        Page<Question> questions =
                questionRepository.findByCourse_CourseCodeAndCourse_Professor_Id(
                        (courseCode), professorId, pageable
                );

        return questions.map(questionMapper::toDto);
    }

    //helper methods:

    private  Long getLoggedPersonId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        return user.getId();
    }


}

