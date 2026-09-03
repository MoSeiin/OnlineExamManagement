package ir.maktabsharif.maktab_finalProject.service;

import ir.maktabsharif.maktab_finalProject.dto.request.ExamCreateRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.request.UpdateExamRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.ExamFullResponseDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.ExamResponseDTO;

import java.util.List;

public interface ExamService {

    ExamResponseDTO createExam(ExamCreateRequestDTO dto);

    ExamResponseDTO updateExam( String examCode, UpdateExamRequestDTO dto);

    void deleteExam( String examCode);

    ExamResponseDTO getExam(String examCode);

    List<ExamResponseDTO> getCourseExams(String courseCode);

    List<ExamFullResponseDTO> getProfessorExams();
}
