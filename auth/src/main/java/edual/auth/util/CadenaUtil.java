package edual.auth.util;

public class CadenaUtil {

  public static String maskString(String str, int start, int end) {
    if (str == null || str.length() == 0) {
      return str;
    }
    int length = str.length();
    if (start + end >= length) {
      return str; // No hay suficientes caracteres para ocultar en el medio
    }
    StringBuilder maskedString = new StringBuilder();
    maskedString.append(str.substring(0, start)); // Parte visible inicial
    for (int i = start; i < length - end; i++) {
      maskedString.append('*'); // Ocultar con asteriscos
    }
    maskedString.append(str.substring(length - end)); // Parte visible final
    return maskedString.toString();
  }

  public static String maskString(String str, int start, int end, char maskChar) {
    if (str == null || str.length() == 0) {
      return str;
    }
    int length = str.length();
    if (start + end >= length) {
      return str; // No hay suficientes caracteres para ocultar en el medio
    }
    StringBuilder maskedString = new StringBuilder();
    maskedString.append(str.substring(0, start)); // Parte visible inicial
    for (int i = start; i < length - end; i++) {
      maskedString.append(maskChar); // Ocultar con asteriscos
    }
    maskedString.append(str.substring(length - end)); // Parte visible final
    return maskedString.toString();
  }

  public static String maskCorreo(String correo) {
    if (correo == null || !correo.contains("@")) {
      return correo;
    }

    String[] partes = correo.split("@");
    String usuario = partes[0];
    String dominio = partes[1];

    int largo = usuario.length();

    int inicioVisible = Math.min(3, largo / 2);
    int finVisible = Math.min(3, largo - inicioVisible - 1);
    if (inicioVisible + finVisible >= largo) {
      return usuario + "@" + dominio;
    }

    String inicio = usuario.substring(0, inicioVisible);
    String fin = usuario.substring(largo - finVisible);
    String asteriscos = repetirCaracter('*', largo - inicioVisible - finVisible);

    return inicio + asteriscos + fin + "@" + dominio;
  }

  private static String repetirCaracter(char c, int veces) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < veces; i++) {
      sb.append(c);
    }
    return sb.toString();
  }
}
