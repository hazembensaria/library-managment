package com.isamm.libraryManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
