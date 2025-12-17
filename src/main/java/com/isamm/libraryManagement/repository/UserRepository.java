package com.isamm.libraryManagement.repository;


import java.util.Optional;

import com.isamm.libraryManagement.entity.Role;
import com.isamm.libraryManagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Page<User> findAll(Pageable pageable);

    Page<User> findByRole(Role role, Pageable pageable);

}