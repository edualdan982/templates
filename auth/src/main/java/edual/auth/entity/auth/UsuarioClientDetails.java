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

import edual.auth.entity.GenericEntity;
import edual.auth.entity.auth.id.IdUSuarioClientDetails;
import edual.auth.entity.auth.oauth.ClientDetail;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "auth_usuario_client_details")
public class UsuarioClientDetails extends GenericEntity {

  @EmbeddedId
  @NotNull
  private IdUSuarioClientDetails id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "idUsuario", foreignKey = @ForeignKey(name = "AUTH_ID_USUARIO_CLIENTS_DETAILS_FK"))
  @MapsId("idUsuario")
  @JsonBackReference
  private Usuario usuario;

  @JsonIgnoreProperties(value = { "sistema", "hibernateLazyInitializer", "handler" }, allowSetters = true)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "clientId", foreignKey = @ForeignKey(name = "AUTH_CLIENT_ID_USUARIO_FK"))
  @MapsId("clientId")
  private ClientDetail sistema;

  @Temporal(TemporalType.TIMESTAMP)
  private Date fechaVencimiento;

  public UsuarioClientDetails() {
    super();
  }

  @PrePersist
  public void prePersist() {
    super.initial();
  }

  public IdUSuarioClientDetails getId() {
    return id;
  }

  public void setId(IdUSuarioClientDetails id) {
    this.id = id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public ClientDetail getSistema() {
    return sistema;
  }

  public void setSistema(ClientDetail sistema) {
    this.sistema = sistema;
  }

  public Date getFechaVencimiento() {
    return fechaVencimiento;
  }

  public void setFechaVencimiento(Date fechaVencimiento) {
    this.fechaVencimiento = fechaVencimiento;
  }

}
