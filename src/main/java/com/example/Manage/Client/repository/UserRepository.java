package com.example.Manage.Client.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Manage.Client.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    User findByUsername(String username);
}
