package com.example.Manage.Client.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Manage.Client.dto.request.AuthenticationRequest;
import com.example.Manage.Client.dto.request.IntrospectRequest;
import com.example.Manage.Client.dto.request.LogoutRequest;
import com.example.Manage.Client.dto.request.RefreshTokenRequest;
import com.example.Manage.Client.dto.response.AuthenticationResponse;
import com.example.Manage.Client.dto.response.IntrospectResponse;
import com.example.Manage.Client.entity.InvalidateToken;
import com.example.Manage.Client.entity.User;
import com.example.Manage.Client.exception.AppException;
import com.example.Manage.Client.exception.ErrorCode;
import com.example.Manage.Client.repository.InvalidateTokenRepository;
import com.example.Manage.Client.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    InvalidateTokenRepository invalidateTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    String SECRET_KEY;

    @NonFinal
    @Value("${jwt.validDuration}")
    long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable.duration}")
    long REFRESH_DURATION;

    public String generateToken(User user) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                    // authentication.getName() = "username_cua_user"
                    .subject(user.getUsername())
                    .issuer("Hiếu võ")
                    .issueTime(new Date()) // Thời gian phát hành token
                    .expirationTime(new Date(
                            Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli())) // Thời
                                                                                                    // gian
                                                                                                    // hết
                                                                                                    // hạn
                                                                                                    // token
                    .claim("userId", user.getId())
                    // Spring Security tự động parse thành:
                    // - SimpleGrantedAuthority("SCOPE_USER")
                    // - SimpleGrantedAuthority("SCOPE_ADMIN")
                    .claim("scope", buildScopeRole(user)) // mặt đinh nó ghi scope
                    // .claim("scope", "USER ADMIN")
                    .jwtID(UUID.randomUUID().toString())
                    .build();

            Payload payload = new Payload(jwtClaimsSet.toJSONObject());
            JWSObject JWSObject = new JWSObject(header, payload);

            JWSObject.sign(new MACSigner(SECRET_KEY.getBytes()));
            return JWSObject.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    private String buildScopeRole(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
                    role.getPermissions().forEach(permission -> {
                        stringJoiner.add(permission.getName());
                    });
                }
            });
        }
        return stringJoiner.toString();
    }

    // logout
    public void logout(LogoutRequest logoutRequest) throws JOSEException, ParseException {

        var signToken = verifyToken(logoutRequest.getToken(), true); // để nếu còn hiệu lực thì mới lấy được JIT

        String jit = signToken.getJWTClaimsSet().getJWTID();

        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidateToken invalidateToken = InvalidateToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();
        invalidateTokenRepository.save(invalidateToken);

    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(REFRESH_DURATION,
                        ChronoUnit.SECONDS).toEpochMilli())

                : signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHORIZED);

        }

        if (invalidateTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return signedJWT;
    }

    // login
    public AuthenticationResponse authenticated(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername());

        if (user == null) {
            throw new RuntimeException("Ko thấy user");
        }

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new RuntimeException("Sai mật khẩu");
        }
        return AuthenticationResponse.builder().token(generateToken(user))
                .authenticated(true).build();
    }

    // refresh token
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws Exception {
        var signedJWT = verifyToken(request.getToken(), true);

        // var jit = signedJWT.getJWTClaimsSet().getJWTID();
        // var expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var username = signedJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Ko thấy user");
        }

        // tạo token mới
        String newToken = generateToken(user);

        return AuthenticationResponse.builder()
                .token(newToken)
                .authenticated(true)
                .build();
    }
    // kiểm tra token

    public IntrospectResponse introspect(IntrospectRequest request) throws Exception {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (Exception e) {
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();

    }
}
