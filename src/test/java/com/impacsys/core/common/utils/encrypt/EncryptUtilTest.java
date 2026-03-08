package com.impacsys.core.common.utils.encrypt;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for EncryptUtil demonstrating how salt-based password hashing works.
 * To test {@link EncryptUtil}
 * 
 * @author hkb@impacsys.kr
 * @since 2025.08.19
 */
@DisplayName("EncryptUtil Tests")
public class EncryptUtilTest {

    @Test
    @DisplayName("같은 패스워드를 두 번 해시화하면 다른 결과가 나와야 함 (salt가 다르기 때문)")
    public void testHashPassword_SamePasswordDifferentHashes() {
        // Given
        String plainPassword = "mySecretPassword123";
        
        // When
        String hashedPassword1 = EncryptUtil.hashPassword(plainPassword);
        String hashedPassword2 = EncryptUtil.hashPassword(plainPassword);
        
        // Then
        assertNotNull(hashedPassword1);
        assertNotNull(hashedPassword2);
        assertNotEquals(hashedPassword1, hashedPassword2, "같은 패스워드라도 다른 해시값이 생성되어야 함");
        
    }

    @Test
    @DisplayName("해시된 패스워드는 원본 패스워드와 검증이 성공해야 함")
    public void testVerifyPassword_CorrectPassword() {
        // Given
        String plainPassword = "mySecretPassword123";
        String hashedPassword = EncryptUtil.hashPassword(plainPassword);
        
        // When
        boolean isValid = EncryptUtil.verifyPassword(plainPassword, hashedPassword);
        
        // Then
        assertTrue(isValid, "올바른 패스워드는 검증에 성공해야 함");
    }

    @Test
    @DisplayName("잘못된 패스워드는 검증이 실패해야 함")
    public void testVerifyPassword_WrongPassword() {
        // Given
        String plainPassword = "mySecretPassword123";
        String wrongPassword = "wrongPassword";
        String hashedPassword = EncryptUtil.hashPassword(plainPassword);
        
        // When
        boolean isValid = EncryptUtil.verifyPassword(wrongPassword, hashedPassword);
        
        // Then
        assertFalse(isValid, "잘못된 패스워드는 검증에 실패해야 함");
    }

    @Test
    @DisplayName("다른 해시값들도 모두 원본 패스워드와 검증이 성공해야 함")
    public void testVerifyPassword_MultipleHashes() {
        // Given
        String plainPassword = "testPassword456";
        
        // When - 여러 번 해시화
        String hash1 = EncryptUtil.hashPassword(plainPassword);
        String hash2 = EncryptUtil.hashPassword(plainPassword);
        String hash3 = EncryptUtil.hashPassword(plainPassword);
        
        // Then - 모든 해시가 원본 패스워드와 매치되어야 함
        assertTrue(EncryptUtil.verifyPassword(plainPassword, hash1), "첫 번째 해시 검증 성공");
        assertTrue(EncryptUtil.verifyPassword(plainPassword, hash2), "두 번째 해시 검증 성공");
        assertTrue(EncryptUtil.verifyPassword(plainPassword, hash3), "세 번째 해시 검증 성공");
        
        // 해시값들은 모두 달라야 함
        assertNotEquals(hash1, hash2);
        assertNotEquals(hash2, hash3);
        assertNotEquals(hash1, hash3);
    }

    @Test
    @DisplayName("null 또는 빈 패스워드는 예외를 발생시켜야 함")
    public void testHashPassword_InvalidInput() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            EncryptUtil.hashPassword(null);
        }, "null 패스워드는 예외 발생");

        assertThrows(IllegalArgumentException.class, () -> {
            EncryptUtil.hashPassword("");
        }, "빈 패스워드는 예외 발생");

        assertThrows(IllegalArgumentException.class, () -> {
            EncryptUtil.hashPassword("   ");
        }, "공백만 있는 패스워드는 예외 발생");
    }

    @Test
    @DisplayName("검증 시 null 또는 빈 값은 예외를 발생시켜야 함")
    public void testVerifyPassword_InvalidInput() {
        // Given
        String validHash = EncryptUtil.hashPassword("testPassword");
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            EncryptUtil.verifyPassword(null, validHash);
        }, "null 패스워드는 예외 발생");

        assertThrows(IllegalArgumentException.class, () -> {
            EncryptUtil.verifyPassword("password", null);
        }, "null 해시는 예외 발생");

        assertThrows(IllegalArgumentException.class, () -> {
            EncryptUtil.verifyPassword("", validHash);
        }, "빈 패스워드는 예외 발생");

        assertThrows(IllegalArgumentException.class, () -> {
            EncryptUtil.verifyPassword("password", "");
        }, "빈 해시는 예외 발생");
    }

    @Test
    @DisplayName("문자열 해시화 테스트 (salt 없이)")
    public void testHashString() {
        // Given
        String input1 = "testString";
        String input2 = "testString";
        String input3 = "differentString";
        
        // When
        String hash1 = EncryptUtil.hashString(input1);
        String hash2 = EncryptUtil.hashString(input2);
        String hash3 = EncryptUtil.hashString(input3);
        
        // Then
        assertNotNull(hash1);
        assertNotNull(hash2);
        assertNotNull(hash3);
        
        // 같은 입력은 같은 해시 (salt 없음)
        assertEquals(hash1, hash2, "같은 문자열은 같은 해시값");
        assertNotEquals(hash1, hash3, "다른 문자열은 다른 해시값");
        
        System.out.println("문자열 해시1: " + hash1);
        System.out.println("문자열 해시2: " + hash2);
        System.out.println("문자열 해시3: " + hash3);
    }

    @Test
    @DisplayName("실제 사용 시나리오 테스트")
    public void testRealWorldScenario() {
        // 시나리오: 사용자 회원가입과 로그인
        
        // 1. 회원가입 - 패스워드 해시화
        String userEmail = "user@example.com";
        String plainPassword = "userPassword123!";
        String hashedPasswordForStorage = EncryptUtil.hashPassword(plainPassword);
        
        System.out.println("=== 회원가입 ===");
        System.out.println("이메일: " + userEmail);
        System.out.println("원본 패스워드: " + plainPassword);
        System.out.println("저장될 해시: " + hashedPasswordForStorage);
        
        // 2. 로그인 시도 - 패스워드 검증
        String loginPassword = "userPassword123!";
        boolean loginSuccess = EncryptUtil.verifyPassword(loginPassword, hashedPasswordForStorage);
        
        System.out.println("\n=== 로그인 시도 ===");
        System.out.println("입력된 패스워드: " + loginPassword);
        System.out.println("로그인 성공: " + loginSuccess);
        
        assertTrue(loginSuccess, "올바른 패스워드로 로그인 성공");
        
        // 3. 잘못된 패스워드로 로그인 시도
        String wrongLoginPassword = "wrongPassword";
        boolean wrongLoginAttempt = EncryptUtil.verifyPassword(wrongLoginPassword, hashedPasswordForStorage);
        
        System.out.println("\n=== 잘못된 로그인 시도 ===");
        System.out.println("입력된 패스워드: " + wrongLoginPassword);
        System.out.println("로그인 성공: " + wrongLoginAttempt);
        
        assertFalse(wrongLoginAttempt, "잘못된 패스워드로 로그인 실패");
    }
}
