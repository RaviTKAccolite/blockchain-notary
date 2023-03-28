package com.example.demo.service.util;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JWTEncryption {

  private String hmacSha256(String data, String secret) {
    try {

      byte[] hash = secret.getBytes(StandardCharsets.UTF_8);
      Mac sha256Hmac = Mac.getInstance("HmacSHA256");
      SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA256");
      sha256Hmac.init(secretKey);

      byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

      return Base64.getEncoder().encodeToString(signedBytes);
    } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
      log.info("Exception in JWT encryption "+ ex);
      return null;
    }
  }

  public String getNewJwtToken(String header, String payload, int length, String alphaNumericString){

    String password = getAlphaNumericString(length, alphaNumericString);
    String headerBase64 = Base64.getEncoder().encodeToString(header.getBytes(StandardCharsets.UTF_8));
    String payloadBase64 = Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    String signature = hmacSha256(headerBase64 +"."+ payloadBase64, password);

    String jwtToken = headerBase64 + "." + payloadBase64 + "." + signature;

    return jwtToken;
  }

  static String getAlphaNumericString(int n, String alphaNumericString)
  {

    // create StringBuffer size of alphaNumericString
    StringBuilder sb = new StringBuilder(n);
    for (int i = 0; i < n; i++) {
      int index  = (int)(alphaNumericString.length()* Math.random());
      sb.append(alphaNumericString.charAt(index));
    }

    return sb.toString();
  }

}
