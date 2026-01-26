package com.becajava.ms_transaction_api.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public SecurityFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("--- DEBUG: Iniciando filtro de segurança ---");
        var token = this.recoverToken(request);

        if(token != null){
            System.out.println("--- DEBUG: Token encontrado: " + token.substring(0, 10) + "...");
            try {
                var login = tokenService.validateToken(token);
                System.out.println("--- DEBUG: Token validado com sucesso! Usuário: " + login);

                if(!login.isEmpty()){
                    var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                    var authentication = new UsernamePasswordAuthenticationToken(login, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("--- DEBUG: Autenticação setada no contexto. Acesso liberado.");
                }
            } catch (Exception e) {
                System.out.println("--- DEBUG: ERRO ao validar token: " + e.getMessage());
                // Isso aqui vai mostrar se o erro é senha (Signature) ou validade (Expired)
                e.printStackTrace();
            }
        } else {
            System.out.println("--- DEBUG: Nenhum token encontrado no Header.");
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}