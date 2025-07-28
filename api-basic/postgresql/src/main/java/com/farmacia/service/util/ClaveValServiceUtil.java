package com.farmacia.service.util;

import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.farmacia.util.validacion.ValidadorRegExp;

@Service
public class ClaveValServiceUtil implements IClaveValServiceUtil {

  private Random rand = new Random();

  @Value("${aplicacion.pass.ldap}")
  private String PASS_INST;

  @Override
  public Optional<String> validarClave(String clave, Byte grado) {
    Matcher matcher = null;
    String msg = null;
    if(grado == null) 
      grado = 0;
    

    switch (grado) {
      case -1:
        matcher = Pattern.compile(String.format("^%s$", PASS_INST)).matcher(clave);
        break;
      case 0:
        matcher = Pattern.compile(ValidadorRegExp.LETRAS_NUMEROS).matcher(clave);
        msg = ValidadorRegExp.MSG_BAJA;
        break;
      case 1:
        matcher = Pattern.compile(ValidadorRegExp.MINUS_MAYUS_NUM).matcher(clave);
        msg = ValidadorRegExp.MSG_MEDIA;
        break;
      case 2:
        matcher = Pattern.compile(ValidadorRegExp.MINUS_MAYUS_NUM_CESP).matcher(clave);
        msg = ValidadorRegExp.MSG_MAXIMA;
        break;
      default:
        matcher = Pattern.compile(ValidadorRegExp.LETRAS_NUMEROS).matcher(clave);
        msg = ValidadorRegExp.MSG_BAJA;
    }
    if (!matcher.matches())
      return Optional.of(msg);
    else
      return Optional.empty();
  }

  @Override
  public String generarClave() {
    StringBuilder strBluid = new StringBuilder();

    int indice = 0;
    for (int i = 1; i <= 10; i++) {
      indice = (rand.nextInt((122 - 65) + 1)) + 65;
      if (indice >= 91 && indice <= 96) {
        indice = indice + (rand.nextDouble() > 0.5 ? +6 : -6);
      }
      strBluid.append((char) indice);
    }
    return strBluid.toString();
  }
}
