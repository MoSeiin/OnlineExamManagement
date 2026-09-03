package ir.maktabsharif.maktab_finalProject.dto.response;

public class PersonResponseDTO {

    public record ProfessorDTO(
            String userCode,
            String firstName,
            String lastName,
            String email
    ) {}

    public record StudentDTO(
            String userCode,
            String firstName,
            String lastName,
            String email
    ) {}
}
