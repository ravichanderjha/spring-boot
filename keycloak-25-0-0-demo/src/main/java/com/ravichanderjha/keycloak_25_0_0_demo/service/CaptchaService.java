package com.ravichanderjha.keycloak_25_0_0_demo.service;

import java.io.IOException;

public interface CaptchaService {
    String generateCaptchaToken();

    byte[] getCaptchaImage(String token) throws IOException;

    boolean validateCaptcha(String token, String answer) throws IOException;
}
