package com.isamm.libraryManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class UIController {

    @RequestMapping("/register")
    public String registerPage() {
        return "register"; // Thymeleaf template register.html
    }

    @RequestMapping("/login")
    public String loginPage() {
        // Redirect to static HTML in resources/static
        return "login";
    }

    @RequestMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // create a simple dashboard.html
    }



}




