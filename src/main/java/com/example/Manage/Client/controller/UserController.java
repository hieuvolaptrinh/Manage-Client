package com.example.Manage.Client.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Manage.Client.dto.request.UserRequest;
import com.example.Manage.Client.dto.response.Notification;
import com.example.Manage.Client.dto.response.UserResponse;
import com.example.Manage.Client.entity.User;
import com.example.Manage.Client.exception.AppException;
import com.example.Manage.Client.exception.ErrorCode;

import com.example.Manage.Client.service.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    public User createUser(@RequestBody @Valid UserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/myInfo")
    public ResponseEntity<UserResponse> getMyInfo() {
        return ResponseEntity.ok(userService.getMyInfo());
    }

    // @PostAuthorize("returnObject.username == authentication.name or
    // hasAuthority('ADMIN')") // kiểm tra sau khi thực
    // returnObject là response trả về

    // @PreAuthorize("hasRole('ROLE_ADMIN')") //=> role là nó đã thêm prefix rồi,
    // còn authority thì không
    @PreAuthorize("hasAuthority('APPROVE_POST')")
    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<UserResponse>> contextHolder() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Current user: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> {
            log.info("Granted authority: {}", grantedAuthority.getAuthority());
        });
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
            @RequestBody @Valid UserRequest request) {
        UserResponse updatedUser = userService.updateUser(id, request);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        throw new AppException(ErrorCode.USER_NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ví dụ lấy để xác định user bằng token truyền về

    @GetMapping("/me")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal Jwt jwt) {
        // Jwt.getSubject() = claim "sub"
        String userId = jwt.getSubject();
        String email = jwt.getClaimAsString("userId"); // nếu bạn thêm claim "email"
        // Bạn có thể lấy roles nếu để trong claim "roles"
        List<String> roles = jwt.getClaimAsStringList("scope");

        Map<String, Object> profile = Map.of(
                "userId", userId,
                "email", email,
                "roles", roles);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getMyProfile() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("=== XEM PROFILE CÁ NHÂN ===");
        // OUTPUT: Current user: john_doe (từ "sub" claim)
        log.info("Current user: {}", authentication.getName());
        // OUTPUT: Principal type: JwtAuthenticationToken
        log.info("Principal type: {}", authentication.getPrincipal().getClass().getSimpleName());
        // OUTPUT: Credentials type: Jwt
        log.info("Credentials type: {}", authentication.getCredentials().getClass().getSimpleName());

        // ✨ LẤY userId TỪ JWT TOKEN ✨
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            var jwt = jwtAuth.getToken();

            // Lấy userId từ claim "userId"
            Object userIdObj = jwt.getClaim("userId");
            Long userId = userIdObj != null ? ((Number) userIdObj).longValue() : null;

            // Lấy issuer từ claim "iss"
            String issuer = jwt.getClaimAsString("iss");

            // Lấy expiration time
            var expiration = jwt.getExpiresAt();

            // OUTPUT: 🆔 User ID từ token: 123
            log.info("🆔 User ID từ token: {}", userId);
            // OUTPUT: 🏷️ Issuer từ token: Hiếu võ
            log.info("🏷️ Issuer từ token: {}", issuer);
            // OUTPUT: ⏰ Token expires at: 2025-08-05T16:30:00Z
            log.info("⏰ Token expires at: {}", expiration);
            // OUTPUT: 📋 Tất cả claims: {sub=john_doe, iss=Hiếu võ, iat=1641234567,
            // exp=1641238167, userId=123, scope=USER ADMIN}
            log.info("📋 Tất cả claims trong token: {}", jwt.getClaims());
        }

        return ResponseEntity.ok(Notification.builder()
                .message("Xem profile thành công" + authentication.getName())

                .build());
    }
}
