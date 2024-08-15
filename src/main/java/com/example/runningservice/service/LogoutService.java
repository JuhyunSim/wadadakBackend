package com.example.runningservice.service;

import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.security.CustomUserDetails;
import com.example.runningservice.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

    private final BlackList blackList;
    private final JwtUtil jwtUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        String refreshToken = authHeader.substring("Bearer ".length());
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (!jwtUtil.validateToken(userDetails.getUsername(), refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        if (blackList.isListed(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        blackList.add(refreshToken);
        SecurityContextHolder.clearContext();
    }
}