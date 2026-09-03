package ir.maktabsharif.maktab_finalProject.service;

import ir.maktabsharif.maktab_finalProject.dto.request.LoginRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.request.RegisterRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.AuthResponseDTO;


public interface AuthService {

    AuthResponseDTO register(RegisterRequestDTO request);

    AuthResponseDTO login(LoginRequestDTO request);
}
