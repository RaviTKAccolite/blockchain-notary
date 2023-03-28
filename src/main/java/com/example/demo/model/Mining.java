package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Mining {

  private String silverMine;

  private String goldMine;

  private String diamondMine;

  private Long silverMineReward;

  private Long goldMineReward;

  private Long diamondMineReward;

  private String jwtHeader;

  private String jwtPayload;

  private String alphaNumericString;


}
