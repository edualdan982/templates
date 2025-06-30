package edual.auth.entity.auth;

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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import edual.auth.entity.GenericEntity;

@Entity
@Table(name = "auth_persona", uniqueConstraints = {
    @UniqueConstraint(name = "AUTH_NRO_DOC_TIPO_UQ", columnNames = { "numeroDoc", "tipoDoc", "comDoc" }) })
public class Persona extends GenericEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqAuthIdPersonaSeq")
  @SequenceGenerator(name = "seqAuthIdPersonaSeq", allocationSize = 1, sequenceName = "AUTH_ID_PERSONA_SEQ")
  private Integer idPersona;

  @Size(max = 300)
  @Column(length = 300)
  private String apePaterno;

  @Size(max = 300)
  @Column(length = 300)
  private String apeMaterno;

  @Size(max = 250)
  @Column(length = 250)
  private String nombres;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  @Temporal(TemporalType.DATE)
  private Date fechaNac;

  @Enumerated(value = EnumType.STRING)
  @Column(length = 8)
  private TypeDoc tipoDoc;

  @Size(max = 128)
  @NotEmpty
  @Column(length = 128)
  private String numeroDoc;

  @Size(max = 4)
  @Column(length = 4)
  private String comDoc;

  public Persona() {
    super();
  }

  @PrePersist
  public void prePersist() {
    super.initial();
  }

  public Integer getIdPersona() {
    return idPersona;
  }

  public void setIdPersona(Integer idPersona) {
    this.idPersona = idPersona;
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

  public TypeDoc getTipoDoc() {
    return tipoDoc;
  }

  public void setTipoDoc(TypeDoc tipoDoc) {
    this.tipoDoc = tipoDoc;
  }

  public String getNumeroDoc() {
    return numeroDoc;
  }

  public void setNumeroDoc(String numeroDoc) {
    this.numeroDoc = numeroDoc;
  }

  public String getComDoc() {
    return comDoc;
  }

  public void setComDoc(String comDoc) {
    this.comDoc = comDoc;
  }

}
