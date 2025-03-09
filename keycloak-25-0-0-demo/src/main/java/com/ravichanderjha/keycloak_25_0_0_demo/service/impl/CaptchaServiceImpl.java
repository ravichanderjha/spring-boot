package com.ravichanderjha.keycloak_25_0_0_demo.service.impl;


import com.github.cage.Cage;
import com.github.cage.GCage;
import com.ravichanderjha.keycloak_25_0_0_demo.service.CaptchaService;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CaptchaServiceImpl implements CaptchaService {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();


    private final Cage cage = new GCage();
    private final Map<String, String> captchaStore = new ConcurrentHashMap<>();
    private final long CAPTCHA_EXPIRATION = 300_000; // 5 minutes

    public String generateCaptchaToken() {
        String answer = generateRandomString(5); // Generate 5-character token
        String token = cage.getTokenGenerator().next(); // Generate captcha answer
        captchaStore.put(token, answer);
        scheduleCaptchaRemoval(token);
        return token;
    }
    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public byte[] getCaptchaImage(String token) throws IOException {
        String answer = captchaStore.get(token);
        if (answer == null) {
            throw new IllegalArgumentException("Invalid or expired captcha token");
        }

        BufferedImage image = cage.drawImage(answer);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    public boolean validateCaptcha(String token, String userInput) {
        String expectedAnswer = captchaStore.remove(token);
        return expectedAnswer != null && expectedAnswer.equalsIgnoreCase(userInput);
    }

    private void scheduleCaptchaRemoval(String token) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                captchaStore.remove(token);
            }
        }, CAPTCHA_EXPIRATION);
    }
}
