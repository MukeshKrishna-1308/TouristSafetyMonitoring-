package edu.campus.lostfound.web.dto;

import edu.campus.lostfound.model.UserRole;

public record AuthResponse(String token, String tokenType, Long userId, String name, String email, UserRole role) {
}