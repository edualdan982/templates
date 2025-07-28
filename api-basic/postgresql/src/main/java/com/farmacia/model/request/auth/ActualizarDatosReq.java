package com.farmacia.model.request.auth;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public class ActualizarDatosReq {
  private String apePaterno;
  private String apeMaterno;
  private String nombres;
  @Temporal(TemporalType.TIMESTAMP)
  private Date fechaNac;

  private String correo;
  private String telefono;

  public ActualizarDatosReq() {

  }

  public String getApePaterno() {
    return apePaterno;
  }

  public void setApePaterno(String apePaterno) {
    this.apePaterno = apePaterno;
  }

  public String getApeMaterno() {
    return apeMaterno;
  }

  public void setApeMaterno(String apeMaterno) {
    this.apeMaterno = apeMaterno;
  }

  public String getNombres() {
    return nombres;
  }

  public void setNombres(String nombres) {
    this.nombres = nombres;
  }

  public Date getFechaNac() {
    return fechaNac;
  }

  public void setFechaNac(Date fechaNac) {
    this.fechaNac = fechaNac;
  }

  public String getCorreo() {
    return correo;
  }

  public void setCorreo(String correo) {
    this.correo = correo;
  }

  public String getTelefono() {
    return telefono;
  }

  public void setTelefono(String telefono) {
    this.telefono = telefono;
  }

}
