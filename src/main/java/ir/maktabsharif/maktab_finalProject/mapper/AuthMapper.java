package ir.maktabsharif.maktab_finalProject.mapper;

import ir.maktabsharif.maktab_finalProject.domain.Person;
import ir.maktabsharif.maktab_finalProject.domain.Professor;
import ir.maktabsharif.maktab_finalProject.domain.Student;
import ir.maktabsharif.maktab_finalProject.dto.request.RegisterRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.AuthResponseDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "userCode" , ignore = true)
    @Mapping(target = "status" , ignore = true)
    @Mapping(target = "createdAt" , ignore = true)
    Person toPerson(RegisterRequestDTO dto);

    AuthResponseDTO toAuthResponseDTO(Person person);

    @ObjectFactory
    default Person createPerson(RegisterRequestDTO dto) {
        return switch (dto.role()) {
            case STUDENT -> new Student();
            case PROFESSOR -> new Professor();
            default -> throw new IllegalArgumentException("Invalid role");
        };
}
}