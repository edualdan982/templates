package edual.auth.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public abstract class GenericEntity {

  @Column(columnDefinition = "number(1,0) default 0")
  private Boolean eliminado;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(columnDefinition = "timestamp(6) default current_timestamp")
  private Date fechaRegistro;

  protected GenericEntity() {
    this.initial();
  }

  protected void initial() {
    this.eliminado = false;
    this.fechaRegistro = new Date();
  }

  public Boolean getEliminado() {
    return eliminado;
  }

  public void setEliminado(Boolean eliminado) {
    this.eliminado = eliminado;
  }

  public Date getFechaRegistro() {
    return fechaRegistro;
  }

  public void setFechaRegistro(Date fechaRegistro) {
    this.fechaRegistro = fechaRegistro;
  }
}
