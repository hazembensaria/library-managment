package com.isamm.libraryManagement;

import com.isamm.libraryManagement.dto.RegisterRequest;
import com.isamm.libraryManagement.entity.Role;
import com.isamm.libraryManagement.service.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LibraryManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryManagementApplication.class, args);
	}



//	@Bean
//	public CommandLineRunner commandLineRunner(AuthenticationService service) {
//		return args -> {
//			// Create RegisterRequest using default constructor and setters
//			RegisterRequest admin = new RegisterRequest();
//			admin.setFirstname("Admin");
//			admin.setLastname("Admin");
//			admin.setEmail("admin@mail.com");
//			admin.setPassword("password");
//			admin.setRole(Role.ADMIN);
//
//			// Register the admin and print the JWT token
//			System.out.println("Admin token: " + service.register(admin));
//		};
//	}


}
