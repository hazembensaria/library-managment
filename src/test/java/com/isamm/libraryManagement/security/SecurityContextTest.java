package com.isamm.libraryManagement.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityContextTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    void securityFilterChain_shouldBeLoaded() {
        assertNotNull(securityFilterChain);
    }
}
