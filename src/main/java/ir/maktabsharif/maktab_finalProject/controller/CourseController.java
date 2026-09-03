package ir.maktabsharif.maktab_finalProject.controller;

import ir.maktabsharif.maktab_finalProject.dto.request.CourseRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.CourseResponseDTO;
import ir.maktabsharif.maktab_finalProject.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/courses")

public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<CourseResponseDTO> addCourse(@RequestBody @Valid CourseRequestDTO dto) {
        return ResponseEntity.status(201).body(courseService.addCourse(dto));
    }

    @GetMapping
    public Page<CourseResponseDTO> getAllCourses(Pageable pageable) {
        return courseService.getAllCourse(pageable);
    }

    @PutMapping("/{courseCode}")
    public CourseResponseDTO updateCourse(@PathVariable String courseCode, @RequestBody CourseRequestDTO dto) {
        return courseService.updateCourse(courseCode, dto);
    }
    @DeleteMapping("/{courseCode}")
    public ResponseEntity<Void> delete(@PathVariable String courseCode) {
        courseService.deleteCourse(courseCode);
        return ResponseEntity.noContent().build();
    }


}



