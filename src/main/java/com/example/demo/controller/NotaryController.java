package com.example.demo.controller;

import com.example.demo.model.AuthorizationRequestBody;
import com.example.demo.service.NotaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@Slf4j
public class NotaryController {

  private static final String NOTARY_CONTROLLER = "NotaryController:: ";

  @Autowired
  NotaryService notaryService;

  @PostMapping(value = "/getAuthorizationToken")
  public ResponseEntity<String> getAuthorizationToken(@RequestBody AuthorizationRequestBody requestBody)
      throws Exception {
    log.info("GetAuthorizationToken called by "+ requestBody.getNodeName());
    HttpHeaders httpHeaders = notaryService.getAuthorizationToken(requestBody);
    return ResponseEntity.ok().headers(httpHeaders).body("Successful");
  }

}
