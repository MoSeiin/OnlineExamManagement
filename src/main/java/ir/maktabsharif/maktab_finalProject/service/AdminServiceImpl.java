package ir.maktabsharif.maktab_finalProject.service;

import ir.maktabsharif.maktab_finalProject.domain.*;
import ir.maktabsharif.maktab_finalProject.dto.request.AdminUserSearchDTO;
import ir.maktabsharif.maktab_finalProject.dto.request.UpdateUserRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.AuthResponseDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.CourseResponse;
import ir.maktabsharif.maktab_finalProject.dto.response.UserListResponseDTO;
import ir.maktabsharif.maktab_finalProject.exception.BadRequestException;
import ir.maktabsharif.maktab_finalProject.exception.ResourceNotFoundException;
import ir.maktabsharif.maktab_finalProject.exception.UserNotFoundException;
import ir.maktabsharif.maktab_finalProject.mapper.AdminMapper;
import ir.maktabsharif.maktab_finalProject.mapper.CourseMapper;
import ir.maktabsharif.maktab_finalProject.repository.CourseRepository;
import ir.maktabsharif.maktab_finalProject.repository.PersonRepository;

import ir.maktabsharif.maktab_finalProject.specification.PersonSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class AdminServiceImpl implements AdminService {


    private final PersonRepository personRepository;
    private final AdminMapper adminMapper;
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Autowired
    public AdminServiceImpl(PersonRepository personRepository
            , AdminMapper adminMapper
            , CourseRepository courseRepository, CourseMapper courseMapper) {

        this.personRepository = personRepository;
        this.adminMapper = adminMapper;
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    @Override
    public void approveUser(String userCode) {
        Person person = findPersonByUserCode(userCode);
        if (person.getStatus() == Status.APPROVED) throw new BadRequestException("User is already approved");
        person.setStatus(Status.APPROVED);


    }

    @Override
    public void rejectUser(String userCode) {
        Person person = findPersonByUserCode(userCode);
        if (person.getStatus() == Status.REJECTED) throw new BadRequestException("User is already rejected");
        person.setStatus(Status.REJECTED);


    }

    @Override
    public AuthResponseDTO updateUser(String userCode, UpdateUserRequestDTO dto) {
        Person person = findPersonByUserCode(userCode);
        if (person.getRole() == Role.ADMIN && dto.role() != Role.ADMIN)
            throw new BadRequestException("Cannot change role of admin");
        adminMapper.updatePerson(dto, person);

        return adminMapper.toAuthResponseDTO(person);
    }


    @Override
    public Page<UserListResponseDTO> getAllUsers(Pageable pageable) {
        Page<Person> person = personRepository.findAll(pageable);
        return person.map(adminMapper::toUserListResponseDTO);
    }


    @Override
    public Page<UserListResponseDTO> searchUser(AdminUserSearchDTO dto, Pageable pageable) {

        Specification<Person> spec = PersonSpecification.search(dto);
        Page<Person> person = personRepository.findAll(spec, pageable);
        return person.map(adminMapper::toUserListResponseDTO);
    }


    @Override
    public void assignPersonToCourse(String courseCode, String userCode) {
        Course course = findCourseByCode(courseCode);
        Person person = findPersonByUserCode(userCode);
        if (person.getStatus() != Status.APPROVED) throw new UserNotFoundException("User must be approved");

        switch (person.getRole()) {
            case PROFESSOR -> {
                if (course.getProfessor() != null)
                    throw new BadRequestException("User is already approved");
                if (!(person instanceof Professor professor))
                    throw new BadRequestException("User entity is not Professor");

                course.setProfessor(professor);
            }
            case STUDENT -> {
                if (!(person instanceof Student student))
                    throw new BadRequestException("User entity is not Student");

                if (course.getStudents().contains(student))
                    throw new BadRequestException("Student already enrolled in course");

                course.getStudents().add(student);
            }
            default -> throw new BadRequestException("Unsupported role for assignment: " + person.getRole());

        }
        courseRepository.save(course);


    }


    @Override
    public void changeProfessorOfCourse(String courseCode, String newUserCode) {

        Course course = findCourseByCode(courseCode);

        if (course.getProfessor() == null) {
            throw new IllegalStateException("Course has no professor yet");
        }

        Person person = findPersonByUserCode(newUserCode);
        if (person.getRole() != Role.PROFESSOR || person.getStatus() != Status.APPROVED)
            throw new IllegalStateException("User is not an approved professor");

        course.setProfessor((Professor) person);
        courseRepository.save(course);


    }


    @Override
    public void removePersonFromCourse(String courseCode, String userCode) {
        Course course = findCourseByCode(courseCode);
        Person person = findPersonByUserCode(userCode);

        switch (person.getRole()) {
            case PROFESSOR -> {
                if (course.getProfessor() == null ||
                        !course.getProfessor().getId().equals(person.getId())) {
                    throw new IllegalStateException("This professor is not assigned to this course.");
                }
                course.removeProfessor();


            }
            case STUDENT -> {
                boolean remove = course.getStudents().remove(person);
                if (!remove) throw new IllegalStateException("This student is not assigned to this course.");
            }
            default -> {
                throw new IllegalStateException("This user cannot be removed from a course.");

            }

        }

        courseRepository.save(course);

    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseDetails(String courseCode) {
        Course course = findCourseByCode(courseCode);
        return courseMapper.toDTO(course);
    }

    //helper methods =

    private Course findCourseByCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new ResourceNotFoundException("Course","courseCode", courseCode));
    }

    private Person findPersonByUserCode(String userCode) {
        return personRepository.findByUserCode(userCode)
                .orElseThrow(() -> new ResourceNotFoundException("User","userCode", userCode));
    }



}
