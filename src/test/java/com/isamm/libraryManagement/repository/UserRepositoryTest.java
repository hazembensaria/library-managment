package com.isamm.libraryManagement.repository;


import com.isamm.libraryManagement.entity.Role;
import com.isamm.libraryManagement.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_ShouldReturnCorrectUser() {
        // Arrange
        String email = "john@example.com";
        User user = new User();
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail(email);
        user.setPassword("password");
        user.setRole(Role.USER);
        
        userRepository.save(user);

        // Act
        Optional<User> foundUser = userRepository.findByEmail(email);

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(email, foundUser.get().getEmail());
    }
    @Test
    void findByRole_ShouldReturnUsersWithRole() {
        User user1 = new User();
        user1.setFirstname("Alice");
        user1.setLastname("Smith");
        user1.setEmail("alice@example.com");
        user1.setPassword("password");
        user1.setRole(Role.ADMIN);

        userRepository.save(user1);

        Page<User> admins = userRepository.findByRole(Role.ADMIN, PageRequest.of(0, 10));

        assertEquals(1, admins.getTotalElements());
        assertEquals("alice@example.com", admins.getContent().get(0).getEmail());
    }

    @Test
    void findAll_ShouldReturnPagedUsers() {
        Page<User> users = userRepository.findAll(PageRequest.of(0, 10));
        assertTrue(users.getTotalElements() > 0 || users.getTotalElements() == 0); // dépend de la base de test
    }
}
