package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * This class handles password security operations
 * It provides methods for hashing verifying and validating passwords
 * It uses SHA 256 algorithm with salt for better security
 */
public class PasswordUtil {
    
    /**
     * Algorithm used for hashing passwords
     */
    private static final String ALGORITHM = "SHA-256";
    
    /**
     * Length of salt in bytes
     */
    private static final int SALT_LENGTH = 16;
    
    /**
     * This method generates a random salt value
     */
    public static byte[] generateSalt() {
        
        /**
         * Create secure random generator
         */
        SecureRandom random = new SecureRandom();
        
        /**
         * Create byte array for salt
         */
        byte[] salt = new byte[SALT_LENGTH];
        
        /**
         * Fill salt with random bytes
         */
        random.nextBytes(salt);
        
        return salt;
    }
    
    /**
     * This method hashes a password using salt
     */
    public static String hashPassword(String password, byte[] salt) {
        try {
            
            /**
             * Create message digest instance
             */
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            
            /**
             * Add salt before hashing
             */
            md.update(salt);
            
            /**
             * Generate hashed password
             */
            byte[] hashed = md.digest(password.getBytes());
            
            /**
             * Convert hashed bytes to Base64 string
             */
            return Base64.getEncoder().encodeToString(hashed);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }
    
    /**
     * This method hashes password with a newly generated salt
     */
    public static String[] hashPasswordWithSalt(String password) {
        
        /**
         * Generate random salt
         */
        byte[] salt = generateSalt();
        
        /**
         * Hash password using salt
         */
        String hashed = hashPassword(password, salt);
        
        /**
         * Convert salt to Base64 string for storage
         */
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        
        /**
         * Return both hashed password and salt
         */
        return new String[]{hashed, saltBase64};
    }
    
    /**
     * This method verifies a password using stored hash and salt
     */
    public static boolean verifyPassword(String password, String storedHash, String saltBase64) {
        
        /**
         * Decode stored salt from Base64
         */
        byte[] salt = Base64.getDecoder().decode(saltBase64);
        
        /**
         * Hash the input password using same salt
         */
        String computedHash = hashPassword(password, salt);
        
        /**
         * Compare computed hash with stored hash
         */
        return computedHash.equals(storedHash);
    }
    
    /**
     * This method performs simple hashing without salt
     * Not recommended for real applications
     */
    public static String simpleHash(String password) {
        try {
            
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] hashed = md.digest(password.getBytes());
            
            return Base64.getEncoder().encodeToString(hashed);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }
    
    /**
     * This method checks if password is strong
     */
    public static boolean isStrongPassword(String password) {
        
        /**
         * Check minimum length
         */
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        /**
         * Check each character in password
         */
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        
        /**
         * Password is strong only if all conditions are met
         */
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
    
    /**
     * This method returns password strength message
     */
    public static String getPasswordStrengthMessage(String password) {
        
        if (password == null || password.isEmpty()) {
            return "Please enter a password";
        }
        
        int score = 0;
        
        /**
         * Increase score based on password strength factors
         */
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*].*")) score++;
        
        /**
         * Return strength level based on score
         */
        if (score <= 2) return "Weak";
        if (score <= 4) return "Medium";
        
        return "Strong";
    }
}