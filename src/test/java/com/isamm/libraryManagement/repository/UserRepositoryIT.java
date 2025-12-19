package com.isamm.libraryManagement.repository;

import com.isamm.libraryManagement.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_shouldReturnUser() {
        User u = new User();
        u.setEmail("test@mail.com");
        u.setPassword("x");
        userRepository.save(u);

        Optional<User> found = userRepository.findByEmail("test@mail.com");
        assertTrue(found.isPresent());
    }
}
