package com.example.Manage.Client.config;

import java.text.ParseException;
import java.util.Objects;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.example.Manage.Client.repository.InvalidateTokenRepository;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;

// customize lại để token ở black list không xài đc
@Component
@RequiredArgsConstructor
public class CustomeJwtDecoder implements JwtDecoder {

    @Value("${jwt.signerKey}")
    private String signerKey;

    private final InvalidateTokenRepository invalidateTokenRepository;

    private NimbusJwtDecoder mNimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            // Parse token để lấy JTI (JWT ID)
            SignedJWT signedJWT = SignedJWT.parse(token);
            String jti = signedJWT.getJWTClaimsSet().getJWTID();

            // Kiểm tra token có trong blacklist không
            if (jti != null && invalidateTokenRepository.existsById(jti)) {
                throw new JwtException("Token is invalidated");
            }
        } catch (ParseException e) {
            throw new JwtException("Cannot parse token", e);
        }

        // nếu còn hiệu lực thì decode token như bình thường
        if (Objects.isNull(mNimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            mNimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return mNimbusJwtDecoder.decode(token);
    }

}
