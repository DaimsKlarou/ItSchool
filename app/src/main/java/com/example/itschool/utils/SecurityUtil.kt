package com.example.itschool.utils


import android.os.Build
import androidx.annotation.RequiresApi
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

object SecurityUtil {

    // Générer une paire de clés RSA (publique/privée)
    fun generateKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        return keyPairGenerator.generateKeyPair()
    }

    // Chiffrement du message
    @RequiresApi(Build.VERSION_CODES.O)
    fun encryptMessage(message: String, recipientPublicKey: PublicKey): Pair<String, String> {
        // Génération d'une clé symétrique AES
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256)
        val secretKey = keyGenerator.generateKey()

        // Chiffrer le message avec AES
        val aesCipher = Cipher.getInstance("AES")
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedMessage = aesCipher.doFinal(message.toByteArray())
        val encryptedMessageBase64 = Base64.getEncoder().encodeToString(encryptedMessage)

        // Chiffrer la clé AES avec la clé publique RSA
        val rsaCipher = Cipher.getInstance("RSA")
        rsaCipher.init(Cipher.ENCRYPT_MODE, recipientPublicKey)
        val encryptedSecretKey = rsaCipher.doFinal(secretKey.encoded)
        val encryptedSecretKeyBase64 = Base64.getEncoder().encodeToString(encryptedSecretKey)

        return Pair(encryptedMessageBase64, encryptedSecretKeyBase64)
    }

    // Déchiffrement du message
    @RequiresApi(Build.VERSION_CODES.O)
    fun decryptMessage(encryptedMessage: String, encryptedSecretKey: String, recipientPrivateKey: PrivateKey): String {
        // Déchiffrement de la clé AES
        val rsaCipher = Cipher.getInstance("RSA")
        rsaCipher.init(Cipher.DECRYPT_MODE, recipientPrivateKey)
        val secretKeyBytes = rsaCipher.doFinal(Base64.getDecoder().decode(encryptedSecretKey))
        val secretKey = SecretKeySpec(secretKeyBytes, "AES")

        // Déchiffrement du message avec AES
        val aesCipher = Cipher.getInstance("AES")
        aesCipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decryptedMessageBytes = aesCipher.doFinal(Base64.getDecoder().decode(encryptedMessage))

        return String(decryptedMessageBytes)
    }

}