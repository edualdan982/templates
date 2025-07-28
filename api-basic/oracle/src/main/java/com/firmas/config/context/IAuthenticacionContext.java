package com.firmas.config.context;

import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface IAuthenticacionContext {

  Authentication getContextAuthentication();

  String decodeBase64(String encodedString);

  ObjectNode decodeToken(String token);
}
