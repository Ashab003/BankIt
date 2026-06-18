package com.project.BankIt_backend.service;

import com.project.BankIt_backend.entity.User;
import com.project.BankIt_backend.enums.AuditAction;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final UserService userService;
    private final RedisTemplate redisTemplate;
    private final JwtService jwtService;
    private final AuditLogService auditLogService;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ){
        System.out.println("\nLOGOUT HANDLER EXECUTED\n");

        final String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return;
        }


        String jwt = authHeader.substring(7);

        Date expirationDate =
                jwtService.extractExpiration(jwt);

        long remainingMillis =
                expirationDate.getTime()
                        - System.currentTimeMillis();

        if(remainingMillis > 0){
            redisTemplate.opsForValue().set(
                    jwt,
                    "BLACKLISTED",
                    remainingMillis,
                    TimeUnit.MILLISECONDS
            );
        }

        String username =
                jwtService.extractUsername(jwt);

        User user =
                userService.getUserByUsername(username);

        auditLogService.logAction(
                user,
                AuditAction.USER_LOGOUT,
                LocalDateTime.now(),
                "User logged out successfully"
        );

    }
}

