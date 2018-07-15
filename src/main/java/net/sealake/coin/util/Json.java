package net.sealake.coin.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.constants.AppError;
import net.sealake.coin.exception.InternalServerErrorException;

@Slf4j
public class Json {

  private static ObjectMapper mapper = new ObjectMapper();

  public static ObjectMapper getMapper() {
    return mapper;
  }

  /**
   * json序列化
   */
  public static String dumps(Object obj) {
    try {
      return mapper.writeValueAsString(obj);
    } catch (JsonProcessingException ex) {
      log.error("failed serialize object to json string: {}", ex);
      throw new InternalServerErrorException(AppError.OTHER_SERVER_INERNAL_EXCEPTION);
    }
  }
}