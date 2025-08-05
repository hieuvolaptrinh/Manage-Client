package com.example.Manage.Client.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Manage.Client.entity.Role;

public interface RoleRepository extends JpaRepository<Role, String> {

}
