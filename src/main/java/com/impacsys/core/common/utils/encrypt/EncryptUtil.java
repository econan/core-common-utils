package com.impacsys.core.common.utils.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import jakarta.annotation.Nonnull;

/**
 * Utility class for one-way encryption operations.
 * This class provides methods for hashing passwords and other sensitive data
 * using secure algorithms with salt for enhanced security.
 * 
 * For unit test {@link EncryptUtilTest}
 * 
 * @author hkb@impacsys.kr
 * @since 2025.08.19
 */
public class EncryptUtil {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    /**
     * Private constructor to prevent instantiation.
     */
    private EncryptUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Generates a random salt for password hashing.
     * 
     * @return a byte array representing the salt
     */
    private static byte[] generateSalt() {
        final SecureRandom random = new SecureRandom();
        final byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Hashes a password with a generated salt using SHA-256 algorithm.
     * The salt is prepended to the hashed result for storage.
     * Each password gets a unique salt for enhanced security.
     * 
     * Format: [16-byte salt][32-byte hash] -> Base64 encoded
     * 
     * @param password the plain text password to hash
     * @return the Base64 encoded string containing salt + hash
     * @throws IllegalArgumentException if password is null or empty
     */
    public static String hashPassword(@Nonnull final String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        try {
            final byte[] salt = generateSalt();
            final MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            
            // Add salt to the digest
            digest.update(salt);
            
            // Hash the password
            final byte[] hashedPassword = digest.digest(password.getBytes());
            
            // Combine salt and hash
            final byte[] saltedHash = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, saltedHash, 0, salt.length);
            System.arraycopy(hashedPassword, 0, saltedHash, salt.length, hashedPassword.length);
            
            // Return Base64 encoded result
            return Base64.getEncoder().encodeToString(saltedHash);
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    /**
     * Verifies a plain text password against a stored hash.
     * Extracts the salt from the stored hash and re-hashes the plain text password
     * to compare with the stored hash.
     * 
     * Process:
     * 1. Decode the stored Base64 string
     * 2. Extract the first 16 bytes as salt
     * 3. Extract the remaining bytes as the original hash
     * 4. Hash the input password with the extracted salt
     * 5. Compare the results
     * 
     * @param password the plain text password to verify
     * @param storedHash the stored Base64 encoded salt + hash
     * @return true if the password matches the stored hash, false otherwise
     * @throws IllegalArgumentException if password or storedHash is null or empty
     */
    public static boolean verifyPassword(@Nonnull final String password, @Nonnull final String storedHash) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        if (storedHash == null || storedHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Stored hash cannot be null or empty");
        }

        try {
            // Decode the stored hash
            final byte[] saltedHash = Base64.getDecoder().decode(storedHash);
            
            if (saltedHash.length < SALT_LENGTH) {
                return false;
            }
            
            // Extract salt
            final byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(saltedHash, 0, salt, 0, SALT_LENGTH);
            
            // Hash the provided password with the extracted salt
            final MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            digest.update(salt);
            final byte[] hashedPassword = digest.digest(password.getBytes());
            
            // Compare the hashes
            if (saltedHash.length - SALT_LENGTH != hashedPassword.length) {
                return false;
            }
            
            for (int i = 0; i < hashedPassword.length; i++) {
                if (saltedHash[SALT_LENGTH + i] != hashedPassword[i]) {
                    return false;
                }
            }
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hashes a string using SHA-256 without salt.
     * This method should be used for non-password data that needs to be hashed.
     * 
     * @param input the input string to hash
     * @return the Base64 encoded hash
     * @throws IllegalArgumentException if input is null
     */
    public static String hashString(@Nonnull final String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        try {
            final MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            final byte[] hashedBytes = digest.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("String hashing failed", e);
        }
    }
}
