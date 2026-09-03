package ir.maktabsharif.maktab_finalProject.service;

import ir.maktabsharif.maktab_finalProject.domain.Person;
import ir.maktabsharif.maktab_finalProject.domain.Role;
import ir.maktabsharif.maktab_finalProject.domain.Status;
import ir.maktabsharif.maktab_finalProject.dto.request.LoginRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.request.RegisterRequestDTO;
import ir.maktabsharif.maktab_finalProject.dto.response.AuthResponseDTO;
import ir.maktabsharif.maktab_finalProject.exception.BadRequestException;
import ir.maktabsharif.maktab_finalProject.mapper.AuthMapper;
import ir.maktabsharif.maktab_finalProject.repository.PersonRepository;


import ir.maktabsharif.maktab_finalProject.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class AuthServiceImpl implements AuthService {


    private final PersonRepository personRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthServiceImpl(PersonRepository personRepository, AuthMapper authMapper, PasswordEncoder passwordEncoder ,  JwtUtil jwtUtil) {

        this.personRepository = personRepository;
        this.authMapper = authMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }


    @Override
    public AuthResponseDTO register(RegisterRequestDTO request) {
        Person person = authMapper.toPerson(request);
        person.setUserName(person.getUserName().trim().toLowerCase());
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        if (request.role() == Role.ADMIN) {
            throw new IllegalArgumentException("You cannot register as ADMIN");
        }
        person.setRole(request.role());
        personRepository.save(person);
        return authMapper.toAuthResponseDTO(person);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {

        Person person = personRepository.findByUserNameIgnoreCase(request.userName().trim())
                .orElseThrow(() ->
                        new BadRequestException("Invalid username or password")
                );
        if (!passwordEncoder.matches(request.password(), person.getPassword())) {
            throw new BadRequestException("Invalid username or password");
        }

        if (person.getStatus() != Status.APPROVED) {
            throw new BadRequestException("User is not approved yet");
        }
        String token = jwtUtil.generateToken(person.getUserName());

        return new AuthResponseDTO(
                token,
                person.getUserCode(),
                person.getUserName(),
                person.getRole(),
                person.getStatus()
        );
    }


}
