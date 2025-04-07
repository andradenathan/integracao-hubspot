package com.github.andradenathan.hubspot.webhook.services;

import com.github.andradenathan.base.exceptions.InvalidSignatureException;
import com.github.andradenathan.hubspot.webhook.controllers.WebhookController;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class WebhookSignatureVerifier {
    @Value("${hubspot.client-secret}")
    private String clientSecret;
    private static final long MAX_ALLOWED_MILLIS = 5 * 60 * 1000;

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    public WebhookSignatureVerifier() {}

    public void validate(HttpServletRequest request, String body, String timestamp, String receivedSignature)
            throws InvalidKeyException, NoSuchAlgorithmException {

        long currentMillis = System.currentTimeMillis();
        long requestMillis = Long.parseLong(timestamp);

        if ((currentMillis - requestMillis) > MAX_ALLOWED_MILLIS) {
            logger.error("Timestamp is too old: currentMillis {}, requestMillis {}", currentMillis, requestMillis);
            throw new InvalidSignatureException("Timestamp is too old");
        }

        String method = request.getMethod();
        String url = request.getRequestURI();
        String hostname = request.getServerName();

        String uri = String.format("https://%s%s", hostname, url);

        String rawString = String.format("%s%s%s%s", method, uri, body, timestamp);

        String expectedSignature = calculateSignature(rawString, clientSecret);

        if (!MessageDigest.isEqual(expectedSignature.getBytes(), receivedSignature.getBytes())) {
            logger.error("Invalid signature: expected {}, received {}", expectedSignature, receivedSignature);
            throw new InvalidSignatureException("Invalid HubSpot webhook signature");
        }
    }

    private String calculateSignature(String data, String secret)
            throws NoSuchAlgorithmException, InvalidKeyException {

        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSha256.init(secretKey);
        byte[] hash = hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}
