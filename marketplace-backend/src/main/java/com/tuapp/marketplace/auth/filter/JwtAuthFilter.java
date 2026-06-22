package com.tuapp.marketplace.auth.filter;

import com.tuapp.marketplace.auth.entity.User;
import com.tuapp.marketplace.auth.repository.UserRepository;
import com.tuapp.marketplace.auth.service.JwtUtil;
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
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        getTokenFromRequest(request).ifPresent(this::authenticate);
        filterChain.doFilter(request, response);
    }

    private Optional<String> getTokenFromRequest(HttpServletRequest request) {
        var header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return Optional.of(header.substring(7));
        }
        return Optional.empty();
    }

    private void authenticate(String token) {
        if (!jwtUtil.isValid(token)) return;

        var userId = jwtUtil.extractUserId(token);
        var user = userRepository.findById(UUID.fromString(userId));
        if (user.isEmpty()) return;

        var u = user.get();
        var authorities = List.of(new SimpleGrantedAuthority(u.getRole().name()));
        var auth = new UsernamePasswordAuthenticationToken(u, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
