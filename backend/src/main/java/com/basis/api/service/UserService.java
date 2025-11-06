package com.basis.api.service;

import com.basis.api.dto.UserDTO;
import com.basis.api.entity.User;
import com.basis.api.exception.ResourceNotFoundException;
import com.basis.api.exception.ValidationException;
import com.basis.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    
    public List<UserDTO> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public UserDTO getUserByUuid(UUID uuid) {
        log.debug("Fetching user by UUID: {}", uuid);
        return userRepository.findByUuid(uuid)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with UUID: " + uuid));
    }
    
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        log.debug("Creating new user: {}", userDTO.getUsername());
        
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new ValidationException("Username already exists: " + userDTO.getUsername());
        }
        
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ValidationException("Email already exists: " + userDTO.getEmail());
        }
        
        User user = convertToEntity(userDTO);
        user = userRepository.save(user);
        log.info("Created new user with ID: {}", user.getId());
        
        return convertToDTO(user);
    }
    
    @Transactional
    public UserDTO updateUser(UUID uuid, UserDTO userDTO) {
        log.debug("Updating user: {}", uuid);
        
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with UUID: " + uuid));
        
        // Check if username is being changed and already exists
        if (!user.getUsername().equals(userDTO.getUsername()) && 
            userRepository.existsByUsername(userDTO.getUsername())) {
            throw new ValidationException("Username already exists: " + userDTO.getUsername());
        }
        
        // Check if email is being changed and already exists
        if (!user.getEmail().equals(userDTO.getEmail()) && 
            userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ValidationException("Email already exists: " + userDTO.getEmail());
        }
        
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setActive(userDTO.getActive());
        
        user = userRepository.save(user);
        log.info("Updated user with ID: {}", user.getId());
        
        return convertToDTO(user);
    }
    
    @Transactional
    public void deleteUser(UUID uuid) {
        log.debug("Deleting user: {}", uuid);
        
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with UUID: " + uuid));
        
        userRepository.delete(user);
        log.info("Deleted user with ID: {}", user.getId());
    }
    
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .uuid(user.getUuid())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    
    private User convertToEntity(UserDTO userDTO) {
        return User.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .active(userDTO.getActive() != null ? userDTO.getActive() : true)
                .build();
    }
}