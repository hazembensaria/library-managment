package com.isamm.libraryManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import com.isamm.libraryManagement.service.DashboardService;

@Controller
public class UIController {

	  private final DashboardService dashboardService ;
	  
	  public UIController(DashboardService dashboardService) {
	        this.dashboardService = dashboardService;
	    }
	  
	  
    @RequestMapping("/register")
    public String registerPage() {
        return "register"; // Thymeleaf template register.html
    }

    @RequestMapping("/login")
    public String loginPage() {
        // Redirect to static HTML in resources/static
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("kpis", dashboardService.getDashboardKpis());
        return "dashboard";
    }

    
    

    @GetMapping({"/", "/home"})
    public String home(Model model) {
      model.addAttribute("kpis", dashboardService.getDashboardKpis());
      return "home";
    }


}




