/*
 * This file is part of SimpleTextCrypt.
 * Copyright (c) 2015-2026, Aidin Gharibnavaz <aidin@aidinhut.com>
 *
 * SimpleTextCrypt is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SimpleTextCrypt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SimpleTextCrypt.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aidinhut.simpletextcrypt

import android.util.Base64
import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2Mode
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Provides methods for encrypting and decrypting data.
 */
object Crypter {

    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val LEGACY_TRANSFORMATION = "AES/CBC/PKCS5PADDING"
    private const val IV_SIZE = 12 // Recommended size for GCM
    private const val IV_SIZE_IN_BASE64 = 16
    private const val TAG_SIZE = 128 // Tag size in bits

    fun encrypt(password: String, input: String): String {
        // IV (Initialization Vector) generates randomly, and sends along with the message.
        // Since we use CBC mode, 1. IV *must* be unique in every message 2. IV does not need
        // to be secret.
        // See: https://stackoverflow.com/questions/3436864/sending-iv-along-with-cipher-text-safe

        val iv = getRandomIV()
        val secretKey = deriveKey(password, iv)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val gcmParameterSpec = GCMParameterSpec(TAG_SIZE, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec)
        val encrypted = cipher.doFinal(input.toByteArray())

        return Base64.encodeToString(iv, Base64.DEFAULT) +
                Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    fun decrypt(password: String, input: String): String {
        // First 12 chars is the random IV.
        val ivString = input.substring(0, IV_SIZE_IN_BASE64)
        val iv = Base64.decode(ivString, Base64.DEFAULT)
        val inputBytes = Base64.decode(input.substring(IV_SIZE_IN_BASE64), Base64.DEFAULT)

        val secretKey = deriveKey(password, iv)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val gcmParameterSpec = GCMParameterSpec(TAG_SIZE, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec)
        val original = cipher.doFinal(inputBytes)

        return String(original)
    }

    fun legacyDecrypt(password: String, input: String): String {
        // First 16 chars is the random IV.
        val ivKey = input.substring(0, 16)
        val encryptedMessage = input.substring(16)

        val iv = IvParameterSpec(ivKey.toByteArray(Charsets.UTF_8))
        val secretKey = legacyDeriveKey(password, ivKey)

        val cipher = Cipher.getInstance(LEGACY_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)

        val original = cipher.doFinal(Base64.decode(encryptedMessage, Base64.DEFAULT))

        return String(original)
    }

    private fun getRandomIV(): ByteArray {
        val iv = ByteArray(IV_SIZE)
        SecureRandom().nextBytes(iv)
        return iv
    }

    // TODO: Should I use IV as salt? Should I use another random string?
    //  Recommended salt is 16 bytes. 12 bytes (that I'm using) is the minimum.

    /**
     * Derives a secure key from the given password.
     * Uses Argon2 algorithm, which is the recommendation in 2026.
     */
    private fun deriveKey(password: String, salt: ByteArray): SecretKey {
        val argon2Kt = Argon2Kt()

        // Following parameters adjusted so it takes about 400ms on my own device.
        val hashResult = argon2Kt.hash(
            Argon2Mode.ARGON2_ID,
            password.toByteArray(),
            salt,
            5,
            64 * 1024
        )

        val passwordHash = hashResult.rawHashAsByteArray()
        return SecretKeySpec(passwordHash, ALGORITHM)
    }

    /**
     * Derives a key from the specified password.
     * Uses older, insecure algorithms. Only used for decrypting old app's encrypted data.
     */
    private fun legacyDeriveKey(password: String, salt: String): SecretKey {
        val passwordChars = password.toCharArray()

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec = PBEKeySpec(passwordChars, salt.toByteArray(), 2000, 256)

        return SecretKeySpec(factory.generateSecret(spec).encoded, ALGORITHM)
    }
}
