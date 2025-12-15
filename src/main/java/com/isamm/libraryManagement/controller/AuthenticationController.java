package com.isamm.libraryManagement.controller;


import com.isamm.libraryManagement.dto.AuthenticationRequest;
import com.isamm.libraryManagement.dto.AuthenticationResponse;
import com.isamm.libraryManagement.dto.RegisterRequest;
import com.isamm.libraryManagement.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService service;

    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }


    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse response
    ) {
        AuthenticationResponse authResponse = service.authenticate(request);
        Cookie cookie = new Cookie("jwt", authResponse.getToken());
        cookie.setHttpOnly(true);   // JS cannot read it
        cookie.setSecure(false);    // true in production with HTTPS
        cookie.setPath("/");        // cookie valid for entire app
        cookie.setMaxAge(24 * 60 * 60); // 1 day expiration

        // 3️⃣ Add cookie to response
        response.addCookie(cookie);

        // 4️⃣ Return 200 OK (body empty, token is in cookie)
        return ResponseEntity.ok().build();
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // 1️⃣ Invalidate JWT cookie
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0); // deletes the cookie

        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }



}