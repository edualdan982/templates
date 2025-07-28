package com.firmas.service.util;

import java.util.Optional;

public interface IClaveValServiceUtil {
  public Optional<String> validarClave(String clave, Byte grado);

  public String generarClave();
}
