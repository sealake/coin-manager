package net.sealake.coin.service.integration.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinOrderRequest {

  private String symbol;

  private String price;

  private String quantity;
}