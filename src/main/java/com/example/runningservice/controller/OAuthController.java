package com.example.runningservice.controller;

import com.example.runningservice.dto.JwtResponse;
import com.example.runningservice.dto.googleToken.GoogleAccountProfileResponseDto;
import com.example.runningservice.service.OAuth2Service;
import com.example.runningservice.service.Oauth2ProcessService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuth2Service oAuth2Service;
    private final Oauth2ProcessService oauth2ProcessService;

    @GetMapping("login/oauth")
    public ResponseEntity<JwtResponse> googleAccountProfileRes(@RequestParam String code,
        HttpServletResponse response) {

        GoogleAccountProfileResponseDto googleAccountProfileResponseDto = oAuth2Service.getGoogleAccountProfile(
            code);
        JwtResponse jwtResponse = oauth2ProcessService.processOauth2Info(
            googleAccountProfileResponseDto);

        setResponseHeader(response, jwtResponse);

        return ResponseEntity.ok(jwtResponse);
    }

    private void setResponseHeader(HttpServletResponse response, JwtResponse jwtResponse) {
        response.setHeader("Set-Cookie",
            createRefreshTokenCookie(jwtResponse.getRefreshJwt()).toString());
    }


    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
            .maxAge(7 * 24 * 60 * 60) // 7알
            .path("/")
            .secure(true)
            .sameSite("None")
            .httpOnly(true)
            .build();
    }

}
