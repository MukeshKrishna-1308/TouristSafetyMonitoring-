package edu.campus.lostfound.service;

import edu.campus.lostfound.model.User;
import edu.campus.lostfound.model.UserRole;
import edu.campus.lostfound.repository.UserRepository;
import edu.campus.lostfound.security.JwtService;
import edu.campus.lostfound.web.dto.AuthResponse;
import edu.campus.lostfound.web.dto.LoginRequest;
import edu.campus.lostfound.web.dto.RegisterRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final String adminRegistrationKey;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService,
            @Value("${app.auth.admin-registration-key:}") String adminRegistrationKey) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.adminRegistrationKey = adminRegistrationKey;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request, String suppliedAdminRegistrationKey) {
        if (request.role() == UserRole.ADMIN && (adminRegistrationKey.isBlank()
                || suppliedAdminRegistrationKey == null
                || !MessageDigest.isEqual(adminRegistrationKey.getBytes(StandardCharsets.UTF_8),
                        suppliedAdminRegistrationKey.getBytes(StandardCharsets.UTF_8)))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "A valid X-Admin-Registration-Key is required to register an administrator");
        }
        if (users.existsByEmailIgnoreCase(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
        }
        User user = users.save(new User(request.email().trim().toLowerCase(), passwordEncoder.encode(request.password()),
                request.name().trim(), request.role()));
        return tokenResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().trim().toLowerCase(), request.password()));
        User user = users.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        return tokenResponse(user);
    }

    private AuthResponse tokenResponse(User user) {
        return new AuthResponse(jwtService.createToken(user), "Bearer", user.getId(), user.getName(),
                user.getEmail(), user.getRole());
    }
}