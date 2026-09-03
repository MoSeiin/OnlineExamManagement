/*
package ir.maktabsharif.maktab_finalProject.mapper;

import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.DescriptiveQuestion;
import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.MultipleChoiceQuestion;
import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.Question;
import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.QuestionType;
import ir.maktabsharif.maktab_finalProject.dto.response.QuestionResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    @Mapping(target = "courseCode", source = "course.courseCode")
    @Mapping(target = "type", expression = "java(getType(question))")
    @Mapping(target = "options", expression = "java(getOptions(question))")
    QuestionResponseDTO toDto(Question question);

    default QuestionType getType(Question question) {
        if (question instanceof MultipleChoiceQuestion) {
            return QuestionType.MULTIPLE_CHOICE;
        }
        return QuestionType.DESCRIPTIVE;
    }

    default java.util.List<String> getOptions(Question question) {
        if (question instanceof MultipleChoiceQuestion mcq) {
            return mcq.getOptions();
        }
        return null;
    }


}
*/

package ir.maktabsharif.maktab_finalProject.mapper;

import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.DescriptiveQuestion;
import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.MultipleChoiceQuestion;
import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.Question;
import ir.maktabsharif.maktab_finalProject.domain.QuestionEntity.QuestionType;
import ir.maktabsharif.maktab_finalProject.dto.response.QuestionResponseDTO;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    @Mapping(target = "courseCode", source = "course.courseCode")
    @Mapping(target = "type", expression = "java(getType(question))")
    @Mapping(target = "options", expression = "java(getOptions(question))")
    QuestionResponseDTO toDto(Question question);

    default QuestionType getType(Question question) {

        Question actualQuestion = (Question) Hibernate.unproxy(question);

        if (actualQuestion instanceof MultipleChoiceQuestion) {
            return QuestionType.MULTIPLE_CHOICE;
        }

        return QuestionType.DESCRIPTIVE;
    }

    default java.util.List<String> getOptions(Question question) {

        Question actualQuestion = (Question) Hibernate.unproxy(question);

        if (actualQuestion instanceof MultipleChoiceQuestion mcq) {
            return mcq.getOptions();
        }

        return null;
    }
}
