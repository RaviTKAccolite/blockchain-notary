package com.example.demo.controller;

import com.example.demo.model.AuthorizationRequestBody;
import com.example.demo.model.TransactionInitializerRequestBody;
import com.example.demo.service.NotaryService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

  @PostMapping(value = "/initializeTransaction")
  public ResponseEntity<String> initializeTransaction(
      @RequestBody TransactionInitializerRequestBody requestBody,
      @RequestHeader(value="node-id", required = false) String nodeId)
      throws Exception {
    log.info("InitializeTransaction called by "+ requestBody.getInitializerName());
    notaryService.initializeTransaction(requestBody, nodeId);
    return ResponseEntity.ok().body("Successful");
  }

  @PostMapping(value = "/validateMiningRequest")
  public ResponseEntity<String> validateMiningRequest(
      @RequestBody TransactionInitializerRequestBody requestBody,
      @RequestHeader(value="node-id", required = false) String nodeId)
      throws Exception {
    log.info("ValidateMiningRequest called by "+ requestBody.getInitializerName());
    notaryService.validateMiningRequest(nodeId);
    return ResponseEntity.ok().body("Successful");
  }

  @PostMapping(value = "/miningRequest")
  public ResponseEntity<List<String>> miningRequest(
      @RequestHeader(value="node-id", required = true) String nodeId)
      throws Exception {
    log.info("MiningRequest called by "+ nodeId);
    List<String> miningQuestion = notaryService.miningRequest(nodeId);
    return ResponseEntity.ok().body(miningQuestion);
  }

}
