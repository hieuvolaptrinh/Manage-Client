package com.example.Manage.Client.controller;

import com.example.Manage.Client.dto.request.PermissionRequest;
import com.example.Manage.Client.dto.response.PermissionResponse;
import com.example.Manage.Client.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class PermissionController {

    PermissionService permissionService;

    // ✅ Create
    @PostMapping
    public ResponseEntity<PermissionResponse> create(@RequestBody PermissionRequest request) {
        PermissionResponse response = permissionService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ✅ Read - Get All
    @GetMapping
    public ResponseEntity<List<PermissionResponse>> getAll() {
        List<PermissionResponse> permissions = permissionService.getAll();
        return ResponseEntity.ok(permissions);
    }

    // ✅ Read - Get By ID
    @GetMapping("/{permission}")
    public ResponseEntity<PermissionResponse> getById(@PathVariable String permission) {
        PermissionResponse response = permissionService.getByName(permission);
        return ResponseEntity.ok(response);
    }

    // ✅ Update
    @PutMapping("/{permission}")
    public ResponseEntity<PermissionResponse> update(@PathVariable String permission,
            @RequestBody PermissionRequest request) {
        PermissionResponse response = permissionService.update(permission, request);
        return ResponseEntity.ok(response);
    }

    // ✅ Delete
    @DeleteMapping("/{permission}")
    public ResponseEntity<Void> delete(@PathVariable String permission) {
        permissionService.deleteById(permission);
        return ResponseEntity.noContent().build();
    }
}
