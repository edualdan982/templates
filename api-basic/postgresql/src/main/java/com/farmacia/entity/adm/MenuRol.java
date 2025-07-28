package com.farmacia.entity.adm;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.farmacia.entity.GenericEntity;
import com.farmacia.entity.auth.Rol;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "ADM_MENU_ROL")
public class MenuRol extends GenericEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqidMenuRol")
  @SequenceGenerator(name = "seqidMenuRol", allocationSize = 1, sequenceName = "ADM_ID_MENU_ROL_SEQ")
  private Integer idMenuRol;

  @JsonIgnoreProperties(value = { "rol", "hibernateLazyInitializer", "handler" })
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "idRol", foreignKey = @ForeignKey(name = "ADM_MENU_ROL_ID_ROL_FK"))
  private Rol rol;

  @JsonIgnoreProperties(value = { "menu", "hibernateLazyInitializer", "handler" })
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "idMenu", foreignKey = @ForeignKey(name = "ADM_MENU_ROL_ID_MENU_FK"))
  private Menu menu;
  

  public MenuRol() {
    super();
  }

  @PrePersist
  public void prePersist() {
    super.initial();
  }

  public Integer getIdMenuRol() {
    return idMenuRol;
  }

  public void setIdMenuRol(Integer idMenuRol) {
    this.idMenuRol = idMenuRol;
  }

  public Rol getRol() {
    return rol;
  }

  public void setRol(Rol rol) {
    this.rol = rol;
  }

  public Menu getMenu() {
    return menu;
  }

  public void setMenu(Menu menu) {
    this.menu = menu;
  }
}
