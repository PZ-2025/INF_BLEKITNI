package org.example.sys;

import org.example.sys.PasswordHasher;
import org.junit.jupiter.api.Test;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    @Test
    void testHashPassword_Generates64CharacterHash() throws NoSuchAlgorithmException, InvalidKeyException {
        String password = "test";
        int userId = 123;
        String hash = PasswordHasher.hashPassword(password, userId);
        assertEquals(64, hash.length(), "HMAC-SHA256 powinien generować 64-znakowy hash");
    }

    @Test
    void testVerifyPassword_CorrectPassword_ReturnsTrue() throws NoSuchAlgorithmException, InvalidKeyException {
        String password = "test";
        int userId = 123;
        String storedHash = PasswordHasher.hashPassword(password, userId);
        assertTrue(PasswordHasher.verifyPassword(storedHash, password, userId));
    }

    @Test
    void testVerifyPassword_WrongPassword_ReturnsFalse() throws NoSuchAlgorithmException, InvalidKeyException {
        String correctPassword = "test";
        String wrongPassword = "wrong";
        int userId = 123;
        String storedHash = PasswordHasher.hashPassword(correctPassword, userId);
        assertFalse(PasswordHasher.verifyPassword(storedHash, wrongPassword, userId));
    }

    @Test
    void testVerifyPassword_DifferentUserId_ReturnsFalse() throws NoSuchAlgorithmException, InvalidKeyException {
        String password = "test";
        int originalUserId = 123;
        int differentUserId = 456;
        String storedHash = PasswordHasher.hashPassword(password, originalUserId);
        assertFalse(PasswordHasher.verifyPassword(storedHash, password, differentUserId));
    }

    @Test
    void testHashPassword_EmptyPassword_ReturnsValidHash() throws NoSuchAlgorithmException, InvalidKeyException {
        String password = "";
        int userId = 123;
        String hash = PasswordHasher.hashPassword(password, userId);
        assertEquals(64, hash.length());
        assertTrue(PasswordHasher.verifyPassword(hash, password, userId));
    }

    @Test
    void testHashPassword_NonAsciiCharacters_ReturnsValidHash() throws NoSuchAlgorithmException, InvalidKeyException {
        String password = "pa$$wörd";
        int userId = 123;
        String hash = PasswordHasher.hashPassword(password, userId);
        assertEquals(64, hash.length());
        assertTrue(PasswordHasher.verifyPassword(hash, password, userId));
    }

    @Test
    void testVerifyPassword_ZeroUserId_ReturnsTrue() throws NoSuchAlgorithmException, InvalidKeyException {
        String password = "test";
        int userId = 0;
        String storedHash = PasswordHasher.hashPassword(password, userId);
        assertTrue(PasswordHasher.verifyPassword(storedHash, password, userId));
    }
}