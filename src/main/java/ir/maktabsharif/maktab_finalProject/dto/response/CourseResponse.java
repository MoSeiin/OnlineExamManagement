package ir.maktabsharif.maktab_finalProject.dto.response;

import ir.maktabsharif.maktab_finalProject.domain.Professor;
import ir.maktabsharif.maktab_finalProject.domain.Student;

import java.time.LocalDate;
import java.util.List;

public record CourseResponse(
        String courseCode ,
        String title ,
        LocalDate startDate ,
        LocalDate endDate ,
        PersonResponseDTO.ProfessorDTO professor ,
        List<PersonResponseDTO.StudentDTO> students
) {
}
