package com.jmarqb.ms.auth.app.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmarqb.ms.auth.app.services.impl.JpaUserDetailsService;
import com.jmarqb.ms.auth.app.services.impl.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.jmarqb.ms.auth.app.security.config.TokenJwtConfig.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final JpaUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, JpaUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HEADER_AUTHORIZATION);

        if (header != null && header.startsWith(PREFIX_TOKEN)) {

            String token = header.replace(PREFIX_TOKEN, "");

            try {
                String username = jwtService.extractUsername(token);


                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtService.isTokenValid(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());

                        authenticationToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            } catch (JwtException e) {
                Map<String, String> body = new HashMap<>();
                body.put("error", e.getMessage());
                body.put("message", "Invalid token!");

                response.getWriter().write(new ObjectMapper().writeValueAsString(body));
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(CONTENT_TYPE);
            }

        }
        filterChain.doFilter(request, response);
    }
}
