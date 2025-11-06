package com.basis.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User Data Transfer Object")
public class UserDTO {
    
    @Schema(description = "User ID", example = "1")
    private Long id;
    
    @Schema(description = "User UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID uuid;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    @Schema(description = "Username", example = "johndoe", required = true)
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "User email", example = "john.doe@example.com", required = true)
    private String email;
    
    @Size(max = 100)
    @Schema(description = "First name", example = "John")
    private String firstName;
    
    @Size(max = 100)
    @Schema(description = "Last name", example = "Doe")
    private String lastName;
    
    @Schema(description = "User active status", example = "true")
    private Boolean active;
    
    @Schema(description = "Creation timestamp", example = "2024-01-01T00:00:00Z")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp", example = "2024-01-01T00:00:00Z")
    private LocalDateTime updatedAt;
}