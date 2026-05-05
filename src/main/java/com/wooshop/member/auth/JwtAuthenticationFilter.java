package com.wooshop.member.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Authorization 헤더 값 추출
        String bearerToken = request.getHeader("Authorization");

        // 2. 토큰 없거나 형식이 다르면 다음 필터로 바로 넘김
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. "Bearer " 제거 후 순수 토큰 추출
        String token = bearerToken.substring(7);

        // 4. 토큰 유효하면 SecurityContext에 인증 정보 저장
        if (jwtProvider.validateToken(token)) {
            String email = jwtProvider.getEmail(token);
            String role = jwtProvider.getRole(token);

            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(email, null, authorities)
            );
        }

        // 5. 다음 필터로 전달
        filterChain.doFilter(request, response);
    }
}
