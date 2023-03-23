package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationRequestBody {

  @NonNull
  private String nodeName;

  @NonNull
  private String nodePassword;

}
