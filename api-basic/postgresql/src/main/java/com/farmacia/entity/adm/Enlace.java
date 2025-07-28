package com.farmacia.entity.adm;

import javax.persistence.Column;
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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import com.farmacia.entity.GenericEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "ADM_ENLACE")
public class Enlace extends GenericEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqIdEnlace")
	@SequenceGenerator(name = "seqIdEnlace", allocationSize = 1, sequenceName = "ADM_ID_ENLACE_SEQ")
	private Integer idEnlace;

	@NotEmpty
	@Size(min = 5, max = 256)
	@Column(length = 256)
	private String descripcion;

	@NotEmpty
	@Size(min = 3, max = 50)
	@Column(length = 50)
	private String abreviatura;

	@NotEmpty
	@Size(min = 3, max = 512)
	@Column(length = 512)
	private String ruta;

	@Size(max = 256)
	@Column(length = 256)
	private String imagen;

	@NotNull
	@PositiveOrZero
	private Integer orden;

	@Column(columnDefinition = "boolean default false")
	private Boolean estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idMenu", foreignKey = @ForeignKey(name = "ADM_ENLACE_ID_MENU_FK"))
	@JsonBackReference
	private Menu menu;

	@PrePersist
	public void prePersist() {
		super.initial();
		this.estado = true;
	}

	public Enlace() {
		super();
		this.estado = true;
	}

	public Integer getIdEnlace() {
		return idEnlace;
	}

	public void setIdEnlace(Integer idEnlace) {
		this.idEnlace = idEnlace;
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

	public String getRuta() {
		return ruta;
	}

	public void setRuta(String ruta) {
		this.ruta = ruta;
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

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}


}
