package io.sketchware.encryptor

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Responsible for encrypting and decrypting project files.
 * Sketchware encrypts all files (excluding resources and collections).
 * This class deals with decryption of such files.
 */
object FileEncryptor {
    /**
     * Sketchware encrypts everything under one static key,
     * so it is used in the [encrypt] & [decrypt] method.
     */
    private val encryptKey = "sketchwaresecure".toByteArray()

    /**
     * Decryption of the incoming [byteArray] by [encryptKey].
     * @return [ByteArray] of decrypted [byteArray].
     */
    suspend fun decrypt(byteArray: ByteArray) = suspendCoroutine<ByteArray> { continuation ->
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(2, SecretKeySpec(encryptKey, "AES"), IvParameterSpec(encryptKey))
        continuation.resume(cipher.doFinal(byteArray))
    }

    /**
     * Encryption of the incoming [byteArray] by [encryptKey].
     * @return [ByteArray] of encrypted [byteArray].
     */
    suspend fun encrypt(byteArray: ByteArray) = suspendCoroutine<ByteArray> { continuation ->
        val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(1, SecretKeySpec(encryptKey, "AES"), IvParameterSpec(encryptKey))
        continuation.resume(cipher.doFinal(byteArray))
    }

}