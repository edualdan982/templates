package edual.auth.entity.auth;

/**
 * Enumeración de los tipos de cuenta de usuario.
 * Máximo de descripción de la cadena de 8 bits
 */
public enum TypeAccount {
  INST("Institucional"),
  USER("Usuario"),
  API("Interfaz de aplicaciones"),
  OTHER("Otro");

  private String desc;

  private TypeAccount(String desc) {
    this.desc = desc;
  }

  public String getDesc() {
    return desc;
  }
}
