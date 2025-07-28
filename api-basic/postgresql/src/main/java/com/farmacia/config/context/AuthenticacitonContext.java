package com.farmacia.config.context;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class AuthenticacitonContext implements IAuthenticacionContext {
  private static final Logger log = LoggerFactory.getLogger(AuthenticacitonContext.class);

  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public Authentication getContextAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  public String decodeBase64(String encodedString) {
    byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
    return new String(decodedBytes);
}

  @Override
  public ObjectNode decodeToken(String token) {
    String tokenValue = token.replace("Bearer", "").trim();
    String[] parts = tokenValue.split("\\.");
    if(parts.length != 3) {
      log.error("Token invalido");
      return null;
    }
    ObjectNode objectNode = null;
    try {
      objectNode = (ObjectNode) objectMapper.readTree(decodeBase64(parts[1]));
    } catch (Exception e) {
      log.error("No se pudo convertir el token");
    }

    return objectNode;

  }

}
