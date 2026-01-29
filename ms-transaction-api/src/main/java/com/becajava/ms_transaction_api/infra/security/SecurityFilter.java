package com.becajava.ms_transaction_api.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public SecurityFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);


        String requestURI = request.getRequestURI();
        if (isPublicRoute(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (token != null) {
            try {
                var login = tokenService.validateToken(token);

                if (!login.isEmpty()) {

                    String role = tokenService.getRoleFromToken(token);

                    var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));

                    var authentication = new UsernamePasswordAuthenticationToken(login, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    System.out.println("Usuário: " + login + " | Role: " + role);
                }
            } catch (Exception e) {
                System.out.println("Erro na validação: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicRoute(String uri) {
        return uri.contains("/swagger-ui") || uri.contains("/v3/api-docs") ||
                uri.contains("/webjars") || uri.contains("/status") ||
                uri.contains("/favicon.ico") || uri.contains("/error");
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}