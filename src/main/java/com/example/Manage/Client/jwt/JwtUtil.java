package com.example.Manage.Client.jwt;

import com.example.Manage.Client.dto.response.IntrospectResponse;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.signerKey}")
    private static String SECRET_KEY;

    // Thời gian hết hạn: 1 giờ (ms)
    private static final long EXPIRATION_TIME = 60 * 60 * 1000;

    public static String generateToken(String username) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .issuer("Hiếu võ")
                    .issueTime(new Date()) // Thời gian phát hành token
                    .expirationTime(new Date(Instant.now().toEpochMilli() + 3600000)) // Token hết hạn sau 1 giờ
                    .claim("userId", "hiếu võ nè")
                    .claim("name", "hiếu võ")
                    .build();

            Payload payload = new Payload(jwtClaimsSet.toJSONObject());
            JWSObject JWSObject = new JWSObject(header, payload);

            JWSObject.sign(new MACSigner(SECRET_KEY.getBytes()));
            return JWSObject.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            // Kiểm tra chữ ký với khóa bí mật
            boolean isVerified = signedJWT.verify(new MACVerifier(SECRET_KEY.getBytes()));
            // Kiểm tra hạn dùng của token
            boolean notExpired = signedJWT.getJWTClaimsSet()
                    .getExpirationTime()
                    .after(new Date());

            return isVerified && notExpired;

        } catch (Exception e) {
            return false; // Token không hợp lệ hoặc parse thất bại
        }
    }

    public String extractUsername(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
