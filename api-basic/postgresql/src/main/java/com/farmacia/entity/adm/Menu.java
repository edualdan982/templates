package com.farmacia.entity.adm;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import com.farmacia.entity.GenericEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "ADM_MENU")
public class Menu extends GenericEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqIdMenu")
	@SequenceGenerator(name = "seqIdMenu", allocationSize = 1, sequenceName = "ADM_ID_MENU_SEQ")
	private Integer idMenu;

	@NotEmpty
	@Size(min = 5, max = 200)
	@Column(length = 200)
	private String descripcion;

	@NotEmpty
	@Size(min = 3, max = 50)
	@Column(length = 50)
	private String abreviatura;

	@NotEmpty
	@Size(min = 3, max = 256)
	@Column(length = 256)
	private String imagen;

	@NotNull
	@PositiveOrZero
	private Integer orden;

	@Column(columnDefinition = "boolean default false")
	private Boolean estado;


	@JsonIgnoreProperties(value = { "enlaces", "hibernateLazyInitializer", "handler" })
	@OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<Enlace> enlaces;

	public Menu() {
		super();
		this.estado = true;
	}

	@PrePersist
	public void prePersit() {
		super.initial();
		this.estado = true;
	}

	public Integer getIdMenu() {
		return idMenu;
	}

	public void setIdMenu(Integer idMenu) {
		this.idMenu = idMenu;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getAbreviatura() {
		return abreviatura;
	}

	public void setAbreviatura(String abreviatura) {
		this.abreviatura = abreviatura;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	public Integer getOrden() {
		return orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}

}
