package com.example.runningservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.security.CustomUserDetails;
import com.example.runningservice.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private BlackList blackList;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails customUserDetails;

    @InjectMocks
    private LogoutService logoutService;

    @Test
    void testLogout_NullToken() {
        //given
        when(request.getHeader("Authorization")).thenReturn(null);
        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> logoutService.logout(request, response, authentication));
        //then
        assertEquals(ErrorCode.INVALID_TOKEN, exception.getErrorCode());
    }

    @Test
    void testLogout_TokenNotStartingWithBearer() {
        // given
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        // when
        CustomException exception = assertThrows(CustomException.class,
            () -> logoutService.logout(request, response, authentication));

        // then
        assertEquals(ErrorCode.INVALID_TOKEN, exception.getErrorCode());
    }

    @Test
    void testLogout_UserEmailTokenEmailMisMatch() {
        //given
        when(request.getHeader("Authorization")).thenReturn("Bearer validRefreshToken");
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUsername()).thenReturn("username");
        when(jwtUtil.validateToken("username", "validRefreshToken")).thenCallRealMethod();
        Claims mockClaims = mock(Claims.class);
        when(mockClaims.getSubject()).thenReturn("differentEmail");
        when(jwtUtil.extractAllClaims("validRefreshToken")).thenReturn(mockClaims);

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> logoutService.logout(request, response, authentication));

        //then
        assertEquals(ErrorCode.INVALID_TOKEN, exception.getErrorCode());
    }

    @Test
    void testLogout_ExpiredToken() {
        // given
        when(request.getHeader("Authorization")).thenReturn("Bearer validRefreshToken");
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUsername()).thenReturn("username");
        when(jwtUtil.validateToken("username", "validRefreshToken")).thenCallRealMethod();
        Claims mockClaims = mock(Claims.class);
        when(mockClaims.getSubject()).thenReturn("username");
        when(jwtUtil.extractAllClaims("validRefreshToken")).thenReturn(mockClaims);
        when(jwtUtil.isTokenExpired("validRefreshToken")).thenReturn(true);

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> logoutService.logout(request, response, authentication));

        //then
        assertEquals(ErrorCode.INVALID_TOKEN, exception.getErrorCode());
    }

    @Test
    void testLogout_TokenAlreadyInBlacklist() {
        when(request.getHeader("Authorization")).thenReturn("Bearer validRefreshToken");
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUsername()).thenReturn("username");
        when(jwtUtil.validateToken("username", "validRefreshToken")).thenReturn(true);
        when(blackList.isListed("validRefreshToken")).thenReturn(true);

        //when
        CustomException exception = assertThrows(CustomException.class,
            () -> logoutService.logout(request, response, authentication));

        //then
        assertEquals(ErrorCode.INVALID_TOKEN, exception.getErrorCode());
    }

    @Test
    void testLogout_success() {
        //given
        when(request.getHeader("Authorization")).thenReturn("Bearer validRefreshToken");
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUsername()).thenReturn("username");
        when(jwtUtil.validateToken("username", "validRefreshToken")).thenReturn(true);
        when(blackList.isListed("validRefreshToken")).thenReturn(false);

        //when
        logoutService.logout(request, response, authentication);

        verify(blackList, times(1)).add("validRefreshToken");
        assert (SecurityContextHolder.getContext().getAuthentication() == null);
    }
}