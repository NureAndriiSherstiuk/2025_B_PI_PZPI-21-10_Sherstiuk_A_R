package com.example.fliplearn_final.util


import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.util.Base64

object PasswordHasher {
    private const val SALT_LENGTH = 16
    private const val ITERATION_COUNT = 65536
    private const val KEY_LENGTH = 256

    fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)
        return salt
    }

    fun hashPassword(password: String, salt: ByteArray): String {
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val hash = factory.generateSecret(spec).encoded
        return Base64.getEncoder().encodeToString(salt + hash)
    }

    fun verifyPassword(inputPassword: String, storedHashBase64: String): Boolean {
        val fullHash = Base64.getDecoder().decode(storedHashBase64)

        val salt = fullHash.sliceArray(0 until SALT_LENGTH)
        val originalHash = fullHash.sliceArray(SALT_LENGTH until fullHash.size)

        val spec: KeySpec = PBEKeySpec(inputPassword.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val inputHash = factory.generateSecret(spec).encoded

        return inputHash.contentEquals(originalHash)
    }
}
