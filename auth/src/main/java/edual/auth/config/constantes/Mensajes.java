package edual.auth.config.constantes;

public class Mensajes {
  public static final String NO_NULO = "no puede ser nulo";
  public static final String NO_FOUND_ENTITY = "No se encontro la entidad %s con el identificador %d";
  public static final String NO_FOUND_FIND = "No se encontro la entidad %s con el identificador %s";
  public static final String NO_ERROR_DETAIL = "Sin detalle de la excepción";
  public static final String MENOR_A_CERO = "no pueder ser menor a cero";
  public static final String VALID = "La solicitud tiene validaciones que no fueron cumplidas, revise y reenvie la solicitud";
  public static final String NO_COMPLETE_ACT = "No se completo la actualización de la entidad %s. Detalle: %s";
  public static final String NO_COMPLETE_REG = "No se completo la registro de la entidad %s. Detalle: %s";
  public static final String NO_RECONOCIBLE = "No se puede reconocer el tipo-exporter para exportación";
  public static final String NO_COMPLETE = "No se puedo completar el proceso: %s. Detalle: %s";
  public static final String NO_BAD_REQUEST = "El request tiene validaciones no cumplidas";

  private Mensajes() {
  }
}