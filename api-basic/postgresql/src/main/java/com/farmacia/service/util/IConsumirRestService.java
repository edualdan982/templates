package com.farmacia.service.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface IConsumirRestService {

  /**
   * Este metodo solo usar para GET
   * 
   * @param url
   * @param headers
   * @return
   */
  ResponseEntity<ObjectNode> consumirRest(String baseUrl, HttpHeaders headers);

  ResponseEntity<ObjectNode> consumirRest(String baseUrl, HttpMethod metodo, HttpHeaders headers, ObjectNode body);

  ResponseEntity<ObjectNode> consumirRest(String baseUrl, HttpMethod metodo, HttpHeaders headers, Object object);

}
