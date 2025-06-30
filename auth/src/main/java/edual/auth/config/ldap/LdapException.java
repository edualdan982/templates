package edual.auth.config.ldap;

public class LdapException extends Exception {
  private final String metodo;
  private final String desc;

  public LdapException(String message, String metodo, String desc) {
    super(message);
    this.metodo = metodo;
    this.desc = desc;
  }

  public String getMetodo() {
    return metodo;
  }

  public String getDesc() {
    return desc;
  }

}
