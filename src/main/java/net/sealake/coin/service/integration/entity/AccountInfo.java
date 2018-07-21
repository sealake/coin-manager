package net.sealake.coin.service.integration.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {
  private boolean canTrade;
  private boolean canWithdraw;
  private boolean canDeposit;
  private long    updateTime;
  private List<AssetInfo> assetInfos = new ArrayList<>();
}