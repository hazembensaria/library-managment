package com.isamm.libraryManagement.service;

import com.isamm.libraryManagement.config.JwtService;
import com.isamm.libraryManagement.dto.AuthenticationRequest;
import com.isamm.libraryManagement.dto.AuthenticationResponse;
import com.isamm.libraryManagement.dto.RegisterRequest;
import com.isamm.libraryManagement.entity.Role;
import com.isamm.libraryManagement.entity.User;
import com.isamm.libraryManagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    
    @Test
    void register_Successful() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setFirstname("John");
        request.setLastname("Doe");
        request.setEmail("john@example.com");
        request.setPassword("password");
        request.setRole(Role.USER);

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenReturn(new User()); 
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        // Act
        AuthenticationResponse response = authenticationService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(repository).save(any(User.class));
    }

    @Test
    void authenticate_Successful() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("john@example.com", "password");

        User user = new User();
        user.setEmail(request.getEmail());

        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        // Act
        AuthenticationResponse response = authenticationService.authenticate(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
    @Test
    void register_DefaultRole_User() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstname("Jane");
        request.setLastname("Doe");
        request.setEmail("jane@example.com");
        request.setPassword("password");
        request.setRole(null);

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenReturn(new User());
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        AuthenticationResponse response = authenticationService.register(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(repository).save(any(User.class));
    }

    @Test
    void authenticate_Failure_ThrowsException() {
        AuthenticationRequest request = new AuthenticationRequest("john@example.com", "wrongPassword");

        doThrow(new RuntimeException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.authenticate(request);
        });

        assertEquals("Bad credentials", exception.getMessage());
    }
    @Test
    void authenticate_UserNotFound_ThrowsException() {
        AuthenticationRequest request = new AuthenticationRequest("unknown@example.com", "password");

        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            authenticationService.authenticate(request);
        });

        assertNotNull(exception);
    }

}
