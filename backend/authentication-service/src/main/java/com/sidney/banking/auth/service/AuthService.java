package com.sidney.banking.auth.service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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

    /**
     * Cadastra um novo cliente.
     *
     * A senha é transformada em hash pelo BCrypt antes de ser
     * persistida no banco de dados.
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "E-mail já cadastrado"
            );
        }

        User user = new User(
                request.fullName().trim(),
                normalizedEmail,
                passwordEncoder.encode(request.password()),
                UserRole.CUSTOMER
        );

        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }

    /**
     * Autentica o usuário e gera um JWT.
     *
     * A mensagem de erro é propositalmente genérica para não revelar
     * se o e-mail existe no banco de dados.
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        User user = userRepository
                .findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "E-mail ou senha inválidos"
                        )
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

    /**
     * Retorna o usuário representado pelo subject do JWT.
     *
     * Mesmo que o token seja válido, o usuário é consultado novamente
     * para verificar se ainda existe e continua ativo.
     */
    @Transactional(readOnly = true)
    public UserResponse getAuthenticatedUser(String subject) {
        UUID userId = parseUserId(subject);

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Usuário não encontrado"
                        )
                );

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadCredentialsException(
                    "Usuário inativo"
            );
        }

        return UserResponse.from(user);
    }

    /**
     * Lista os usuários cadastrados.
     *
     * O controller administrativo protege essa operação com:
     * @PreAuthorize("hasRole('ADMIN')")
     */
    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return userRepository
                .findAll()
                .stream()
                .sorted(
                        Comparator.comparing(
                                User::getCreatedAt
                        )
                )
                .map(UserResponse::from)
                .toList();
    }

    /**
     * Converte o subject do JWT para UUID.
     */
    private UUID parseUserId(String subject) {
        try {
            return UUID.fromString(subject);
        } catch (IllegalArgumentException exception) {
            throw new BadCredentialsException(
                    "Token inválido"
            );
        }
    }

    /**
     * Padroniza o e-mail para evitar duplicidade causada por
     * letras maiúsculas ou espaços.
     */
    private String normalizeEmail(String email) {
        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}