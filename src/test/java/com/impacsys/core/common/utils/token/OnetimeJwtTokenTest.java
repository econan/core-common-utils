package com.impacsys.core.common.utils.token;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;

/**
 * Unit tests for {@link OnetimeJwtToken}
 * 
 * @author hkb@impacsys.kr
 * @since 2025.11.17
 */
public class OnetimeJwtTokenTest {

    private String tenantId;
    private Map<String, Object> testClaims;
    private long expiration;

    @BeforeEach
    void setUp() {
        tenantId = "test-tenant-id"; // 실제 subdomain 형태로 복원 (- 문자 포함)
        testClaims = new HashMap<>();
        testClaims.put("userId", "user123");
        testClaims.put("role", "admin");
        testClaims.put("email", "test@example.com");
        expiration = 3600000L; // 1 hour in milliseconds
    }

    @Test
    @DisplayName("JWT 토큰 생성 테스트")
    void testGenerateToken() {
        // Given
        // setUp에서 준비된 데이터 사용

        // When
        String token = OnetimeJwtToken.generateToken(tenantId, testClaims, expiration);

        // Then
        assertNotNull(token, "토큰이 null이어서는 안됩니다");
        assertFalse(token.isEmpty(), "토큰이 비어있어서는 안됩니다");
        assertTrue(token.contains("."), "JWT 토큰은 점(.)으로 구분된 형식이어야 합니다");
        
        // JWT 토큰은 3개의 부분으로 구성되어야 함 (header.payload.signature)
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT 토큰은 3개의 부분으로 구성되어야 합니다");
    }

    @Test
    @DisplayName("유효한 토큰에서 Claims 추출 테스트")
    void testClaimsWithValidToken() {
        // Given
        String token = OnetimeJwtToken.generateToken(tenantId, testClaims, expiration);

        // When
        Optional<Claims> claims = OnetimeJwtToken.claims(token, tenantId);

        // Then
        assertTrue(claims.isPresent(), "유효한 토큰에서 Claims를 추출할 수 있어야 합니다");
        
        Claims claimsData = claims.get();
        assertEquals(tenantId, claimsData.getIssuer(), "Issuer가 일치해야 합니다");
        assertNotNull(claimsData.getIssuedAt(), "발행 시간이 있어야 합니다");
        assertNotNull(claimsData.getExpiration(), "만료 시간이 있어야 합니다");
    }

    @Test
    @DisplayName("잘못된 토큰에서 Claims 추출 테스트")
    void testClaimsWithInvalidToken() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When
        Optional<Claims> claims = OnetimeJwtToken.claims(invalidToken, "another-tenant-id");

