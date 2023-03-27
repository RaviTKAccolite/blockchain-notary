package com.example.demo.service.util;


import com.example.demo.model.node.TransactionValidationRequest;
import com.example.demo.model.node.TransactionValidationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class NodeIntegrationService {

  @Autowired
  private RestTemplate restTemplate;

  public ResponseEntity<TransactionValidationResponse> nodeTransactionValidation(
      TransactionValidationRequest transactionValidationRequest, String hostPort) throws Exception {
    try {
      log.info("Calling init Transaction notary API");
      String hostUrl = "http://localhost:"+hostPort+"/v1/transactionValidation";
      HttpEntity<TransactionValidationRequest> requestEntity =
          new HttpEntity<>(transactionValidationRequest, getBaseRequestHeaders());
      return restTemplate.exchange(hostUrl, HttpMethod.POST,
          requestEntity, new ParameterizedTypeReference<TransactionValidationResponse>() {});
    } catch (Exception e) {
      log.info(" exception  occurred while invoking notary, exception message: " + e.getMessage());
      throw new Exception("Internal error");
    }
  }

  private HttpHeaders getBaseRequestHeaders() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    return httpHeaders;
  }
}
