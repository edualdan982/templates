package edual.auth.entity.auth.hist;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import edual.auth.entity.auth.TypeAccount;
import edual.auth.util.CadenaUtil;

@Entity
@Table(name = "auth_hist_evento")
public class HistEvento {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqIdHistEventoAuth")
  @SequenceGenerator(name = "seqIdHistEventoAuth", allocationSize = 1, sequenceName = "AUTH_HIST_ID_HIST_EVENTO_SEQ")
  private Integer idHistEvento;

  @Column(length = 64)
  @Enumerated(value = EnumType.STRING)
  private TypeEstadoLogin estado;

  @Column(length = 256)
  private String ip;

  @Column(length = 256)
  private String ipServer;

  @Size(max = 1024)
  @Column(length = 1024)
  private String passKey;

  @Column(length = 4096)
  private String mensaje;

  @Enumerated(value = EnumType.STRING)
  private TypeAccount tipoCuenta;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(columnDefinition = "timestamp(6) default current_timestamp")
  private Date fechaRegistro;

  private String username;

  private Integer idUsuario;

  @Column(columnDefinition = "varchar2(24) default 'LOGIN'")
  @Enumerated(value = EnumType.STRING)
  private TypeOperacionAuth operacion;

  public HistEvento() {
    this.inital();
  }

  public HistEvento(String ip, String ipServer, String passKey, TypeAccount tipoCuenta) {
    this.ip = ip;
    this.ipServer = ipServer;
    this.passKey = CadenaUtil.maskString(passKey, 3, 3);
    this.tipoCuenta = tipoCuenta;
  }

  public HistEvento(TypeEstadoLogin estado, String ip, String ipServer, String passKey, String mensaje,
      TypeAccount tipoCuenta) {
    this.estado = estado;
    this.ip = ip;
    this.ipServer = ipServer;
    this.passKey = passKey;
    this.mensaje = mensaje;
    this.tipoCuenta = tipoCuenta;
  }

  public HistEvento(String ip, String ipServer, String passKey, TypeOperacionAuth operacion, String mensaje) {
    this.ip = ip;
    this.ipServer = ipServer;
    this.passKey = passKey;
    this.operacion = operacion;
    this.mensaje = mensaje;
    this.estado = TypeEstadoLogin.SUCCESS;
  }

  public void inital() {
    this.fechaRegistro = new Date();
    if (this.operacion == null)
      this.operacion = TypeOperacionAuth.LOGIN;
  }

  @PrePersist
  public void prePersist() {
    this.inital();
  }

  public Integer getIdHistEvento() {
    return idHistEvento;
  }

  public void setIdHistEvento(Integer idHistEvento) {
    this.idHistEvento = idHistEvento;
  }

  public TypeEstadoLogin getEstado() {
    return estado;
  }

  public void setEstado(TypeEstadoLogin estado) {
    this.estado = estado;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getPassKey() {
    return passKey;
  }

  public void setPassKey(String passKey) {
    this.passKey = passKey;
  }

  public String getMensaje() {
    return mensaje;
  }

  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }

  public String getIpServer() {
    return ipServer;
  }

  public void setIpServer(String ipServer) {
    this.ipServer = ipServer;
  }

  public Date getFechaRegistro() {
    return fechaRegistro;
  }

  public void setFechaRegistro(Date fechaRegistro) {
    this.fechaRegistro = fechaRegistro;
  }

  public TypeAccount getTipoCuenta() {
    return tipoCuenta;
  }

  public void setTipoCuenta(TypeAccount tipoCuenta) {
    this.tipoCuenta = tipoCuenta;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Integer getIdUsuario() {
    return idUsuario;
  }

  public void setIdUsuario(Integer idUsuario) {
    this.idUsuario = idUsuario;
  }

  public TypeOperacionAuth getOperacion() {
    return operacion;
  }

  public void setOperacion(TypeOperacionAuth operacion) {
    this.operacion = operacion;
  }

}
