package edual.auth.entity.auth.id;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class IdUSuarioClientDetails implements Serializable {
  @NotNull
  private Integer idUsuario;

  @NotNull
  private String clientId;

  @Override
  public int hashCode() {
    return Objects.hash(idUsuario, clientId);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    IdUSuarioClientDetails that = (IdUSuarioClientDetails) obj;
    return Objects.equals(idUsuario, that.idUsuario) && Objects.equals(clientId, that.clientId);

  }

  public IdUSuarioClientDetails() {
  }

  public IdUSuarioClientDetails(@NotNull Integer idUsuario, @NotNull String clientId) {
    this.idUsuario = idUsuario;
    this.clientId = clientId;
  }

  public Integer getIdUsuario() {
    return idUsuario;
  }

  public void setIdUsuario(Integer idUsuario) {
    this.idUsuario = idUsuario;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  private static final long serialVersionUID = 1L;
}
