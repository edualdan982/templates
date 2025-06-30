package edual.auth.entity.auth;

import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import edual.auth.entity.GenericEntity;
import edual.auth.entity.auth.id.IdUsuarioRol;

@Entity
@Table(name = "auth_usuario_rol")
public class UsuarioRol extends GenericEntity {

  @EmbeddedId
  @NotNull
  private IdUsuarioRol id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "idUsuario", foreignKey = @ForeignKey(name = "AUTH_ID_USUARIO_ROL_FK"))
  @JsonBackReference
  @MapsId("idUsuario")
  private Usuario usuario;

  @JsonIgnoreProperties(value = { "rol", "hibernateLazyInitializer", "handler" }, allowSetters = true)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "idRol", foreignKey = @ForeignKey(name = "AUTH_ID_ROL_USUARIO_FK"))
  @MapsId("idRol")
  private Rol rol;

  @Temporal(TemporalType.TIMESTAMP)
  private Date fechaVencimiento;

  @PrePersist
  public void prePersist() {
    super.initial();
  }

  public UsuarioRol() {
    super();
  }

  public UsuarioRol(Integer idUsuario, Integer idRol, Date fechaVencimiento) {
    super();
    this.id = new IdUsuarioRol(idUsuario, idRol);
    this.fechaVencimiento = fechaVencimiento;
  }

  public UsuarioRol(Usuario usuario, Rol rol) {
    this.usuario = usuario;
    this.rol = rol;
  }

  public IdUsuarioRol getId() {
    return id;
  }

  public void setId(IdUsuarioRol id) {
    this.id = id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public Rol getRol() {
    return rol;
  }

  public void setRol(Rol rol) {
    this.rol = rol;
  }

  public Date getFechaVencimiento() {
    return fechaVencimiento;
  }

  public void setFechaVencimiento(Date fechaVencimiento) {
    this.fechaVencimiento = fechaVencimiento;
  }
}
