package com.example.demo.service.util;



import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;

@Service
public class AESEncryption {

  private Cipher encryptionCipher;

  private SecretKey secretKeyGenerator(String keyString) throws Exception {
    byte[] encodedKey     = keyString.getBytes(StandardCharsets.UTF_8);
    SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    return key;
  }

  public String encrypt(String data, String keyString) throws Exception {
    SecretKey key = secretKeyGenerator(keyString);

    byte[] dataInBytes = data.getBytes();
    encryptionCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    encryptionCipher.init(Cipher.ENCRYPT_MODE, key);
    byte[] encryptedBytes = encryptionCipher.doFinal(dataInBytes);
    return Base64.getEncoder().encodeToString(encryptedBytes);
  }

  public String getPublicKey(){
    return Base64.getEncoder().encodeToString(encryptionCipher.getIV());
  }

}
