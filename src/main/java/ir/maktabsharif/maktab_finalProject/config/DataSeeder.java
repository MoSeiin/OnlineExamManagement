package ir.maktabsharif.maktab_finalProject.config;

import ir.maktabsharif.maktab_finalProject.domain.*;
import ir.maktabsharif.maktab_finalProject.repository.AdminRepository;
import ir.maktabsharif.maktab_finalProject.repository.CourseRepository;
import ir.maktabsharif.maktab_finalProject.repository.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Component
public class DataSeeder implements CommandLineRunner {
    private final PasswordEncoder passwordEncoder;

    private final PersonRepository personRepository;
    private final AdminRepository adminRepository;
    private final CourseRepository courseRepository;
    @Autowired
    private DataSeeder(PasswordEncoder passwordEncoder, PersonRepository personRepository, AdminRepository adminRepository, CourseRepository courseRepository) {
        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
        this.adminRepository = adminRepository;
        this.courseRepository = courseRepository;
    }


    @Override
    public void run(String... args) throws Exception {
        if (personRepository.count() > 0) return;

        Random random = new Random();

        // ---------------- ADMIN ----------------
        Admin admin = new Admin();
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setUserName("admin");
        admin.setEmail("admin@gmail.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        admin.setStatus(Status.APPROVED);
        adminRepository.save(admin);

        // ---------------- STUDENTS ----------------
        List<Student> students = new ArrayList<>();

        String[] firstNames = {"Ali", "Reza", "Sara", "Mina", "Amir", "Nima", "Parsa", "Mahsa", "Arman", "Sahar"};
        String[] lastNames = {"Ahmadi", "Karimi", "Hosseini", "Moradi", "Rahimi", "Akbari", "Jafari", "Hasani", "Safari", "Kazemi"};

        for (int i = 0; i < 10; i++) {

            Student student = new Student();

            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];

            student.setFirstName(firstName);
            student.setLastName(lastName);
            student.setStatus(Status.APPROVED);
            student.setUserName(firstName.toLowerCase() + "." + lastName.toLowerCase() + i);
            student.setEmail("student" + i + "@gmail.com");
            student.setPassword(passwordEncoder.encode("1234"));
            student.setAge(18 + random.nextInt(22));
            student.setRole(Role.STUDENT);

            personRepository.save(student);
            students.add(student);
        }

        // ---------------- PROFESSORS ----------------
        List<Professor> professors = new ArrayList<>();

        for (int i = 0; i < 5; i++) {

            Professor professor = new Professor();

            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];

            professor.setFirstName(firstName);
            professor.setLastName(lastName);
            professor.setStatus(Status.APPROVED);
            professor.setUserName(firstName.toLowerCase() + "." + lastName.toLowerCase() + "_prof" + i);
            professor.setEmail("professor" + i + "@mail.com");
            professor.setPassword(passwordEncoder.encode("1234"));
            professor.setAge(30 + random.nextInt(35));
            professor.setRole(Role.PROFESSOR);

            personRepository.save(professor);
            professors.add(professor);
        }

        // ---------------- COURSES ----------------
        String[] courseNames = {
                "Mathematics 1",
                "Mathematics 2",
                "Physics",
                "Chemistry",
                "Algorithms",
                "Data Structures",
                "Operating Systems",
                "Databases",
                "Software Engineering",
                "Artificial Intelligence"
        };

        List<Course> courseList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {

            Course course = new Course();

            course.setTitle(courseNames[i]);
            course.setCourseCode(course.getCourseCode());

            // random professor
            Professor professor = professors.get(random.nextInt(professors.size()));
            course.setProfessor(professor);

            courseRepository.save(course);
            courseList.add(course);
        }

        // ---------------- ADD STUDENTS TO COURSES ----------------
       /* for (Course course : courseList) {

            for (int i = 0; i < 10; i++) {

                Student student = students.get(random.nextInt(students.size()));

                course.getStudents().add(student);
            }

            courseRepository.save(course);
        }*/

        System.out.println("✅ Done: Added Admin + 20 Professors + 100 Students + 10 Courses");
    };
}







