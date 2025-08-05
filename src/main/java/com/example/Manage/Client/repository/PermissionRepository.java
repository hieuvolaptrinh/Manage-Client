package com.example.Manage.Client.repository;

import com.example.Manage.Client.entity.Permission;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {

    Optional<Permission> findById(String id);

}
