package com.example.demo.service.impl;

import com.example.demo.entity.NodesConfiguration;
import com.example.demo.model.AuthorizationRequestBody;
import com.example.demo.service.NotaryService;
import com.example.demo.service.util.JsonReader;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class NotaryServiceImpl implements NotaryService {

  JsonReader jsonReader = new JsonReader();

  @Override
  public HttpHeaders getAuthorizationToken(AuthorizationRequestBody requestBody) throws Exception {
    HttpHeaders httpHeaders = new HttpHeaders();
    var objectMapper = new ObjectMapper();
    List<NodesConfiguration> nodesConfigurationList =
        jsonReader.jsonToObject("/NodesConfiguration.json", ArrayList.class);
    nodesConfigurationList = objectMapper.convertValue(nodesConfigurationList, new TypeReference<List<NodesConfiguration>>(){});

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


}
