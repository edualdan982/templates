package edual.auth.entity.auth.id;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class IdUsuarioRol implements Serializable {

  @NotNull
  private Integer idUsuario;

  @NotNull
  private Integer idRol;

  public IdUsuarioRol() {
  }

  public IdUsuarioRol(Integer idUsuario, Integer idRol) {
    this.idUsuario = idUsuario;
    this.idRol = idRol;
  }

  @Override
  public int hashCode() {
    return Objects.hash(idUsuario, idRol);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    IdUsuarioRol that = (IdUsuarioRol) obj;
    return Objects.equals(idUsuario, that.idUsuario) && Objects.equals(idRol, that.idRol);

  }

  public Integer getIdUsuario() {
    return idUsuario;
  }

  public void setIdUsuario(Integer idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Integer getIdRol() {
    return idRol;
  }

  public void setIdRol(Integer idRol) {
    this.idRol = idRol;
  }

  private static final long serialVersionUID = 1L;

}
