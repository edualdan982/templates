package edual.auth.entity.auth.hist;

public enum TypeEstadoLogin {

  ERROR("ERROR"),
  WARNING("WARNING"),
  SUCCESS("SUCCESS");

  TypeEstadoLogin(String estado) {
    this.estado = estado;
  }

  private String estado;

  public String getEstado() {
    return estado;
  }

}
