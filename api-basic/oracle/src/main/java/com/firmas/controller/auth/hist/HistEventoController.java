package com.firmas.controller.auth.hist;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.firmas.config.constantes.ResponseKeys;
import com.firmas.entity.auth.hist.HistEvento;
import com.firmas.service.auth.hist.IHistEventoService;
import com.firmas.service.util.IJwtAuthServiceUtil;
import com.firmas.util.proceso.BuildMap;

@RestController
@RequestMapping("/hist-evento")
public class HistEventoController {
  private static final Logger log = LoggerFactory.getLogger(HistEventoController.class);

  @Autowired
  private IHistEventoService histEventoService;
  @Autowired
  private IJwtAuthServiceUtil jwtAuthServiceUtil;

  @GetMapping("/listar")
  @Secured({ "ROLE_ADMIN" })
  public ResponseEntity<Map<String, Object>> listar(
      @RequestHeader String authorization, @RequestParam(required = false, defaultValue = "0") Integer page,
      @RequestParam(required = false, defaultValue = "15") Integer size,
      @RequestParam(required = false) String username) {
    Map<String, Object> response = BuildMap.initMap();
    if (username == null) {
      response.put("mensaje", "El parametro 'usuario' es requerido");
      return ResponseEntity.badRequest().body(response);
    }

    Page<HistEvento> pageLista = histEventoService.listarPaginado(PageRequest.of(page, size), username);
    response.put(ResponseKeys.RESPUESTA.getKey(), pageLista);
    if (pageLista.getNumberOfElements() == 0) {
      response.put(ResponseKeys.ESTADO.getKey(), false);
      response.put(ResponseKeys.MENSAJE.getKey(), "Se han encontrado 0 registros");
    } else {
      response.put(ResponseKeys.ESTADO.getKey(), true);
      response.put(ResponseKeys.MENSAJE.getKey(),
          "Se han encontrado " + pageLista.getNumberOfElements() + " registros");
    }

    return ResponseEntity.ok(response);
  }

  @GetMapping("/listar-sesiones")
  public ResponseEntity<Map<String, Object>> listarPaginado(
      @RequestHeader String authorization, @RequestParam(required = false, defaultValue = "0") Integer page,
      @RequestParam(required = false, defaultValue = "15") Integer size) {
    Map<String, Object> response = BuildMap.initMap();
    Map<String, Object> dataJwt = new HashMap<>();
    try {
      dataJwt = jwtAuthServiceUtil.decodeJwt(authorization);
      if (dataJwt.isEmpty()) {
        response.put(ResponseKeys.ESTADO.getKey(), false);
        response.put(ResponseKeys.MENSAJE.getKey(), "No se ha podido leer el token o el token es invalido");
        return ResponseEntity.badRequest().body(response);
      }
    } catch (Exception e) {
      log.error("No se ha podido leer los datos del token. Detalle: {}",
          (e.getLocalizedMessage() == null ? "Sin detalle" : e.getLocalizedMessage()));
    }

    Page<HistEvento> pageLista = histEventoService.listarPaginado(PageRequest.of(page, size),
        dataJwt.get("user_name").toString());
    response.put(ResponseKeys.RESPUESTA.getKey(), pageLista);
    if (pageLista.getNumberOfElements() == 0) {
      response.put(ResponseKeys.ESTADO.getKey(), false);
      response.put(ResponseKeys.MENSAJE.getKey(), "Se han encontrado 0 registros");
    } else {
      response.put(ResponseKeys.ESTADO.getKey(), true);
      response.put(ResponseKeys.MENSAJE.getKey(),
          "Se han encontrado " + pageLista.getNumberOfElements() + " registros");
    }

    return ResponseEntity.ok(response);
  }
}
