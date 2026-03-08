package com.impacsys.core.common.utils.token;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nonnull;

/**
 * Onetime JWT Token Utility Class 
 * 
 * For unit test {@link OnetimeJwtTokenTest}
 * 
 * @author hkb@impacsys.kr
 * @since 2025.11.17
 */
public class OnetimeJwtToken {

    // ... TODO: Move this to configuration
    private static final String ONETIME_TOKEN_KEY = "OnetimeJwtTokenAccessSecretKeyForImpacSysCoreCommonUtils";
    private static final String HASH_ALGORITHM = "SHA-256";

    /**
     * Generate JWT Token
     * 
     * @param tenantId
     * @param claims
     * @param expiration
     * @return
     */
    public static String generateToken(
            @Nonnull final String tenantId,
            @Nonnull final Map<String, Object> claims, final long expiration) {

        final Date now = new Date(System.currentTimeMillis());
        try {
            return Jwts.builder()
                    .issuer(tenantId)
                    .claims(claims)
                    .issuedAt(now)
                    .expiration(new Date(now.getTime() + expiration))
                    .signWith(getSecretKey(tenantId))
                    .compact();
        } catch (final Exception e) {
        	e.printStackTrace();
        	return null;
        }
    }

    /**
     * Get Claims from JWT Token
     * 
     * @param token
     * @param tenantId
     * @return
     */
    public static Optional<Claims> claims(
            @Nonnull final String token, 
            @Nonnull final String tenantId) {

        try {
            return Optional.ofNullable(Jwts.parser()
                    .verifyWith(getSecretKey(tenantId))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload());
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Extract all claims from JWT Token
     * 
     * @param token
     * @param tenantId
     * @param isAccessToken
     * @return
     */
    public static Map<String, Object> extractAllClaims(
            @Nonnull final String token, 
            @Nonnull final String tenantId) {
        final Optional<Claims> claims = claims(token, tenantId);
        return claims.map(Claims::entrySet)
                     .map(entries -> {
                         Map<String, Object> map = new HashMap<>();
                         for (var entry : entries) {
                             map.put(entry.getKey(), entry.getValue());
                         }
                         return map;
                     })
                     .orElse(new HashMap<>());
    }

    /**
     * Get Secret Key for each tenant.
     * '-', '_', '.', etc. special characters in tenantId are safely handled.
     * 
     * @param tenantId
     * @return
     */
    private static SecretKey getSecretKey(@Nonnull final String tenantId) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest((tenantId + ONETIME_TOKEN_KEY).getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
