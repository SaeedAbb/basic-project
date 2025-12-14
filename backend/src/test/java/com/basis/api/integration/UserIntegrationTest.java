package com.basis.api.integration;

import com.basis.api.dto.UserDTO;
import com.basis.api.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(com.basis.api.config.TestSecurityConfig.class)
@Transactional
class UserIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserRepository userRepository;
    
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }
    
    @Test
    void createAndRetrieveUser_FullFlow() throws Exception {
        // Given
        UserDTO newUserDTO = UserDTO.builder()
                .username("integrationuser")
                .email("integration@example.com")
                .firstName("Integration")
                .lastName("Test")
                .build();
        
        // Create user
        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("integrationuser"))
                .andExpect(jsonPath("$.uuid").exists())
                .andReturn();
        
        UserDTO createdUser = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), UserDTO.class);
        
        // Retrieve user by UUID
        mockMvc.perform(get("/api/v1/users/{uuid}", createdUser.getUuid())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("integrationuser"))
                .andExpect(jsonPath("$.email").value("integration@example.com"));
        
        // Get all users
        mockMvc.perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username").value("integrationuser"));
    }
    
    @Test
    void updateUser_FullFlow() throws Exception {
        // Given - Create initial user
        UserDTO newUserDTO = UserDTO.builder()
                .username("originaluser")
                .email("original@example.com")
                .firstName("Original")
                .lastName("User")
                .build();
        
        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isCreated())
                .andReturn();
        
        UserDTO createdUser = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), UserDTO.class);
        
        // Update user
        UserDTO updateDTO = UserDTO.builder()
                .username("updateduser")
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("User")
                .active(false)
                .build();
        
        mockMvc.perform(put("/api/v1/users/{uuid}", createdUser.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.active").value(false));
    }
    
    @Test
    void deleteUser_FullFlow() throws Exception {
        // Given - Create user
        UserDTO newUserDTO = UserDTO.builder()
                .username("tobedeleted")
                .email("delete@example.com")
                .build();
        
        MvcResult createResult = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isCreated())
                .andReturn();
        
        UserDTO createdUser = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), UserDTO.class);
        
        // Delete user
        mockMvc.perform(delete("/api/v1/users/{uuid}", createdUser.getUuid())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        
        // Verify user is deleted
        mockMvc.perform(get("/api/v1/users/{uuid}", createdUser.getUuid())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void createUser_WithDuplicateUsername_ReturnsBadRequest() throws Exception {
        // Given - Create first user
        UserDTO firstUser = UserDTO.builder()
                .username("duplicateuser")
                .email("first@example.com")
                .build();
        
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstUser)))
                .andExpect(status().isCreated());
        
        // Try to create second user with same username
        UserDTO secondUser = UserDTO.builder()
                .username("duplicateuser")
                .email("second@example.com")
                .build();
        
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(containsString("Username already exists")));
    }
}