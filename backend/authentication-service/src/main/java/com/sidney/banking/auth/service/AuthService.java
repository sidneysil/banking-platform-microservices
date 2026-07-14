package com.sidney.banking.auth.service;

import java.util.Locale;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidney.banking.auth.api.LoginRequest;
import com.sidney.banking.auth.api.LoginResponse;
import com.sidney.banking.auth.api.RegisterRequest;
import com.sidney.banking.auth.api.UserResponse;
import com.sidney.banking.auth.user.User;
import com.sidney.banking.auth.user.UserRepository;
import com.sidney.banking.auth.user.UserRole;
import com.sidney.banking.auth.user.UserStatus;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        User user = new User(
                request.fullName().trim(),
                normalizedEmail,
                passwordEncoder.encode(request.password()),
                UserRole.CUSTOMER
        );

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        User user = userRepository
                .findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() ->
                        new BadCredentialsException("E-mail ou senha inválidos")
                );

        boolean validPassword = passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        );

        if (!validPassword || user.getStatus() != UserStatus.ACTIVE) {
            throw new BadCredentialsException(
                    "E-mail ou senha inválidos"
            );
        }

        String token = tokenService.generateToken(user);

        return new LoginResponse(
                token,
                "Bearer",
                tokenService.getExpirationSeconds(),
                UserResponse.from(user)
        );
    }

    private String normalizeEmail(String email) {
        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}