package com.example.cmderma.auth;

import jakarta.validation.constraints.NotBlank;

public record AccessCodeRequest(@NotBlank String code) {
}

