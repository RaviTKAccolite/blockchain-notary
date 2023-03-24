package com.example.demo.service;

import com.example.demo.model.AuthorizationRequestBody;
import com.example.demo.model.TransactionInitializerRequestBody;
import org.springframework.http.HttpHeaders;

public interface NotaryService {

  HttpHeaders getAuthorizationToken(AuthorizationRequestBody requestBody) throws Exception;

  void initializeTransaction(TransactionInitializerRequestBody requestBody, String nodeId)
      throws Exception;
}
