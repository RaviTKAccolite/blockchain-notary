package com.example.demo.model.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionValidationRequest {
  @NonNull
  private String initializerId;

  @NonNull
  private String acceptorId;

  private String message;

}
