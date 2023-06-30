package com.jpcchaves.adotar.service.impl;

import com.jpcchaves.adotar.domain.entities.Role;
import com.jpcchaves.adotar.domain.entities.User;
import com.jpcchaves.adotar.exception.BadRequestException;
import com.jpcchaves.adotar.exception.ResourceNotFoundException;
import com.jpcchaves.adotar.payload.dto.ApiMessageResponseDto;
import com.jpcchaves.adotar.payload.dto.auth.*;
import com.jpcchaves.adotar.payload.dto.role.RoleDto;
import com.jpcchaves.adotar.payload.dto.user.UserDto;
import com.jpcchaves.adotar.repository.RoleRepository;
import com.jpcchaves.adotar.repository.UserRepository;
import com.jpcchaves.adotar.security.JwtTokenProvider;
import com.jpcchaves.adotar.service.usecases.AuthService;
import com.jpcchaves.adotar.utils.mapper.MapperUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenServiceImpl passwordResetTokenService;
    private final PasswordEncoder passwordEncoder;
    private final MapperUtils mapperUtils;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordResetTokenServiceImpl passwordResetTokenService,
                           PasswordEncoder passwordEncoder,
                           MapperUtils mapperUtils,
                           JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordResetTokenService = passwordResetTokenService;
        this.passwordEncoder = passwordEncoder;
        this.mapperUtils = mapperUtils;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public JwtAuthResponseDto login(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            loginDto.getUsernameOrEmail(), loginDto.getPassword()
                    ));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtTokenProvider.generateToken(authentication);

            User user = userRepository.findByUsernameOrEmail(loginDto.getUsernameOrEmail(), loginDto.getUsernameOrEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com os dados informados: " + loginDto.getUsernameOrEmail()));

            UserDto userDto = copyPropertiesFromUserToUserDto(user);

            JwtAuthResponseDto jwtAuthResponseDto = new JwtAuthResponseDto();

            jwtAuthResponseDto.setAccessToken(token);
            jwtAuthResponseDto.setUser(userDto);

            return jwtAuthResponseDto;
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Usuário inexistente ou senha inválida");
        }
    }

    @Override
    public RegisterResponseDto register(RegisterRequestDto registerDto) {
// verify if user already registered
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new BadRequestException("Já existe um usuário cadastrado com o usuário informado");
        }

        // check if email already registered
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new BadRequestException("Já existe um usuário cadastrado com o email informado");
        }

        if (!Objects.equals(registerDto.getPassword(), registerDto.getConfirmPassword())) {
            throw new BadRequestException("As senhas não são iguais!");
        }

        Set<Role> roles = new HashSet<>();
        Optional<Role> userRole = roleRepository.findByName("ROLE_USER");

        User user = copyPropertiesFromRegisterDtoToUser(registerDto);

        if (userRole.isPresent()) {
            roles.add(userRole.get());
            user.setRoles(roles);
        }

        user.setAdmin(false);
        user.setActive(true);

        User newUser = userRepository.save(user);

        return mapperUtils.parseObject(newUser, RegisterResponseDto.class);
    }

    @Override
    public ApiMessageResponseDto update(UpdateUserRequestDto updateUserDto,
                                        Long id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Usuário não encontrado com o id: " + id)
                );

        if (passwordsMatches(updateUserDto.getCurrentPassword(), user.getPassword())) {

            user.setFirstName(updateUserDto.getFirstName());
            user.setLastName(updateUserDto.getLastName());
            user.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
            user.setAdmin(false);
            user.setRoles(user.getRoles());

            userRepository.save(user);

            return new ApiMessageResponseDto("Usuário atualizado com sucesso");
        } else {
            throw new BadRequestException("A senha atual não condiz com a senha cadastrada anteriormente.");
        }
    }

    @Override
    public void createPasswordResetTokenForUser(User user,
                                                String passwordToken) {
        passwordResetTokenService.createPasswordResetTokenForUser(user, passwordToken);
    }

    private UserDto copyPropertiesFromUserToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setUsername(user.getUsername());
        userDto.setRoles(mapperUtils.parseSetObjects(user.getRoles(), RoleDto.class));
        userDto.setAdmin(user.getAdmin());
        userDto.setActive(user.getActive());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setUpdatedAt(user.getUpdatedAt());
        userDto.setDeletedAt(user.getDeletedAt());
        return userDto;
    }

    private User copyPropertiesFromRegisterDtoToUser(RegisterRequestDto registerDto) {
        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setEmail(registerDto.getEmail());
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        return user;
    }

    private Boolean passwordsMatches(String currentPassword,
                                     String password) {
        return passwordEncoder.matches(currentPassword, password);
    }
}
