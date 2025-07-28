package com.firmas.config.constantes;

public enum ResponseKeys {
  ESTADO("estado", "Estado de la del proceso true/false si se cumplio el proceso."),
  RESPUESTA("respuesta", "Respuesta del proceso."),
  REGISTRO("registro", "Registro del proceso."),
  ERRORS("errors", "Errores del proceso."),
  MENSAJE("mensaje", "Mensaje del proceso."),
  ERROR("error", "Error del proceso."),
  CODIGO("codigo", "Indentificador de la orden pago.");

  private String key;

  private String descripcion;

  private ResponseKeys(String key, String descripcion) {
    this.key = key;
    this.descripcion = descripcion;
  }

  public String getKey() {
    return key;
  }

  public String getDescripcion() {
    return descripcion;
  }
}
