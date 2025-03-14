package ru.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.jwt.dto.AuthResponse;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JdbcTemplate jdbcTemplate;
    private final JwtUtil jwtUtil;
    private final TokenStorage tokenStorage;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String AUTH_QUERY = """
        SELECT u.id, u.password, r.authority
        FROM users u
        JOIN user_role r ON u.rid = r.id
        WHERE u.login = ?
    """;

    public AuthResponse authorize(String login, String password) {
        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(AUTH_QUERY, login);
            String storedPasswordHash = (String) result.get("password");

            if (passwordEncoder.matches(password, storedPasswordHash)) {
                UUID userId = (UUID) result.get("id");
                String authority = (String) result.get("authority");

                String token = jwtUtil.generateToken(userId, authority);
                tokenStorage.storeToken(userId, token);

                // Возвращаем полное DTO с id, ролью и токеном
                return new AuthResponse(userId, authority, token);
            }
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Invalid login credentials");
        }

        return null;
    }


    public void logout(UUID userId) {
        tokenStorage.revokeToken(userId);
    }
}
