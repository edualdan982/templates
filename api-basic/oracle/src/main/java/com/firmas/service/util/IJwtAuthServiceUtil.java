package com.firmas.service.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.firmas.util.exception.JwtDecodeException;

public interface IJwtAuthServiceUtil {

  Map<String, Object> decodeJwt(String jwt) throws JwtDecodeException, JsonProcessingException;

  Map<String, Object> getJwtContextAuth();

  Authentication getAuthentication();

  OAuth2Authentication getOAuth2Authtenticacion();

  List<GrantedAuthority> getAuthorithies();

  OAuth2Request getOAuth2Request();

  OAuth2AuthenticationDetails getOAuth2ClientDetails();

  User getOAuth2User();

  public GrantedAuthority buscarRole(Collection<? extends GrantedAuthority> authorities,
      Predicate<GrantedAuthority> predicate);

  GrantedAuthority verificarRolExterno(SecurityContext securityContext, Integer idTipoPago);

}
