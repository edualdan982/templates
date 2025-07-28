package com.farmacia.entity.adm;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.farmacia.entity.GenericEntity;

@Entity
@Table(name = "ADM_GESTION")
public class Gestion extends GenericEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqIdGestionAdm")
  @SequenceGenerator(name = "seqIdGestionAdm", allocationSize = 1, sequenceName = "ADM_ID_GESTION_SEQ")
  private Integer idGestion;

  @Column(columnDefinition = "int4 default extract(year from current_date)")
  private Integer gestion;

  @Column(columnDefinition = "boolean default false")
  private Boolean vigente;

  @Column(columnDefinition = "boolean default false")
  private Boolean cierre;

  @Temporal(TemporalType.TIMESTAMP)
  private Date fechaCierre;

  public Gestion() {
    super();
    vigente = false;
    cierre = false;
  }

  @PrePersist
  public void prePersist() {
    super.initial();
  }

  public Integer getIdGestion() {
    return idGestion;
  }

  public void setIdGestion(Integer idGestion) {
    this.idGestion = idGestion;
  }

  public Integer getGestion() {
    return gestion;
  }

  public void setGestion(Integer gestion) {
    this.gestion = gestion;
  }

  public Boolean getVigente() {
    return vigente;
  }

  public void setVigente(Boolean vigente) {
    this.vigente = vigente;
  }

  public Boolean getCierre() {
    return cierre;
  }

  public void setCierre(Boolean cierre) {
    this.cierre = cierre;
  }

  public Date getFechaCierre() {
    return fechaCierre;
  }

  public void setFechaCierre(Date fechaCierre) {
    this.fechaCierre = fechaCierre;
  }

}
