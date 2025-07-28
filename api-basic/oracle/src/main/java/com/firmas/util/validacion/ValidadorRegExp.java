package com.firmas.util.validacion;

public class ValidadorRegExp {

  /**
   * Expresión regular clave en validación de grado MÁXIMA, donde se requiere
   * minúsculas(al menos una), mayúsculas(al menos una), números(al menos uno), un
   * carater especial($@$!%*?&) y de longitud 8 a 15 caracteres. No se aceptan
   * espacios en blanco.
   *
   */
  public static final String MINUS_MAYUS_NUM_CESP = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])([A-Za-z\\d$@$!%*?&]|[^ ]){8,15}$";

  public static final String MSG_MAXIMA = "La clave de cumplir con: minúsculas(al menos una), mayúsculas(al menos una), números(al menos uno), un caracter especial($@$!%*?&) y de longitud 8 a 15 caracteres. No se aceptan espacios en blanco";
  /**
   * Expresión regular clave en validación de grado MEDIA, donde se requiere
   * minúsculas(al menos una), mayúsculas(al menos una), números(al menos uno) y
   * de longitud 6 a 15 caracteres. No se aceptan espacios en blanco.
   *
   */
  public static final String MINUS_MAYUS_NUM = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)([A-Za-z\\d]|[^ ]){6,15}$";

  public static final String MSG_MEDIA = "La clave de cumplir con: minúsculas(al menos una), mayúsculas(al menos una), números(al menos uno) y de longitud 6 a 15 caracteres. No se aceptan espacios en blanco";
  /**
   * Expresión regular clave en validación de grado BAJA, donde se requiere
   * minúsculas(al menos una), mayúsculas(al menos una), números(al menos uno) y
   * de longitud 6 a 15 caracteres. No se aceptan espacios en blanco.
   *
   */
  public static final String LETRAS_NUMEROS = "^([A-Za-z\\d]|[^ ]){6,15}$";

  public static final String MSG_BAJA = "La clave de cumplir con: minúsculas(al menos una), mayúsculas(al menos una), números(al menos uno) y de longitud 6 a 15 caracteres. No se aceptan espacios en blanco";

  private ValidadorRegExp() {
  }
}
