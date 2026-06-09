package com.project.BankIt_backend.config;

import com.project.BankIt_backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    )
            throws ServletException, IOException {
        System.out.println(
                "REQUEST -> "
                        + request.getMethod()
                        + " "
                        + request.getRequestURI()
        );


        final String authHeader = request.getHeader("Authorization");
        final String JwtToken;
        final String username;


        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        JwtToken = authHeader.substring(7);

        username = jwtService.extractUsername(JwtToken);

        //DEBUG PRINT
        System.out.println("TOKEN USERNAME = " + username);
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(JwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                //DEBUG PRINT
                System.out.println("USER FOUND = " + userDetails.getUsername());
                System.out.println("AUTHORITIES = " + userDetails.getAuthorities());
                System.out.println("TOKEN VALID");
                //
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
            filterChain.doFilter(request, response);
    }
}
