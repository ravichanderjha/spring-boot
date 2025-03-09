package com.ravichanderjha.keycloak_25_0_0_demo.controller;

import com.ravichanderjha.keycloak_25_0_0_demo.service.CaptchaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {
    private final CaptchaService captchaService;

    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @GetMapping("/generate")
    public ResponseEntity<Map<String, String>> generateCaptcha() {
        String token = captchaService.generateCaptchaToken();
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/image/{token}")
    public ResponseEntity<byte[]> getCaptchaImage(@PathVariable String token) throws IOException {
        byte[] imageBytes = captchaService.getCaptchaImage(token);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                .body(imageBytes);
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, String>> validateCaptcha(@RequestParam String token, @RequestParam String answer) throws IOException {
        boolean isValid = captchaService.validateCaptcha(token, answer);
        return isValid ? ResponseEntity.ok(Map.of("status", "valid"))
                : ResponseEntity.badRequest().body(Map.of("status", "invalid"));
    }
}
