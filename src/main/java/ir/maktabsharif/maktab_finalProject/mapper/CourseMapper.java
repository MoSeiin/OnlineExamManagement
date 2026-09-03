package ir.maktabsharif.maktab_finalProject.mapper;

import ir.maktabsharif.maktab_finalProject.domain.Course;
import ir.maktabsharif.maktab_finalProject.domain.Professor;
import ir.maktabsharif.maktab_finalProject.domain.Student;
import ir.maktabsharif.maktab_finalProject.dto.request.CourseRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.CourseResponse;
import ir.maktabsharif.maktab_finalProject.dto.response.CourseResponseDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.PersonResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring" , nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CourseMapper {

    CourseResponseDTO toCourseResponseDTO(Course course);

    Course toResponse(CourseRequestDTO dto);

    @Mapping(target = "courseCode", source = "course.courseCode")
    @Mapping(target = "professor", source = "course.professor")
    @Mapping(target = "students", source = "course.students")
    CourseResponse toDTO(Course course);

    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "courseCode" , ignore = true)
    void updateCourse(CourseRequestDTO dto, @MappingTarget Course course);

    // helper mappers
    PersonResponseDTO.ProfessorDTO toProfessorDTO(Professor professor);

    PersonResponseDTO.StudentDTO toStudentDTO(Student student);
}

