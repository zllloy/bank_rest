package com.example.bankcards.config;

import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ErrorResponse;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HEADER_NAME);
        if (StringUtils.isEmpty(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        if (StringUtils.isBlank(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String username;
        try {
            username = jwtService.extractUserName(token);
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            log.warn("JWT просрочен: {}", ex.getMessage());
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                      "Unauthorized", "JWT токен просрочен. Пожалуйста, войдите заново.");
            return;
        } catch (io.jsonwebtoken.JwtException ex) {
            log.warn("Некорректный JWT токен: {}", ex.getMessage());
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                      "Unauthorized", "Некорректный JWT токен.");
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userRepository.findByUsername(username).orElse(null);

            if (user != null) {
                try {
                    if (jwtService.isTokenValid(token, user)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } catch (io.jsonwebtoken.JwtException ex) {
                    log.warn("Ошибка валидации токена: {}", ex.getMessage());
                    sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                              "Unauthorized", "JWT токен недействителен.");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, int status, String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(status, error, message);
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}
