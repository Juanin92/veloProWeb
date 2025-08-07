package com.veloproweb.security.service;

import com.veloproweb.exceptions.security.PasswordDecryptionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class EncryptionService {

    @Value("${crypto.encryption.key}")
    private String encryptionKeyBase64; // Clave de encriptación AES en Base64

    public String getEncryptionKey() {
        return encryptionKeyBase64;
    }

    /**
     * Encripta una contraseña utilizando AES encriptado.
     * @param encryptedPassword contraseña a encriptar
     * @return contraseña encriptada
     */
    public String decrypt(String encryptedPassword) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(encryptionKeyBase64);
            SecretKeySpec secretKey = new SecretKeySpec(decodedKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new PasswordDecryptionException(
                    "Error al descifrar la contraseña. Posible corrupción de datos o clave incompatible");
        }
    }
}
