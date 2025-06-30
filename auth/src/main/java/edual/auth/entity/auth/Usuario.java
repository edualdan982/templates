package edual.auth.entity.auth;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import edual.auth.entity.GenericEntity;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "auth_usuario", uniqueConstraints = {
		@UniqueConstraint(name = "auth_username_uq", columnNames = "username") })
public class Usuario extends GenericEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqAuthIdUsuarioSeq")
	@SequenceGenerator(name = "seqAuthIdUsuarioSeq", allocationSize = 1, sequenceName = "AUTH_ID_USUARIO_SEQ")
	private Integer idUsuario;

	@NotEmpty
	@Size(max = 50)
	@Column(length = 50)
	private String username;

	@Size(max = 300)
	@Column(length = 300)
	private String password;

	@Column(columnDefinition = "number(1,0) default 0")
	private Boolean estado;

	@Enumerated(value = EnumType.STRING)
	@Column(length = 8)
	private TypeAccount tipo;

	@JsonIgnoreProperties(value = { "roles", "hibernateLazyInitializer", "handler" })
	@OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<UsuarioRol> roles = new ArrayList<>();

	@Column(length = 512)
	@Size(max = 512)
	@NotEmpty
	@Pattern(regexp = "^(.+)@(\\S+)$", message = "No es un correo valido Ej. usuario@dominio.com")
	private String correo;

	@Column(length = 512)
	private String claveWeb;

	@JsonProperty(defaultValue = "0")
	@Column(columnDefinition = "number(1) default 0")
	private Byte valPass;

	@Column(length = 32)
	private String telefono;

	@Column(columnDefinition = "number(10,0) default 1")
	private Integer idUsuarioReg;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLogin;

	@Column(columnDefinition = "number(1,0) default 0")
	private Boolean firstLogin;

	@Column(columnDefinition = "number(1,0) default 0")
	private Boolean migrado;

	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaModificacion;

	@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idPersona", foreignKey = @ForeignKey(name = "AUTH_ID_PERSONA_AUSUARIO_FK"))
	private Persona persona;

	private Integer codigoVerificacion;

	public Usuario() {
		super();
	}

	@PrePersist
	public void prePersist() {
		super.initial();
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@JsonGetter("password")
	public String customGetPassword() {
		return password;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}

	public TypeAccount getTipo() {
		return tipo;
	}

	public void setTipo(TypeAccount tipo) {
		this.tipo = tipo;
	}

	public List<UsuarioRol> getRoles() {
		return roles;
	}

	public void setRoles(List<UsuarioRol> roles) {
		this.roles = roles;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getClaveWeb() {
		return claveWeb;
	}

	public void setClaveWeb(String claveWeb) {
		this.claveWeb = claveWeb;
	}

	public Byte getValPass() {
		return valPass;
	}

	public void setValPass(Byte valPass) {
		this.valPass = valPass;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public Integer getIdUsuarioReg() {
		return idUsuarioReg;
	}

	public void setIdUsuarioReg(Integer idUsuarioReg) {
		this.idUsuarioReg = idUsuarioReg;
	}

	public Date getFechaModificacion() {
		return fechaModificacion;
	}

	public void setFechaModificacion(Date fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Date getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Boolean getFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(Boolean firstLogin) {
		this.firstLogin = firstLogin;
	}

	public Boolean getMigrado() {
		return migrado;
	}

	public void setMigrado(Boolean migrado) {
		this.migrado = migrado;
	}

	public Integer getCodigoVerificacion() {
		return codigoVerificacion;
	}

	public void setCodigoVerificacion(Integer codigoVerificacion) {
		this.codigoVerificacion = codigoVerificacion;
	}
	
}
