package com.jpcchaves.adotar.service.usecases;

import com.jpcchaves.adotar.domain.entities.User;
import com.jpcchaves.adotar.payload.dto.ApiMessageResponseDto;
import com.jpcchaves.adotar.payload.dto.auth.*;

public interface AuthService {
    JwtAuthResponseDto login(LoginDto loginDto);

    RegisterResponseDto register(RegisterRequestDto registerDto);

    ApiMessageResponseDto update(UpdateUserRequestDto updateUserDto,
                                 Long id);

    void createPasswordResetTokenForUser(User user,
                                         String passwordToken);
}
