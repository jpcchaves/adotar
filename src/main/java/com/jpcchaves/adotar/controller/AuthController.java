package com.jpcchaves.adotar.controller;

import com.jpcchaves.adotar.domain.entities.PasswordResetRequest;
import com.jpcchaves.adotar.domain.entities.User;
import com.jpcchaves.adotar.payload.dto.ApiMessageResponseDto;
import com.jpcchaves.adotar.payload.dto.auth.*;
import com.jpcchaves.adotar.repository.UserRepository;
import com.jpcchaves.adotar.service.usecases.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService,
                          UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<JwtAuthResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(authService.login(loginDto));
    }

    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<RegisterResponseDto> register(@Valid @RequestBody RegisterRequestDto registerDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(registerDto));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiMessageResponseDto> update(@Valid @RequestBody UpdateUserRequestDto updateUserDto,
                                                        @PathVariable(name = "id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.update(updateUserDto, id));
    }

    @PostMapping("/password-reset-request")
    public String resetPasswordRequest(@RequestBody PasswordResetRequest passwordResetRequest,
                                       final HttpServletRequest request) {
        Optional<User> user = userRepository.findByEmail(passwordResetRequest.getEmail());

        String passwordResetUrl = "";

        if (user.isPresent()) {
            String passwordResetToken = UUID.randomUUID().toString();
            authService.createPasswordResetTokenForUser(user.get(), passwordResetToken);
            passwordResetUrl = passwordResetEmailLink(applicationUrl(request), user.get(), passwordResetToken);
        }

        return passwordResetUrl;
    }

    private String passwordResetEmailLink(String applicationUrl,
                                          User user,
                                          String passwordResetToken) {
        String url = applicationUrl + "/register/reset-password?token=" + passwordResetToken;
        eventListener.sendPasswordResetVerificationEmail(url);

        return url;
    }


    public String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
