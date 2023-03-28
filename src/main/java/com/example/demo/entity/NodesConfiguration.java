package com.example.demo.entity;

import com.example.demo.model.MiningCount;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NodesConfiguration {

  private String nodeName;

  private String nodeId;

  private String nodePassword;

  private String portNumber;

  private String transactionSecretKey;

  private List<String> authority;

  private String authorizationToken;

  private MiningCount miningCount;


}
