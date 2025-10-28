package com.example.cmderma.auth;

import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final byte[] referenceCode;

  public AuthController(@Value("${app.access-code:1234}") String accessCode) {
    this.referenceCode = normalize(accessCode);
  }

  @PostMapping("/unlock")
  public ResponseEntity<Map<String, Object>> unlock(@Valid @RequestBody AccessCodeRequest request) {
    if (!matches(referenceCode, request.code())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("message", "Código inválido."));
    }
    return ResponseEntity.ok(Map.of("success", true));
  }

  private static byte[] normalize(String input) {
    if (input == null) {
      return new byte[0];
    }
    return input.trim().getBytes(StandardCharsets.UTF_8);
  }

  private static boolean matches(byte[] reference, String candidate) {
    if (reference.length == 0) {
      return false;
    }
    byte[] candidateBytes = normalize(candidate);
    return MessageDigest.isEqual(reference, candidateBytes);
  }
}

