package com.farmacia.entity.dt;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.farmacia.config.constantes.Mensajes;

@Entity
@Table(name = "DT_CORREO_DATA", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"correo", "entorno"}, name = "DT_CORREO_DATA_ENV_UQ") })
public class CorreoData {

	@Id
	@NotEmpty(message = Mensajes.NO_NULO)
	@Size(max = 256)
	@Column(length = 256)
	private String correo;

	@NotEmpty(message = Mensajes.NO_NULO)
	@Size(max = 512)
	@Column(length = 512)
	private String claveAcceso;

	@NotEmpty(message = Mensajes.NO_NULO)
	@Size(max = 512)
	@Column(length = 512)
	private String keyAcceso;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(columnDefinition = "timestamp default current_timestamp")
	private Date fechaRegistro;

	@Column(length = 8)
	private String entorno;

	@Column(columnDefinition = "boolean default false")
	private Boolean debug;

	@Column(length = 512, columnDefinition = "varchar(512) default 'smtp.gmail.com'")
	private String host;

	public CorreoData() {
		this.fechaRegistro = new Date();
		this.debug = false;
		this.host = "smtp.gmail.com";
	}

	public CorreoData(String correo, String keyAcceso, String entorno) {
		this.correo = correo;
		this.keyAcceso = keyAcceso;
		this.entorno = entorno;
	}

	@PrePersist
	public void prePersist() {
		this.fechaRegistro = new Date();
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Boolean getDebug() {
		return debug;
	}

	public void setDebug(Boolean debug) {
		this.debug = debug;
	}

	public String getEntorno() {
		return entorno;
	}

	public void setEntorno(String entorno) {
		this.entorno = entorno;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getClaveAcceso() {
		return claveAcceso;
	}

	public void setClaveAcceso(String claveAcceso) {
		this.claveAcceso = claveAcceso;
	}

	public String getKeyAcceso() {
		return keyAcceso;
	}

	public void setKeyAcceso(String keyAcceso) {
		this.keyAcceso = keyAcceso;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

}
