package com.example.Manage.Client.service;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Manage.Client.dto.request.AuthenticationRequest;
import com.example.Manage.Client.dto.request.IntrospectRequest;
import com.example.Manage.Client.dto.response.AuthenticationResponse;
import com.example.Manage.Client.dto.response.IntrospectResponse;
import com.example.Manage.Client.entity.User;
import com.example.Manage.Client.repository.UserRepository;
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
import lombok.var;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${jwt.signerKey}")
    String SECRET_KEY;

    public String generateToken(User user) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                    // authentication.getName() = "username_cua_user"
                    .subject(user.getUsername())
                    .issuer("Hiếu võ")
                    .issueTime(new Date()) // Thời gian phát hành token
                    .expirationTime(new Date(Instant.now().toEpochMilli() + 3600000 * 24)) // Token hết hạn sau 24 giờ
                    .claim("userId", user.getId())
                    // Spring Security tự động parse thành:
                    // - SimpleGrantedAuthority("SCOPE_USER")
                    // - SimpleGrantedAuthority("SCOPE_ADMIN")
                    .claim("scope", buildScopeRole(user)) // mặt đinh nó ghi scope
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
        StringBuilder scopeRole = new StringBuilder(" ");
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            user.getRoles().forEach(role -> scopeRole.append(role).append(" "));
        }
        return scopeRole.toString();
    }

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

    public IntrospectResponse introspect(IntrospectRequest request) throws Exception {
        var token = request.getToken();
        JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);
        return IntrospectResponse.builder()
                .valid(verified && expiryTime.after(new Date()))
                .build();

    }
}
