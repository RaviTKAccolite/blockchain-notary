package com.example.demo.model.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionValidationResponse {

  private Boolean isDestination;

  private Boolean isValidTransaction;
}
