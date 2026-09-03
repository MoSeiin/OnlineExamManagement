package ir.maktabsharif.maktab_finalProject.controller;


import ir.maktabsharif.maktab_finalProject.dto.request.AdminUserSearchDTO;
import ir.maktabsharif.maktab_finalProject.dto.request.UpdateUserRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.AuthResponseDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.CourseResponse;
import ir.maktabsharif.maktab_finalProject.dto.response.UserListResponseDTO;
import ir.maktabsharif.maktab_finalProject.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")

public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PutMapping("/user/{userCode}/approve")
    public ResponseEntity<Void> approveUser(@PathVariable String userCode) {
        adminService.approveUser(userCode);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/user/{userCode}/reject")
    public ResponseEntity<Void> rejectUser(@PathVariable String userCode) {
        adminService.rejectUser(userCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public Page<UserListResponseDTO> getAllUsers(Pageable pageable) {
        return adminService.getAllUsers(pageable);
    }


    @PutMapping("/user/{userCode}")
    public AuthResponseDTO updateUser(@PathVariable String userCode, @RequestBody UpdateUserRequestDTO dto) {
        return adminService.updateUser(userCode, dto);
    }


    @GetMapping("/users/search")
    public Page<UserListResponseDTO> searchUsers(AdminUserSearchDTO dto  , Pageable pageable) {
        return adminService.searchUser(dto, pageable);

    }



    @PutMapping("/course/{courseCode}/users/{userCode}")
    public ResponseEntity<Void> assignPersonToCourse(
            @PathVariable String courseCode,
            @PathVariable String userCode) {

        adminService.assignPersonToCourse(courseCode, userCode);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/course/{courseCode}/updatedProfessor/{userCode}")
    public ResponseEntity<Void> changeCourseProfessor(@PathVariable String courseCode, @PathVariable String userCode) {
        adminService.changeProfessorOfCourse(courseCode, userCode);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/course/{courseCode}/users/{userCode}")
    public ResponseEntity<Void> removeUserFromCourse(
            @PathVariable String courseCode,
            @PathVariable String userCode
    ) {
        adminService.removePersonFromCourse(courseCode, userCode);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/courses/{courseCode}")
    public CourseResponse getAllCourseInfo(@PathVariable String courseCode) {
        return adminService.getCourseDetails(courseCode);

    }


}
