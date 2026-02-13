package com.prem.jats.jats_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.prem.jats.jats_backend.entity.User;
import com.prem.jats.jats_backend.repository.UserRepository;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain)
        throws ServletException, IOException {

    // ✅ Skip JWT filter for auth endpoints
    String path = request.getServletPath();
    if (path.startsWith("/api/auth")) {
        filterChain.doFilter(request, response);
        return;
    }

    // 1️⃣ Read Authorization header
    String authHeader = request.getHeader("Authorization");

    String token = null;
    String email = null;

    // 2️⃣ Check Bearer token
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        token = authHeader.substring(7);
        email = jwtUtil.extractEmail(token);
    }

    // 3️⃣ Validate and authenticate
    if (email != null &&
        SecurityContextHolder.getContext().getAuthentication() == null &&
        jwtUtil.isTokenValid(token)) {

        User user = userRepository.findByEmail(email);

        if (user != null) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            user, null, null);

            authToken.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request));

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authToken);
        }
    }

    // 4️⃣ Continue filter chain
    filterChain.doFilter(request, response);
}
}