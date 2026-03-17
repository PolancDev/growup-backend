package com.growup.backend.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.growup.backend.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.time.OffsetDateTime;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, UserDetailsService userDetailsService,
            ObjectMapper objectMapper) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        log.info("GrowUp-Log: JwtAuthenticationFilter - Método HTTP: {} - Ruta: {} - Origin: {}", request.getMethod(),
                path, request.getHeader("Origin"));
        String token = getJwtFromRequest(request);
        log.info("GrowUp-Log: JwtAuthenticationFilter - Procesando {} - Token presente: {}", path, (token != null));

        if (token != null) {
            System.out.println("GrowUp-Log: JwtAuthenticationFilter - Token presente: " + token);
            String validationError = jwtProvider.getValidationError(token);

            if (validationError == null) {
                String email = jwtProvider.getEmailFromToken(token);
                log.info("GrowUp-Log: JwtAuthenticationFilter - Token válido para email: {}", email);

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("GrowUp-Log: JwtAuthenticationFilter - Autenticación establecida para: {}", email);
                }
            } else {
                log.warn("GrowUp-Log: JwtAuthenticationFilter - Error de validación: {}", validationError);
                sendErrorResponse(response, validationError);
                return; // Detenemos la cadena de filtros si el token existe pero no es válido
            }
        }
        System.out.println("GrowUp-Log: JwtAuthenticationFilter - Token presente: " + token);

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(message)
                .timestamp(OffsetDateTime.now());

        String json = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        System.out.println("GrowUp-Log: JwtAuthenticationFilter - Token presente en getJwtFromRequest: "
                + request);
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        boolean shouldNot = path.startsWith("/api/v1/auth/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/") ||
                path.startsWith("/docs/");

        if (shouldNot) {
            log.info("GrowUp-Log: JwtAuthenticationFilter - Bypassing authentication for: {}", path);
        }
        return shouldNot;
    }
}
