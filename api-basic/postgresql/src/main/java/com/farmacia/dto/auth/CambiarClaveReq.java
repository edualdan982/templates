package com.farmacia.dto.auth;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CambiarClaveReq {

  @NotNull
  private Integer idUsuario;

  @NotEmpty
  @Size(min = 6, max = 64)
  private String claveActual;

  @NotEmpty
  @Size(min = 6, max = 64)
  private String nuevaClave;

  @NotEmpty
  @Size(min = 6, max = 64)
  private String confirmarClave;

  public String getClaveActual() {
    return claveActual;
  }

  public void setClaveActual(String claveActual) {
    this.claveActual = claveActual;
  }

  public String getNuevaClave() {
    return nuevaClave;
  }

  public void setNuevaClave(String nuevaClave) {
    this.nuevaClave = nuevaClave;
  }

  public String getConfirmarClave() {
    return confirmarClave;
  }

  public void setConfirmarClave(String confirmarClave) {
    this.confirmarClave = confirmarClave;
  }

  public Integer getIdUsuario() {
    return idUsuario;
  }

  public void setIdUsuario(Integer idUsuario) {
    this.idUsuario = idUsuario;
  }

}
