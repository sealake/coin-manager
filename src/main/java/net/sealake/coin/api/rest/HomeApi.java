package net.sealake.coin.api.rest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author melody
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/index")
public class HomeApi {

  @GetMapping
  public String index(@RequestParam final String name) {
    return "hello " + name;
  }
}