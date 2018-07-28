package net.sealake.coin.service.integration.cryptopia.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户发起SubmitTrade请求之后的返回值
 */
@Data
@NoArgsConstructor
public class CryptopiaTrade {
  private Long orderId;
  private List<Long> filledOrders;

  public static CryptopiaTrade parse(String responseStr) {
    final CryptopiaResponse<CryptopiaTrade> response = new CryptopiaResponse<>();

    final JsonElement jElement = new JsonParser().parse(responseStr);
    final JsonObject rootObject = jElement.getAsJsonObject();

    response.setSuccess(rootObject.get("Success").getAsBoolean());
    response.setMessage(rootObject.get("Error").toString());
    response.validate();

    final CryptopiaTrade trade = new CryptopiaTrade();
    final JsonObject data = rootObject.get("Data").getAsJsonObject();

    JsonElement orderIdElem = data.get("OrderId");
    if (!orderIdElem.isJsonNull()) {
      trade.setOrderId(orderIdElem.getAsLong());
    }

    trade.setFilledOrders(new ArrayList<>());
    final JsonArray jsonArray = data.get("FilledOrders").getAsJsonArray();
    if (jsonArray.isJsonNull()) {
      return trade;
    }

    for (JsonElement element: jsonArray) {
      if (!element.isJsonNull()) {
        trade.getFilledOrders().add(element.getAsLong());
      }
    }

    return trade;
  }
}
