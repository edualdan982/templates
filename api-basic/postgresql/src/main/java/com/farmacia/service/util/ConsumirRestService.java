package com.farmacia.service.util;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class ConsumirRestService implements IConsumirRestService {
  private static final Logger log = LoggerFactory.getLogger(ConsumirRestService.class);

  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public ResponseEntity<ObjectNode> consumirRest(String url, HttpHeaders headers) {
    log.info("Servicio consumirRest. URL {}", url);
    ResponseEntity<ObjectNode> respuesta = null;
    if (headers == null) {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    }
    HttpEntity<ObjectNode> httpEntity = new HttpEntity<>(null, headers);
    try {
      respuesta = restTemplate.exchange(url, HttpMethod.GET, httpEntity,
          new ParameterizedTypeReference<ObjectNode>() {
          });
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      log.error("Error en la url: {}, Resp: {}", url, e.getResponseBodyAsString());
      try {
        JsonNode obj = this.objectMapper.readTree(e.getResponseBodyAsString());
        respuesta = ResponseEntity.status(e.getStatusCode()).body((ObjectNode) obj);
      } catch (Exception e1) {
        log.error("No se pudo convertir la respuesta en RepsonseEntity. Detalle {}",
            (e.getLocalizedMessage() == null ? "Sin detalle." : e.getLocalizedMessage()));
      }
    }
    return respuesta;
  }

  @Override
  public ResponseEntity<ObjectNode> consumirRest(String url, HttpMethod metodo, HttpHeaders headers,
      ObjectNode body) {
    log.info("Servicio consumirRest. URL {}", url);

    ResponseEntity<ObjectNode> respuesta = null;
    // Creamos y agregamos las cabeceras al request
    if (headers == null) {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    }

    HttpEntity<ObjectNode> httpEntity = new HttpEntity<>(body, headers);
    try {
      respuesta = restTemplate.exchange(url, metodo, httpEntity,
          new ParameterizedTypeReference<ObjectNode>() {
          });
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      log.error("Error en la url: {}, Resp: {}", url, e.getResponseBodyAsString());

      try {
        JsonNode obj = this.objectMapper.createObjectNode();
        if (e.getResponseBodyAsString() instanceof String)
          ((ObjectNode) obj).put("respuesta", e.getResponseBodyAsString());
        else
          obj = this.objectMapper.readTree(e.getResponseBodyAsString());
        respuesta = ResponseEntity.status(e.getStatusCode()).body((ObjectNode) obj);
      } catch (Exception e1) {
        log.error("No se pudo convertir la respuesta en RepsonseEntity. Detalle {}",
            (e.getLocalizedMessage() == null ? "Sin detalle." : e.getLocalizedMessage()));
      }
    } catch (RestClientException soc) {
      log.error("Error rest-client: {}", soc.getLocalizedMessage());
    }
    if (respuesta == null)
      respuesta = ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(
          this.objectMapper.createObjectNode().put("mensaje",
              String.format("No se recibio repuesta del servicio: %s metodo: %s", url, metodo.name())));

    return respuesta;
  }

  @Override
  public ResponseEntity<ObjectNode> consumirRest(String url, HttpMethod metodo, HttpHeaders headers,
      Object object) {
    log.info("Servicio consumirRest. URL {}", url);

    ResponseEntity<ObjectNode> respuesta = null;
    // Creamos y agregamos las cabeceras al request
    if (headers == null) {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    }

    HttpEntity<Object> httpEntity = new HttpEntity<>(object, headers);
    try {
      respuesta = restTemplate.exchange(url, metodo, httpEntity,
          new ParameterizedTypeReference<ObjectNode>() {
          });
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      log.error("Error en la url: {}, Resp: {}", url, e.getResponseBodyAsString());

      try {
        JsonNode obj = this.objectMapper.createObjectNode();
        if (e.getResponseBodyAsString() instanceof String)
          ((ObjectNode) obj).put("respuesta", e.getResponseBodyAsString());
        else
          obj = this.objectMapper.readTree(e.getResponseBodyAsString());
        respuesta = ResponseEntity.status(e.getStatusCode()).body((ObjectNode) obj);
      } catch (Exception e1) {
        log.error("No se pudo convertir la respuesta en RepsonseEntity. Detalle {}",
            (e.getLocalizedMessage() == null ? "Sin detalle." : e.getLocalizedMessage()));
      }
    } catch (RestClientException soc) {
      log.error("Error rest-client: {}", soc.getLocalizedMessage());
    }
    if (respuesta == null)
      respuesta = ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(
          this.objectMapper.createObjectNode().put("mensaje",
              String.format("No se recibio repuesta del servicio: %s metodo: %s", url, metodo.name())));

    return respuesta;
  }


}
