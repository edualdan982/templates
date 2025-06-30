package edual.auth.entity.auth;

public enum TypeDoc {
  CI("Cédula de Identidad"),
  PAS("Pasaporte"),
  CEX("Cédula de Identidad Extranjera"),;

  private String desc;

  private TypeDoc(String desc) {
    this.desc = desc;
  }

  public String getDesc() {
    return desc;
  }
}
