package edual.auth.entity.auth;

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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;

import edual.auth.entity.GenericEntity;

@Entity
@Table(name = "auth_rol", uniqueConstraints = {
		@UniqueConstraint(columnNames = "nombre", name = "auth_nombre_rol_uq") })
public class Rol extends GenericEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idAuthRolSeq")
	@SequenceGenerator(name = "idAuthRolSeq", allocationSize = 1, sequenceName = "AUTH_ID_ROL_SEQ")
	private Integer idRol;

	@Size(max = 30)
	@NotEmpty
	@Column(length = 30)
	private String nombre;

	@Size(max = 256)
	@Column(length = 256)
	private String descripcion;

	// @JsonIgnoreProperties(value = { "usuariosRoles", "hibernateLazyInitializer", "handler" })
	@OneToMany(mappedBy = "rol", fetch = FetchType.LAZY, orphanRemoval = true)
	@JsonBackReference
	private List<UsuarioRol> usuariosRoles;

	public Rol() {
		super();
	}
	

	public Rol(@Size(max = 30) @NotEmpty String nombre, @Size(max = 256) String descripcion) {
		this.nombre = nombre;
		this.descripcion = descripcion;
	}


	@PrePersist
	public void prePersist() {
		super.initial();
	}

	public Integer getIdRol() {
		return idRol;
	}

	public void setIdRol(Integer idRol) {
		this.idRol = idRol;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}