        // Then
        assertFalse(claims.isPresent(), "잘못된 토큰에서는 Claims를 추출할 수 없어야 합니다");
    }

    @Test
    @DisplayName("잘못된 테넌트 ID로 Claims 추출 테스트")
    void testClaimsWithWrongTenantId() {
        // Given
        String token = OnetimeJwtToken.generateToken(tenantId, testClaims, expiration);
        String wrongTenantId = "wrong-tenant-id";

        // When
        Optional<Claims> claims = OnetimeJwtToken.claims(token, wrongTenantId);

        // Then
        assertFalse(claims.isPresent(), "잘못된 테넌트 ID로는 Claims를 추출할 수 없어야 합니다");
    }

    @Test
    @DisplayName("모든 Claims 추출 테스트")
    void testExtractAllClaims() {
        // Given
        String token = OnetimeJwtToken.generateToken(tenantId, testClaims, expiration);

        // When
        Map<String, Object> extractedClaims = OnetimeJwtToken.extractAllClaims(token, tenantId);

        // Then
        assertNotNull(extractedClaims, "추출된 Claims가 null이어서는 안됩니다");
        assertFalse(extractedClaims.isEmpty(), "추출된 Claims가 비어있어서는 안됩니다");
        
        // 커스텀 Claims 확인
        assertEquals("user123", extractedClaims.get("userId"), "userId가 일치해야 합니다");
        assertEquals("admin", extractedClaims.get("role"), "role이 일치해야 합니다");
        assertEquals("test@example.com", extractedClaims.get("email"), "email이 일치해야 합니다");
        
        // 표준 Claims 확인
        assertEquals(tenantId, extractedClaims.get("iss"), "Issuer가 일치해야 합니다");
        assertNotNull(extractedClaims.get("iat"), "발행 시간이 있어야 합니다");
        assertNotNull(extractedClaims.get("exp"), "만료 시간이 있어야 합니다");
    }

    @Test
    @DisplayName("잘못된 토큰으로 모든 Claims 추출 테스트")
    void testExtractAllClaimsWithInvalidToken() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When
        Map<String, Object> extractedClaims = OnetimeJwtToken.extractAllClaims(invalidToken, "another-tenant-id");

        // Then
        assertNotNull(extractedClaims, "결과가 null이어서는 안됩니다");
        assertTrue(extractedClaims.isEmpty(), "잘못된 토큰에서는 빈 Map을 반환해야 합니다");
    }

    @Test
    @DisplayName("빈 Claims로 토큰 생성 테스트")
    void testGenerateTokenWithEmptyClaims() {
        // Given
        Map<String, Object> emptyClaims = new HashMap<>();

        // When
        String token = OnetimeJwtToken.generateToken(tenantId, emptyClaims, expiration);

        // Then
        assertNotNull(token, "빈 Claims로도 토큰이 생성되어야 합니다");
        
        Optional<Claims> claims = OnetimeJwtToken.claims(token, tenantId);
        assertTrue(claims.isPresent(), "생성된 토큰에서 Claims를 추출할 수 있어야 합니다");
    }

    @Test
    @DisplayName("짧은 만료 시간으로 토큰 생성 테스트")
    void testGenerateTokenWithShortExpiration() {
        // Given
        long shortExpiration = 1000L; // 1 second

        // When
        String token = OnetimeJwtToken.generateToken(tenantId, testClaims, shortExpiration);

        // Then
        assertNotNull(token, "짧은 만료 시간으로도 토큰이 생성되어야 합니다");
        
        Optional<Claims> claims = OnetimeJwtToken.claims(token, tenantId);
        assertTrue(claims.isPresent(), "생성된 토큰에서 Claims를 추출할 수 있어야 합니다");
        
        long expirationTime = claims.get().getExpiration().getTime();
        long issuedTime = claims.get().getIssuedAt().getTime();
        long actualExpiration = expirationTime - issuedTime;
        
        assertTrue(actualExpiration <= shortExpiration + 1000, 
                "실제 만료 시간이 설정한 시간과 유사해야 합니다 (1초 여유)");
    }

    @Test
    @DisplayName("null tenantId로 토큰 생성 테스트")
    void testGenerateTokenWithNullTenantId() {
        // Given
        String nullTenantId = null;

        // When
        String token = OnetimeJwtToken.generateToken(nullTenantId, testClaims, expiration);

        // Then
        // 실제 구현에서는 null tenantId로도 토큰이 생성됨
        assertNotNull(token, "null tenantId로도 토큰이 생성될 수 있습니다");
        
        // 생성된 토큰의 기본 구조 검증
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT 토큰은 3개의 부분으로 구성되어야 합니다");
    }

    @Test
    @DisplayName("null claims로 토큰 생성 테스트")
    void testGenerateTokenWithNullClaims() {
        // Given
        Map<String, Object> nullClaims = null;

        // When
        String token = OnetimeJwtToken.generateToken(tenantId, nullClaims, expiration);

        // Then
        // 실제 구현에서는 null claims로도 토큰이 생성됨 (단, claims는 비어있음)
        assertNotNull(token, "null claims로도 토큰이 생성될 수 있습니다");
        
        // 생성된 토큰의 기본 구조 검증
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT 토큰은 3개의 부분으로 구성되어야 합니다");
        
        // claims가 비어있는지 확인 (자동 생성된 시스템 claims만 있어야 함)
        Map<String, Object> extractedClaims = OnetimeJwtToken.extractAllClaims(token, tenantId);
        assertNotNull(extractedClaims, "추출된 Claims가 null이어서는 안됩니다");
        // 시스템 claims(iss, iat, exp)만 있고 사용자 정의 claims는 없어야 함
        assertFalse(extractedClaims.containsKey("userId"), "userId claim은 없어야 합니다");
        assertTrue(extractedClaims.containsKey("iss"), "Issuer claim은 있어야 합니다");
    }

    @Test
    @DisplayName("다양한 데이터 타입의 Claims 테스트")
    void testGenerateTokenWithVariousDataTypes() {
        // Given
        Map<String, Object> variousClaims = new HashMap<>();
        variousClaims.put("stringValue", "test");
        variousClaims.put("intValue", 123);
        variousClaims.put("booleanValue", true);
        variousClaims.put("longValue", 123456789L);

        // When
        String token = OnetimeJwtToken.generateToken(tenantId, variousClaims, expiration);

        // Then
        assertNotNull(token, "다양한 데이터 타입의 Claims로 토큰이 생성되어야 합니다");
        
        Map<String, Object> extractedClaims = OnetimeJwtToken.extractAllClaims(token, tenantId);
        assertEquals("test", extractedClaims.get("stringValue"));
        assertEquals(123, extractedClaims.get("intValue"));
        assertEquals(true, extractedClaims.get("booleanValue"));
        assertEquals(123456789L, ((Number) extractedClaims.get("longValue")).longValue());
    }

    @Test
    @DisplayName("다양한 subdomain 형태의 tenantId 테스트")
    void testVariousSubdomainFormats() {
        // Given
        String[] subdomainTenantIds = {
            "simple-tenant",
            "complex-tenant-name",
            "tenant_with_underscore", 
            "tenant.with.dots",
            "123-numeric-tenant",
            "UPPER-case-tenant"
        };

        for (String subdomainTenantId : subdomainTenantIds) {
            // When
            String token = OnetimeJwtToken.generateToken(subdomainTenantId, testClaims, expiration);

            // Then
            assertNotNull(token, "subdomain 형태 '" + subdomainTenantId + "'로 토큰이 생성되어야 합니다");
            
            Optional<Claims> claims = OnetimeJwtToken.claims(token, subdomainTenantId);
            assertTrue(claims.isPresent(), "subdomain 형태 '" + subdomainTenantId + "'에서 Claims를 추출할 수 있어야 합니다");
            
            assertEquals(subdomainTenantId, claims.get().getIssuer(), 
                    "Issuer가 원본 tenantId와 일치해야 합니다");
        }
    }
}
