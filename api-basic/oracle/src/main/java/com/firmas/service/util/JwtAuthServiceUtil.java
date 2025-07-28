package com.firmas.service.util;

import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firmas.config.context.IAuthenticacionContext;
import com.firmas.util.exception.JwtDecodeException;

@Service
@Transactional(readOnly = true)
public class JwtAuthServiceUtil implements IJwtAuthServiceUtil {
  private static final Logger log = LoggerFactory.getLogger(JwtAuthServiceUtil.class);

  @Autowired
  private IAuthenticacionContext authenticationContext;

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Object> decodeJwt(String jwt) throws JwtDecodeException, JsonProcessingException {
    String[] jwtSplit = jwt.split("\\.");
    if (jwtSplit.length != 3)
      throw new JwtDecodeException("El token enviado es invalido");

    Base64.Decoder decoder = Base64.getUrlDecoder();
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(new String(decoder.decode(jwtSplit[1])), HashMap.class);
  }

  @Override
  public Map<String, Object> getJwtContextAuth() {

    OAuth2AuthenticationDetails clientDetails = this.getOAuth2ClientDetails();
    if (clientDetails != null) {
      try {
        return this.decodeJwt(clientDetails.getTokenValue());
      } catch (Exception e) {
        log.error("No se pudo completar la conversion");

      }
    }
    return Collections.emptyMap();
  }

  @Override
  public Authentication getAuthentication() {
    return authenticationContext.getContextAuthentication();
  }

  @Override
  public OAuth2Authentication getOAuth2Authtenticacion() {
    Object obj = authenticationContext.getContextAuthentication();
    if (obj instanceof OAuth2Authentication)
      return (OAuth2Authentication) obj;
    return null;
  }

  @Override
  public List<GrantedAuthority> getAuthorithies() {
    User user = this.getOAuth2User();
    if (user != null)
      return (List<GrantedAuthority>) user.getAuthorities();
    return Collections.emptyList();
  }

  @Override
  public OAuth2Request getOAuth2Request() {
    OAuth2Authentication oauth = this.getOAuth2Authtenticacion();
    if (oauth != null)
      return oauth.getOAuth2Request();
    return null;
  }

  @Override
  public OAuth2AuthenticationDetails getOAuth2ClientDetails() {
    Object obj = authenticationContext.getContextAuthentication().getDetails();
    if (obj instanceof OAuth2AuthenticationDetails)
      return (OAuth2AuthenticationDetails) obj;
    return null;
  }

  @Override
  public User getOAuth2User() {
    Object obj = authenticationContext.getContextAuthentication().getPrincipal();
    if (obj instanceof User)
      return (User) obj;
    return null;
  }

  @Override
  public GrantedAuthority buscarRole(Collection<? extends GrantedAuthority> authorities,
      Predicate<GrantedAuthority> predicate) {
    @SuppressWarnings("unchecked")
    Optional<GrantedAuthority> res = (Optional<GrantedAuthority>) authorities.stream().filter(predicate)
        .findFirst();
    return res.orElse(null);
  }

  @Override
  public GrantedAuthority verificarRolExterno(SecurityContext securityContext, Integer idTipoPago) {
    GrantedAuthority role = null;
    switch (idTipoPago) {
      case 1:
        role = this.buscarRole(securityContext.getAuthentication().getAuthorities(),
            a -> a.getAuthority().equals("ROLE_INTERNO"));
        break;
      case 2:
        role = this.buscarRole(securityContext.getAuthentication().getAuthorities(),
            a -> a.getAuthority().equals("ROLE_GENERICO") || a.getAuthority().equals("ROLE_MATRICULA"));
        break;
      case 3:
        role = this.buscarRole(securityContext.getAuthentication().getAuthorities(),
            a -> a.getAuthority().equals("ROLE_MATRICULA"));
        break;
      case 4:
        role = this.buscarRole(securityContext.getAuthentication().getAuthorities(),
            a -> a.getAuthority().equals("ROLE_POST_GRADO"));
        break;
      default:
        break;
    }
    return role;
  }

}
