package com.devteria.identity_service.configuration;

import com.devteria.identity_service.dto.request.IntrospectRequest;
import com.devteria.identity_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import java.text.ParseException;
import java.util.Objects;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomJwtDecoder implements JwtDecoder {
  private final AuthenticationService authenticationService;
  private NimbusJwtDecoder jwtDecoder;
  @Value("${jwt.signer-key}")
  private String SIGNER_KEY;
  @Override
  public Jwt decode(String token) throws JwtException {
    try {
      var response =
          authenticationService.introspect(IntrospectRequest.builder().token(token).build());
      if (!response.isValid()) throw new JwtException("Token invalid");
    } catch (JOSEException | ParseException e) {
      throw new JwtException(e.getMessage());
    }
    if (Objects.isNull(jwtDecoder)) {
      SecretKeySpec key = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");
      jwtDecoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS512).build();
    }
    return jwtDecoder.decode(token);
  }
}
