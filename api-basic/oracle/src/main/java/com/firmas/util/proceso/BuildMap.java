package com.firmas.util.proceso;

import java.util.HashMap;
import java.util.Map;

import com.firmas.config.constantes.ResponseKeys;

public class BuildMap {
  private BuildMap() {
  }

  public static Map<String, Object> initMap() {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);
    response.put(ResponseKeys.RESPUESTA.getKey(), null);
    response.put(ResponseKeys.MENSAJE.getKey(), null);
    return response;
  }

  public static Map<String, Object> initMapBadReq() {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);
    response.put(ResponseKeys.RESPUESTA.getKey(), null);
    response.put(ResponseKeys.MENSAJE.getKey(), null);
    response.put(ResponseKeys.ERRORS.getKey(), null);
    return response;
  }

  public static Map<String, Object> initMapReg() {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);
    response.put(ResponseKeys.REGISTRO.getKey(), null);
    response.put(ResponseKeys.MENSAJE.getKey(), null);
    response.put(ResponseKeys.ERRORS.getKey(), null);
    return response;
  }

}