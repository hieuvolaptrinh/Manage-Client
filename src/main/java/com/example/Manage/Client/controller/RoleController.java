package com.example.Manage.Client.controller;

import com.example.Manage.Client.dto.request.RoleRequest;
import com.example.Manage.Client.dto.response.RoleResponse;
import com.example.Manage.Client.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RoleController {

    RoleService roleService;

    // ✅ Create
    @PostMapping
    public ResponseEntity<RoleResponse> create(@RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.create(request));
    }

    // ✅ Get All
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAll() {
        return ResponseEntity.ok(roleService.getAll());
    }

    // ✅ Get by name
    @GetMapping("/{name}")
    public ResponseEntity<RoleResponse> getByName(@PathVariable String name) {
        return ResponseEntity.ok(roleService.getByName(name));
    }

    // ✅ Update
    @PutMapping("/{name}")
    public ResponseEntity<RoleResponse> update(@PathVariable String name,
            @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.update(name, request));
    }

    // ✅ Delete
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> delete(@PathVariable String name) {
        roleService.delete(name);
        return ResponseEntity.noContent().build();
    }
}
