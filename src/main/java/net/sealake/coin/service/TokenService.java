package net.sealake.coin.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;

import net.sealake.coin.configuration.Settings;
import net.sealake.coin.constants.AppError;
import net.sealake.coin.entity.GenericUser;
import net.sealake.coin.exception.InternalServerErrorException;
import net.sealake.coin.exception.UnauthorizedException;
import net.sealake.coin.repository.GenericUserRepository;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class TokenService {
  @Autowired
  private Settings settings;

  @Autowired
  private GenericUserRepository userRepository;

  Algorithm algorithm;
  JWTVerifier verifier;

  @PostConstruct
  public void init() {
    algorithm = Algorithm.HMAC256(settings.getTokenSecret().getBytes());
    verifier = JWT.require(algorithm)
        .withIssuer(settings.getTokenIssuer())
        .build();
  }

  public String generateToken(GenericUser user) {
    String token = null;
    try {
      final DateTime curTime = DateTime.now();
      token = JWT.create()
          .withIssuer(settings.getTokenIssuer())
          .withIssuedAt(curTime.toDate())
          .withExpiresAt(curTime.plusSeconds(settings.getTokenExpireSeconds()).toDate())
          .withSubject(String.valueOf(user.getId()))
          .sign(algorithm);
    } catch (JWTCreationException ex) {
      log.error("failed parse token: {}, exception: {}", token, ex);
      throw new InternalServerErrorException(AppError.OTHER_SERVER_INERNAL_EXCEPTION);
    }

    return token;
  }

  public GenericUser parseToken(String token) {
    GenericUser user = null;
    try {
      DecodedJWT jwt = verifier.verify(token);
      final Long userId = Long.parseLong(jwt.getSubject());
      user = userRepository.findOne(userId);
    } catch (JWTVerificationException ex) {
      log.error("failed parse token: {}, exception: {}", token, ex);
      throw new UnauthorizedException(AppError.AUTHORIZE_TOKEN_INVALID);
    }

    return user;
  }
}
