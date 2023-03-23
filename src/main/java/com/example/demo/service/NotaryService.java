package com.example.demo.service;

import com.example.demo.model.AuthorizationRequestBody;
import org.springframework.http.HttpHeaders;

public interface NotaryService {

  HttpHeaders getAuthorizationToken(AuthorizationRequestBody requestBody) throws Exception;

}
