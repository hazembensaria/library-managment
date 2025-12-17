package com.isamm.libraryManagement.controller;

import com.isamm.libraryManagement.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthenticationControllerTest {

    @Autowired
    private AuthenticationController authenticationController;

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    void authenticationController_shouldBeLoaded() {
        assertNotNull(authenticationController);
    }

    @Test
    void authenticationService_shouldBeLoaded() {
        assertNotNull(authenticationService);
    }
}
