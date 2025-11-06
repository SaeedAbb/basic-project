package com.basis.api.controller;

import com.basis.api.dto.UserDTO;
import com.basis.api.exception.GlobalExceptionHandler;
import com.basis.api.exception.ResourceNotFoundException;
import com.basis.api.exception.ValidationException;
import com.basis.api.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserService userService;
    
    private UserDTO testUserDTO;
    private UUID testUuid;
    
    @BeforeEach
    void setUp() {
        testUuid = UUID.randomUUID();
        testUserDTO = UserDTO.builder()
                .id(1L)
                .uuid(testUuid)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    void getAllUsers_ReturnsListOfUsers() throws Exception {
        // Given
        List<UserDTO> users = Arrays.asList(testUserDTO);
        when(userService.getAllUsers()).thenReturn(users);
        
        // When/Then
        mockMvc.perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
        
        verify(userService).getAllUsers();
    }
    
    @Test
    void getUserByUuid_WhenUserExists_ReturnsUser() throws Exception {
        // Given
        when(userService.getUserByUuid(testUuid)).thenReturn(testUserDTO);
        
        // When/Then
        mockMvc.perform(get("/api/v1/users/{uuid}", testUuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
        
        verify(userService).getUserByUuid(testUuid);
    }
    
    @Test
    void getUserByUuid_WhenUserDoesNotExist_Returns404() throws Exception {
        // Given
        when(userService.getUserByUuid(testUuid))
                .thenThrow(new ResourceNotFoundException("User not found"));
        
        // When/Then
        mockMvc.perform(get("/api/v1/users/{uuid}", testUuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("User not found"));
    }
    
    @Test
    void createUser_WithValidData_ReturnsCreatedUser() throws Exception {
        // Given
        UserDTO newUserDTO = UserDTO.builder()
                .username("newuser")
                .email("new@example.com")
                .firstName("New")
                .lastName("User")
                .build();
        
        UserDTO createdUserDTO = UserDTO.builder()
                .id(2L)
                .uuid(UUID.randomUUID())
                .username("newuser")
                .email("new@example.com")
                .firstName("New")
                .lastName("User")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(userService.createUser(any(UserDTO.class))).thenReturn(createdUserDTO);
        
        // When/Then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
        
        verify(userService).createUser(any(UserDTO.class));
    }
    
    @Test
    void createUser_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Given
        UserDTO invalidUserDTO = UserDTO.builder()
                .username("") // Empty username
                .email("invalid-email") // Invalid email format
                .build();
        
        // When/Then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.errors").exists());
    }
    
    @Test
    void createUser_WithDuplicateUsername_ReturnsBadRequest() throws Exception {
        // Given
        UserDTO newUserDTO = UserDTO.builder()
                .username("existinguser")
                .email("new@example.com")
                .build();
        
        when(userService.createUser(any(UserDTO.class)))
                .thenThrow(new ValidationException("Username already exists"));
        
        // When/Then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("Username already exists"));
    }
    
    @Test
    void updateUser_WithValidData_ReturnsUpdatedUser() throws Exception {
        // Given
        UserDTO updateDTO = UserDTO.builder()
                .username("updateduser")
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("User")
                .active(false)
                .build();
        
        UserDTO updatedUserDTO = UserDTO.builder()
                .id(1L)
                .uuid(testUuid)
                .username("updateduser")
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("User")
                .active(false)
                .createdAt(testUserDTO.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(userService.updateUser(eq(testUuid), any(UserDTO.class))).thenReturn(updatedUserDTO);
        
        // When/Then
        mockMvc.perform(put("/api/v1/users/{uuid}", testUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
        
        verify(userService).updateUser(eq(testUuid), any(UserDTO.class));
    }
    
    @Test
    void deleteUser_WhenUserExists_ReturnsNoContent() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(testUuid);
        
        // When/Then
        mockMvc.perform(delete("/api/v1/users/{uuid}", testUuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        
        verify(userService).deleteUser(testUuid);
    }
    
    @Test
    void deleteUser_WhenUserDoesNotExist_Returns404() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("User not found"))
                .when(userService).deleteUser(testUuid);
        
        // When/Then
        mockMvc.perform(delete("/api/v1/users/{uuid}", testUuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"));
    }
}