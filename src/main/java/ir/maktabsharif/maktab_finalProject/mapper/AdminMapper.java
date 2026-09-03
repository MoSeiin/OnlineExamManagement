package ir.maktabsharif.maktab_finalProject.mapper;

import ir.maktabsharif.maktab_finalProject.domain.Person;
import ir.maktabsharif.maktab_finalProject.dto.request.UpdateUserRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.AuthResponseDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.UserListResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring" , nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AdminMapper {
    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "userCode", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updatePerson(UpdateUserRequestDTO dto, @MappingTarget Person person);

    AuthResponseDTO toAuthResponseDTO(Person person);

    UserListResponseDTO toUserListResponseDTO(Person person);


}
