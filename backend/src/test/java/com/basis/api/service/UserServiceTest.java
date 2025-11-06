package com.basis.api.service;

import com.basis.api.dto.UserDTO;
import com.basis.api.entity.User;
import com.basis.api.exception.ResourceNotFoundException;
import com.basis.api.exception.ValidationException;
import com.basis.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    private User testUser;
    private UserDTO testUserDTO;
    private UUID testUuid;
    
    @BeforeEach
    void setUp() {
        testUuid = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        
        testUser = User.builder()
                .id(1L)
                .uuid(testUuid)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .active(true)
                .build();
        testUser.setCreatedAt(now);
        testUser.setUpdatedAt(now);
        
        testUserDTO = UserDTO.builder()
                .id(1L)
                .uuid(testUuid)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
    
    @Test
    void getAllUsers_ReturnsListOfUsers() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);
        
        // When
        List<UserDTO> result = userService.getAllUsers();
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser");
        verify(userRepository).findAll();
    }
    
    @Test
    void getUserByUuid_WhenUserExists_ReturnsUser() {
        // Given
        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
        
        // When
        UserDTO result = userService.getUserByUuid(testUuid);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(userRepository).findByUuid(testUuid);
    }
    
    @Test
    void getUserByUuid_WhenUserDoesNotExist_ThrowsResourceNotFoundException() {
        // Given
        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> userService.getUserByUuid(testUuid))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with UUID");
    }
    
    @Test
    void createUser_WithValidData_CreatesUser() {
        // Given
        UserDTO newUserDTO = UserDTO.builder()
                .username("newuser")
                .email("new@example.com")
                .firstName("New")
                .lastName("User")
                .active(true)
                .build();
        
        User savedUser = User.builder()
                .id(2L)
                .uuid(UUID.randomUUID())
                .username("newuser")
                .email("new@example.com")
                .firstName("New")
                .lastName("User")
                .active(true)
                .build();
        
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When
        UserDTO result = userService.createUser(newUserDTO);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("newuser");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void createUser_WithExistingUsername_ThrowsValidationException() {
        // Given
        UserDTO newUserDTO = UserDTO.builder()
                .username("existinguser")
                .email("new@example.com")
                .build();
        
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);
        
        // When/Then
        assertThatThrownBy(() -> userService.createUser(newUserDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Username already exists");
    }
    
    @Test
    void createUser_WithExistingEmail_ThrowsValidationException() {
        // Given
        UserDTO newUserDTO = UserDTO.builder()
                .username("newuser")
                .email("existing@example.com")
                .build();
        
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);
        
        // When/Then
        assertThatThrownBy(() -> userService.createUser(newUserDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Email already exists");
    }
    
    @Test
    void updateUser_WithValidData_UpdatesUser() {
        // Given
        UserDTO updateDTO = UserDTO.builder()
                .username("updateduser")
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("User")
                .active(false)
                .build();
        
        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("updateduser")).thenReturn(false);
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        UserDTO result = userService.updateUser(testUuid, updateDTO);
        
        // Then
        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void updateUser_WhenUserDoesNotExist_ThrowsResourceNotFoundException() {
        // Given
        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> userService.updateUser(testUuid, testUserDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with UUID");
    }
    
    @Test
    void deleteUser_WhenUserExists_DeletesUser() {
        // Given
        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.of(testUser));
        
        // When
        userService.deleteUser(testUuid);
        
        // Then
        verify(userRepository).delete(testUser);
    }
    
    @Test
    void deleteUser_WhenUserDoesNotExist_ThrowsResourceNotFoundException() {
        // Given
        when(userRepository.findByUuid(testUuid)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> userService.deleteUser(testUuid))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with UUID");
    }
}