package com.example.Manage.Client.config;

import java.text.ParseException;
import java.util.Objects;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.example.Manage.Client.dto.request.IntrospectRequest;
import com.example.Manage.Client.service.AuthenticationService;

// customize lại để token ở black list không xài đc
@Component
public class CustomeJwtDecoder implements JwtDecoder {

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder mNimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        try { // nếu 0 còn hiệu lực
            var response = authenticationService.introspect(IntrospectRequest.builder().token(token).build());

            if (!response.isValid()) {
                throw new JwtException("Token is not valid");
            }
        } catch (Exception e) {
            throw new JwtException(e.getMessage());
        }

        if (Objects.isNull(mNimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            mNimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS512)
                    .build();
        }

        return mNimbusJwtDecoder.decode(token);
    }

}
