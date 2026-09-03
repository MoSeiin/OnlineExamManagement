package ir.maktabsharif.maktab_finalProject.mapper;

import ir.maktabsharif.maktab_finalProject.domain.Exam;
import ir.maktabsharif.maktab_finalProject.dto.request.ExamCreateRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.request.UpdateExamRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.ExamFullResponseDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.ExamResponseDTO;

import org.mapstruct.*;

@Mapper(componentModel = "spring" ,  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExamMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "examCode", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Exam toEntity(ExamCreateRequestDTO dto);

    @Mapping(target = "examCode", source = "exam.examCode")
    @Mapping(target = "courseCode", source = "exam.course.courseCode")
    @Mapping(target = "creatorCode", source = "exam.creator.userCode")
    ExamResponseDTO toResponseDTO(Exam exam);


    void updateExamFromDto(UpdateExamRequestDTO dto, @MappingTarget Exam exam);

    @Mapping(source = "course.courseCode", target = "courseCode")
    @Mapping(source = "course.title", target = "courseTitle")
    @Mapping(source = "creator.userCode", target = "creatorCode")
    @Mapping(expression = "java(exam.getCreator().getFirstName() + \" \" + exam.getCreator().getLastName())", target = "creatorName")
    @Mapping(expression = "java(exam.getCreatedAt().toString())", target = "createdAt")
    ExamFullResponseDTO toExamDetailsResponseDTO(Exam exam);
}

