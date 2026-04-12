package com.devteria.identity_service.service;

import com.devteria.identity_service.configuration.SecurityConfig;
import com.devteria.identity_service.dto.request.*;
import com.devteria.identity_service.entity.InvalidatedToken;
import com.devteria.identity_service.entity.User;
import com.devteria.identity_service.enums.SystemRole;
import com.devteria.identity_service.enums.UserStatus;
import com.devteria.identity_service.exception.ErrorCode;
import com.devteria.identity_service.exception.WebException;
import com.devteria.identity_service.repository.InvalidatedTokenRepository;
import com.devteria.identity_service.repository.OutBoundIdentityClient;
import com.devteria.identity_service.repository.OutboundUserClient;
import com.devteria.identity_service.repository.UserRepository;
import com.devteria.identity_service.dto.response.AuthenticationResponse;
import com.devteria.identity_service.dto.response.IntrospectResponse;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    private static final Log log = LogFactory.getLog(AuthenticationService.class);
    UserRepository userRepository;
    OutBoundIdentityClient outBoundIdentityClient;
    OutboundUserClient outboundUserClient;
    InvalidatedTokenRepository invalidatedTokenRepository;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    @NonFinal
    protected static final String SIGNER_KEY =
            "p7cHINXNIOg7JEYDrVOYKzMREMuZtAtuZzWsz00TyCX+CikSXSjoLImFBx6ZrsJ6";

    @NonFinal
    @Value("${jwt.valid.duration}")
    protected Long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable.duration}")
    protected Long REFRESHABLE_DURATION;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    protected String CLIENT_ID;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    protected String REDIRECT_URL;

    @NonFinal
    protected final String GRANT_TYPE = "authorization_code";

    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (WebException e) {
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }

    public AuthenticationResponse outboundAuthenticate(String code) {
        var response =
                outBoundIdentityClient.exchangeToken(
                        ExchangeTokenRequest.builder()
                                .code(code)
                                .clientId(CLIENT_ID)
                                .clientSecret(CLIENT_SECRET)
                                .redirectUri(REDIRECT_URL)
                                .grantType(GRANT_TYPE)
                                .build());
        var userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());
        log.info("userInfo: " + userInfo);
        var user = userRepository
                        .findByUsername(userInfo.getName())
                        .orElseGet(
                                () ->
                                        userRepository.save(
                                                User.builder()
                                                        .username(userInfo.getName())
                                                        .email(userInfo.getEmail())
                                                        .role(SystemRole.STAFF)
                                                        .status(UserStatus.ACTIVE)
                                                        .createdAt(Instant.now())
                                                        .build()));
        var token = generateToken(user);
        log.info("TOKEN RESPONSE {}" + response);
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new WebException(ErrorCode.WRONG_CREDENTIALS));
        if(user.getStatus() != UserStatus.ACTIVE) throw new WebException(ErrorCode.USER_INACTIVE);
//        boolean isPasswordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
//        if (!isPasswordMatch) throw new WebException(ErrorCode.WRONG_CREDENTIALS);
        var token = generateToken(user);
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        SignedJWT signToken = null;
        try {
            signToken = verifyToken(request.getToken(), true);
            String JWTid = signToken.getJWTClaimsSet().getJWTID();
            Date expirationTime = signToken.getJWTClaimsSet().getExpirationTime();
            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(JWTid).expiration(expirationTime).build();
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (WebException e) {
            log.info("Token is already invalidated");
        }
    }

    public AuthenticationResponse refreshToken(RefrestTokenRequest request)
            throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken(), true);
        var jID = signToken.getJWTClaimsSet().getJWTID();
        var experationTime = signToken.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jID).expiration(experationTime).build();
        invalidatedTokenRepository.save(invalidatedToken);
        var username = signToken.getJWTClaimsSet().getSubject();
        var user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));
        var token = generateToken(user);
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    private SignedJWT verifyToken(String jwt, boolean isRefresh)
            throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(jwt);
        Date expirationTime =
                (isRefresh)
                        ? new Date(
                        signedJWT
                                .getJWTClaimsSet()
                                .getIssueTime()
                                .toInstant()
                                .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                                .toEpochMilli())
                        : signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);
        if (!(verified && expirationTime.after(new Date())))
            throw new WebException(ErrorCode.UNAUTHENTICATED);
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new WebException(ErrorCode.UNAUTHENTICATED);
        return signedJWT;
    }

    public String generateToken(User user) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet =
                new JWTClaimsSet.Builder()
                        .subject(user.getUser_id())
                        .issueTime(new Date())
                        .expirationTime(
                                new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                        .jwtID(UUID.randomUUID().toString())
                        .claim("scope", user.getRole())
                        .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Error while signing the token", e);
            throw new RuntimeException(e);
        }
    }

    public boolean introspectToken(IntrospectRequest request) {
        var token = request.getToken();
        try {
            verifyToken(token, false);
            return true;
        } catch (WebException | ParseException | JOSEException e) {
            return false;
        }
    }

    public User findOrCreateUser(String email, String name) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            User newUser =
                    User.builder()
                            .username(name)
                            .email(email)
                            .role(SystemRole.STAFF)
                            .status(UserStatus.ACTIVE)
                            .createdAt(Instant.now())
                            .build();
            userRepository.save(newUser);
            return newUser;
        }
    }
}
