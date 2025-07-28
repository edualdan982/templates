package com.farmacia.config.constantes;

public enum ContexProperties {
  ID_USUARIO("idUsuario", "Indentificador del usuario"),
  USERNAME("username", "Nombre de usuario");

  private String key;

  private String descripcion;

  private ContexProperties(String key, String descripcion) {
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