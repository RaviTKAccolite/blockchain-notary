package com.example.demo.service.impl;

import com.example.demo.entity.NodesConfiguration;
import com.example.demo.model.AuthorizationRequestBody;
import com.example.demo.model.TransactionInitializerRequestBody;
import com.example.demo.model.node.TransactionValidationRequest;
import com.example.demo.model.node.TransactionValidationResponse;
import com.example.demo.service.NotaryService;
import com.example.demo.service.util.AESEncryption;
import com.example.demo.service.util.JsonReader;
import com.example.demo.service.util.NodeIntegrationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class NotaryServiceImpl implements NotaryService {

  JsonReader jsonReader = new JsonReader();

  AESEncryption aesEncryption = new AESEncryption();

  @Autowired
  NodeIntegrationService nodeIntegrationService;

  @Override
  public HttpHeaders getAuthorizationToken(AuthorizationRequestBody requestBody) throws Exception {
    HttpHeaders httpHeaders = new HttpHeaders();
    List<NodesConfiguration> nodesConfigurationList = fetchConfigurationList();

    Optional<NodesConfiguration> nodesConfigurationOptional = nodesConfigurationList.stream().filter(
        nodeConfig -> requestBody.getNodeName().contentEquals(nodeConfig.getNodeName())).findFirst();

    if(!ObjectUtils.isEmpty(nodesConfigurationOptional) &&
        !ObjectUtils.isEmpty(nodesConfigurationOptional.get()) &&
        nodesConfigurationOptional.get().getNodePassword().equals(requestBody.getNodePassword())){
      NodesConfiguration nodeConfiguration = nodesConfigurationOptional.get();
      httpHeaders.add("Authorization-Token", nodeConfiguration.getAuthorizationToken());
      httpHeaders.add("portNumber", nodeConfiguration.getPortNumber());
    } else {
      throw new Exception("Invalid User Credentials");
    }

    return httpHeaders;
  }

  @Override
  public void initializeTransaction(TransactionInitializerRequestBody requestBody, String nodeId)
      throws Exception {
    List<NodesConfiguration> nodesConfigurationList = fetchConfigurationList();
    // encrypt the message
    NodesConfiguration acceptorNodeConfiguration = getNodesConfiguration(requestBody.getAcceptorName(),
        nodesConfigurationList);
    NodesConfiguration initializerNodeConfiguration = getNodesConfiguration(requestBody.getInitializerName(),
        nodesConfigurationList);

    if(nodeId.contentEquals(initializerNodeConfiguration.getNodeId())){
      String message = requestBody.getMessage();
      aesEncryption.encrypt(message, acceptorNodeConfiguration.getTransactionSecretKey());
    }

    // pass it on the next node
    TransactionValidationRequest transactionValidationRequest = TransactionValidationRequest
        .builder()
        .initializerId(acceptorNodeConfiguration.getNodeId())
        .acceptorId(acceptorNodeConfiguration.getNodeId())
        .message(requestBody.getMessage())
        .build();

    Map<String, Boolean> visitedNode = new HashMap<String, Boolean>();
    List<String> connectedNodes = new ArrayList<>();
    Boolean isDestinationNode = false;
    Boolean isSuccessfulTransaction = nextNodeTraversalDFS(initializerNodeConfiguration.getNodeId(),
        acceptorNodeConfiguration.getNodeId(), visitedNode, nodesConfigurationList, connectedNodes, isDestinationNode, transactionValidationRequest);
    if(isSuccessfulTransaction){
//      return isSuccessfulTransaction;
    } else {
//      TODO Pending failed situation needs discussion
    }

  }

  @Override
  public void validateMiningRequest(String triggeredNodeId) throws Exception {
    List<NodesConfiguration> nodesConfigurationList = fetchConfigurationList();
    Map<String, Boolean> visitedNode = new HashMap<String, Boolean>();
    List<String> connectedNodes = new ArrayList<>();

    Boolean mineRequestValidated = nodeBroadcaster(triggeredNodeId, nodesConfigurationList, visitedNode, connectedNodes);
    if(mineRequestValidated && nodesConfigurationList.size() == visitedNode.size()){

    }

  }

  private Boolean nodeBroadcaster(String currentNodeId, List<NodesConfiguration> nodesConfigurationList,
      Map<String, Boolean> visitedNode, List<String> connectedNodes) throws Exception {
    NodesConfiguration currentNode = getSpecificNodeConfig(currentNodeId, nodesConfigurationList);

    visitedNode.put(currentNodeId, Boolean.TRUE);
    System.out.println("DFS : "+currentNodeId);
    // TODO logic to call the mining validation - node API

    connectedNodes.addAll(currentNode.getAuthority());

    while (!ObjectUtils.isEmpty(connectedNodes)) {
      String nextNodeId = connectedNodes.get(0);
      if (!visitedNode.containsKey(nextNodeId) || !visitedNode.get(nextNodeId)){
        Boolean isValidMine = nodeBroadcaster(nextNodeId, nodesConfigurationList, visitedNode, connectedNodes);
        return isValidMine;
      } else {
        connectedNodes.remove(nextNodeId);
      }
    }

    return true;
  }

  private Boolean nextNodeTraversalDFS(String currentNodeId, String acceptorId, Map<String, Boolean> visitedNode,
      List<NodesConfiguration> nodesConfigurationList, List<String> connectedNodes,Boolean isDestinationNode, TransactionValidationRequest transactionValidationRequest)
      throws Exception {

    NodesConfiguration currentNode = getSpecificNodeConfig(currentNodeId, nodesConfigurationList);
    visitedNode.put(currentNodeId, Boolean.TRUE);
    System.out.println("DFS : "+currentNodeId);
    TransactionValidationResponse transactionValidationResponse =
        nodeIntegrationService.nodeTransactionValidation(transactionValidationRequest,
            currentNode.getPortNumber()).getBody();

    // transaction failed scenario
    if(!transactionValidationResponse.getIsValidTransaction()){
      return false;
    }

    isDestinationNode = acceptorId.contentEquals(currentNodeId);
    if(isDestinationNode && transactionValidationResponse.getIsValidTransaction() &&
        transactionValidationResponse.getIsDestination()){
      return true;
    }

    connectedNodes.addAll(currentNode.getAuthority());

    while (!ObjectUtils.isEmpty(connectedNodes)) {
      String nextNodeId = connectedNodes.get(0);
      if (!visitedNode.containsKey(nextNodeId) || !visitedNode.get(nextNodeId)){
        boolean destinationNodeReached =
            nextNodeTraversalDFS(nextNodeId, acceptorId, visitedNode, nodesConfigurationList, connectedNodes, isDestinationNode, transactionValidationRequest);
        return destinationNodeReached;
      } else {
        connectedNodes.remove(nextNodeId);
      }
    }

    return false;
  }

  private NodesConfiguration getNodesConfiguration(String nodeName,
      List<NodesConfiguration> nodesConfigurationList) throws Exception {
    Optional<NodesConfiguration> nodesConfigurationOptional = nodesConfigurationList.stream().filter(
        nodeConfig -> nodeName.contentEquals(nodeConfig.getNodeName())).findFirst();
    if(ObjectUtils.isEmpty(nodesConfigurationOptional) ||
        ObjectUtils.isEmpty(nodesConfigurationOptional.get())) {
      throw new Exception("Invalid Node details");
    }
    NodesConfiguration nodesConfiguration = nodesConfigurationOptional.get();
    return nodesConfiguration;
  }

  private NodesConfiguration getSpecificNodeConfig(String initializerId, List<NodesConfiguration> nodesConfigurationList)
      throws Exception {
    Optional<NodesConfiguration> nodesConfigurationOptional = nodesConfigurationList.stream().filter(
        nodeConfig -> initializerId.contentEquals(nodeConfig.getNodeId())).findFirst();

    if(ObjectUtils.isEmpty(nodesConfigurationOptional) ||
        ObjectUtils.isEmpty(nodesConfigurationOptional.get())) {
      throw new Exception("Invalid Node details");
    } else {
      return nodesConfigurationOptional.get();
    }
  }

  private List<NodesConfiguration> fetchConfigurationList() {
    var objectMapper = new ObjectMapper();
    List<NodesConfiguration> nodesConfigurationList =
        jsonReader.jsonToObject("/NodesConfiguration.json", ArrayList.class);
    nodesConfigurationList = objectMapper.convertValue(nodesConfigurationList,
        new TypeReference<List<NodesConfiguration>>() {
        });
    return nodesConfigurationList;
  }

}
