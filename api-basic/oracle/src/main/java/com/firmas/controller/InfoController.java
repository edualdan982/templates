package com.firmas.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.firmas.config.constantes.ResponseKeys;
import com.firmas.util.proceso.Metodos;

@RestController
public class InfoController {

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Value("${spring.profiles.active}")
  private String ENTORNO;
  @Value("${server.servlet.context-path}")
  private String context_api;

  @GetMapping("/estado")
  public ResponseEntity<Map<String, Object>> env() {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.MENSAJE.getKey(), "Servicio backend farmacia en línea.");
    response.put("entorno", ENTORNO);
    response.put("fechaHora", new Date());
    response.put("context_api", context_api);
    response.put("estado", Boolean.TRUE);
    response.put("periodo", Metodos.getPeriodo());
    try {
      InetAddress ip = InetAddress.getLocalHost();
      response.put("ip-server", ip.getHostAddress());
    } catch (UnknownHostException e) {
      response.put("ip-server", "No se pudo obtener la IP local del servidor.");
    }

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @GetMapping("/generar")
  public ResponseEntity<Map<String, Object>> encriptarClave(@RequestParam String clave) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), Boolean.TRUE);
    response.put(ResponseKeys.MENSAJE.getKey(), "Clave encriptadao con ".concat(passwordEncoder.getClass().toString()));
    response.put("clave", passwordEncoder.encode(clave));

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/prueba")
  public ResponseEntity<Map<String, Object>> pruebas() {
    Map<String, Object> response = new HashMap<>();

    response.put(ResponseKeys.ESTADO.getKey(), Boolean.TRUE);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
