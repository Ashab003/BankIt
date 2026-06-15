package com.project.BankIt_backend.config;

import com.project.BankIt_backend.repository.TokenRepository;
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

//intercepts the request before reaching oru controllers
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

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

        //it looks for Authorization: Bearer <token>
        if(authHeader == null || !authHeader.startsWith("Bearer ")){

            //if the header is missing then doFilterInternal will be like not my problem and
            // then pass it to springs default filter which will catch it and reject it with a 403 Forbidden
            filterChain.doFilter(request,response);
            return;
        }

        JwtToken = authHeader.substring(7); // bearer <token> //takes the string after "bearer", basically extracts the token

        username = jwtService.extractUsername(JwtToken);
        //extracts username from the token

        //DEBUG PRINT
        System.out.println("TOKEN USERNAME = " + username);

        //double-checking that username and current request have not been authenticated somehow
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            //checking if this token is valid
            // checking if it belongs this to this user, and it has not expired
            // AND also checking if the token is valid in the database or not
            if (jwtService.isTokenValid(JwtToken, userDetails) && jwtService.isTokenValidInDB(JwtToken) ) {

                //if everything is valid create this and shove it in SecurityContextHolder
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
