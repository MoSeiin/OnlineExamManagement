package ir.maktabsharif.maktab_finalProject.service;

import ir.maktabsharif.maktab_finalProject.dto.request.AdminUserSearchDTO;
import ir.maktabsharif.maktab_finalProject.dto.request.UpdateUserRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.AuthResponseDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.CourseResponse;
import ir.maktabsharif.maktab_finalProject.dto.response.UserListResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AdminService {

    void approveUser(String userCode);

    void rejectUser(String userCode);

    AuthResponseDTO updateUser(String userCode, UpdateUserRequestDTO dto);


    Page<UserListResponseDTO> getAllUsers(Pageable pageable);

    void assignPersonToCourse(String courseCode, String userCode);

    void changeProfessorOfCourse(String courseCode, String newUserCode);

    void removePersonFromCourse(String courseCode, String userCode);

    CourseResponse getCourseDetails(String courseCode);

    Page<UserListResponseDTO> searchUser(AdminUserSearchDTO dto, Pageable pageable);

}
