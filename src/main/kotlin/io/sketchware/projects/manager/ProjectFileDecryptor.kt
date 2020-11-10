package io.sketchware.projects.manager

import java.io.RandomAccessFile
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object ProjectFileDecryptor {

    private const val decryptKey = "sketchwaresecure"

    /**
     * Decrypt project list file, return ByteArray
     * @param path path to file
     */
    fun decrypt(path: String): ByteArray {
        val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val decryptKetBytes = decryptKey.toByteArray()
        cipher.init(2, SecretKeySpec(decryptKetBytes, "AES"), IvParameterSpec(decryptKetBytes))
        val randomAccessFile = RandomAccessFile(path, "r")
        val bytes = ByteArray(randomAccessFile.length().toInt())
        randomAccessFile.readFully(bytes)
        return cipher.doFinal(bytes)
    }

    /**
     * Encrypt project list file
     * @param content ListFileModel Json
     * @return nullable ByteArray with encrypted data
     */
    fun encrypt(content: String): ByteArray? {
        val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val bytes = decryptKey.toByteArray()
        cipher.init(1, SecretKeySpec(bytes, "AES"), IvParameterSpec(bytes))
        return cipher.doFinal(content.toByteArray())
    }
}