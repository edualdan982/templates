package edual.auth.service.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.Column;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import edual.auth.AuthApplication;
import edual.auth.config.constantes.Mensajes;

@Service
public class EntidadUtilService implements IEntidadUtilService {
  private static final Logger log = LoggerFactory.getLogger(EntidadUtilService.class);

  @Override
  public <T> T pasarDatos(T entidadOrigen, T entidadDestino, Boolean transferirTodo) {
    Field[] atributos = entidadOrigen.getClass().getDeclaredFields();

    for (Field atributo : atributos) {
      if (atributo.getModifiers() <= 2 && (Boolean.TRUE.equals(transferirTodo)
          || !atributo.getType().isInterface())) {
        log.debug("atributo: {}, v: {}", atributo.getName(), atributo.getType());
        try {
          Optional<Object> valorOpcional = ejecutarGetter(entidadOrigen, atributo.getName());
          if (valorOpcional.isPresent())
            ejutarSetter(entidadDestino, atributo.getName(), valorOpcional.get(), atributo.getType());
        } catch (Exception e) {
          log.error("Error al pasar datos de la entidad origen a la entidad destino. Detalle: {}",
              e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage());
        }
      }
    }
    return entidadDestino;
  }

  @Override
  public <T> List<FieldError> validarDatos(T objetoValidar, List<String> excepciones) {
    List<FieldError> errors = new ArrayList<>();
    Field[] atributos = objetoValidar.getClass().getDeclaredFields();

    for (Field atributo : atributos) {
      if (atributo.getModifiers() <= 2 && !atributo.getType().isInterface()
          && atributo.getType().getPackage().getName().indexOf(AuthApplication.class.getPackage().getName()) == -1) {
        if (!excepciones.isEmpty() && excepciones.contains(atributo.getName()))
          continue;
        log.debug("Atributo: {}, Tipo: {}", atributo.getName(), atributo.getType());
        try {
          Optional<Object> valorOpcional = ejecutarGetter(objetoValidar, atributo.getName());
          valorOpcional.ifPresent(obj -> {
            switch (getType(obj)) {
              case STRING:
                if (obj.toString().trim().isEmpty())
                  errors.add(
                      new FieldError(objetoValidar.getClass().getSimpleName(), atributo.getName(),
                          "no pueder ser vacío"));
                Annotation[] annotations = atributo.getAnnotations();
                for (Annotation annotation : annotations) {
                  if (annotation instanceof Column) {
                    Column column = (Column) annotation;
                    if (obj.toString().length() > column.length()) {
                      errors.add(
                          new FieldError(objetoValidar.getClass().getSimpleName(), atributo.getName(),
                              "no debe sobrepasar los  " + column.length() + " caracteres"));
                      break;
                    }
                  }
                }
                break;
              case INTEGER:
                if (Integer.parseInt(obj.toString()) <= 0)
                  errors.add(
                      new FieldError(objetoValidar.getClass().getSimpleName(), atributo.getName(),
                          "no puede ser negativo"));
                break;
              case BOOLEAN:
                if (!Boolean.TRUE.equals(obj) || !Boolean.FALSE.equals(obj))
                  errors.add(
                      new FieldError(objetoValidar.getClass().getSimpleName(), atributo.getName(),
                          "debe enviar un valor booleano(true/false)"));
                break;
              case DATE:
                Date fecha = (Date) obj;
                if (fecha == null)
                  errors.add(
                      new FieldError(objetoValidar.getClass().getSimpleName(), atributo.getName(),
                          "el date enviado no es valido"));
                break;
              default:
                log.debug("No se reconoce el tipo de dato para la validación.");
                break;
            }
          });
          valorOpcional.orElseGet(() -> {
            errors.add(new FieldError(objetoValidar.getClass().getSimpleName(), atributo.getName(),
                "no puede ser nulo"));
            return null;
          });
        } catch (Exception e) {
          log.error("Error al validar el objeto. Detalle: {}",
              e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage());
        }
      }
    }
    return errors;
  }

  @Override
  public <T> void validarDatos(T objetoValidar, BindingResult bindingResult) {
    Field[] atributos = objetoValidar.getClass().getDeclaredFields();

    for (Field atributo : atributos) {
      if (atributo.getModifiers() <= 2 && !atributo.getType().isInterface()) {
        log.debug("atributo: {}, v: {}", atributo.getName(), atributo.getType());
        try {
          Optional<Object> valorOpcional = ejecutarGetter(objetoValidar, atributo.getName());
          valorOpcional.ifPresent(obj -> {
            switch (getType(obj)) {
              case STRING:
                if (obj.toString().trim().isEmpty())
                  bindingResult.rejectValue(atributo.getName(), "error.empty");
                break;
              case INTEGER:
                if (Integer.parseInt(obj.toString()) <= 0)
                  bindingResult.rejectValue(atributo.getName(), "error.empty");
                break;
              case BOOLEAN:
                if (Boolean.TRUE.equals(obj) || Boolean.FALSE.equals(obj))
                  bindingResult.rejectValue(atributo.getName(), "error",
                      "debe enviar un valor booleano(true/false)");
                break;
              default:
                log.debug("No se reconoce el tipo de dato para la validación.");
                break;
            }
          });
        } catch (Exception e) {
          log.error("Error al pasar datos de la entidad origen a la entidad destino. Detalle: {}",
              e.getLocalizedMessage() == null ? "Sin detalle" : e.getLocalizedMessage());
        }
      }
    }
  }

  private void ejutarSetter(Object objeto, String nombreAtributo, Object valor, Class<?> clase)
      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException,
      SecurityException {
    String nombreMetodo = String.format("set%s%s", nombreAtributo.substring(0, 1).toUpperCase(),
        nombreAtributo.substring(1));

    Method metodo = objeto.getClass().getMethod(nombreMetodo, clase);
    metodo.invoke(objeto, valor);
  }

  private Optional<Object> ejecutarGetter(Object objeto, String nombreAtributo)
      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException,
      SecurityException {
    Class<?> clase = objeto.getClass();
    String nombreGetter = "get" + nombreAtributo.substring(0, 1).toUpperCase() + nombreAtributo.substring(1);
    Method metodoGetter = clase.getMethod(nombreGetter);
    return Optional.ofNullable(metodoGetter.invoke(objeto));

  }

  private Type getType(Object obj) {
    if (obj instanceof String) {
      return Type.STRING;
    } else if (obj instanceof Integer) {
      return Type.INTEGER;
    } else if (obj instanceof Boolean) {
      return Type.BOOLEAN;
    } else if (obj instanceof Date) {
      return Type.DATE;
    } else {
      return Type.UNKNOWN;
    }
  }

  private enum Type {
    STRING, INTEGER, BOOLEAN, DATE, UNKNOWN
  }

  @Override
  public <T> Map<String, String> listarAtributos(T clase, List<String> excepciones) {
    Map<String, String> respAtributos = new HashMap<>();

    Field[] atributos = clase.getClass().getDeclaredFields();
    for (Field atributo : atributos) {
      log.info("Atributo: {}, Interface: {}", atributo.getName(), atributo.getType().isInterface());
      if (atributo.getModifiers() <= 2
          && atributo.getType().getPackage().getName().indexOf(AuthApplication.class.getPackage().getName()) == -1) {
        if (!excepciones.isEmpty() && excepciones.contains(atributo.getName()))
          continue;
        respAtributos.put(atributo.getName(), this.darFormatoCadena(atributo.getName()));
      }
    }
    return respAtributos;
  }

  @Override
  public <T> Map<String, String> listarAtributos(T clase) {
    Map<String, String> respAtributos = new HashMap<>();

    Field[] atributos = clase.getClass().getDeclaredFields();
    for (Field atributo : atributos) {
      log.info("Atributo: {}, Interface: {}", atributo.getName(), atributo.getType().isInterface());
      if (atributo.getModifiers() <= 2 && !atributo.getType().isInterface()
          && atributo.getType().getPackage().getName().indexOf(AuthApplication.class.getPackage().getName()) == -1) {
        respAtributos.put(atributo.getName(), this.darFormatoCadena(atributo.getName()));
      }
    }
    return respAtributos;
  }

  @Override
  public String darFormatoCadena(final String cadena) {
    return cadena.replaceAll("([A-Z])", " $1").toUpperCase();
  }

  @Override
  public <T> Map<String, Object> extraerDatos(T clase) {
    Map<String, Object> respDatos = new HashMap<>();
    Class<?> claseActual = clase.getClass();

    while (claseActual != null) { // Recorre la jerarquía de clases
      Field[] atributos = claseActual.getDeclaredFields();
      for (Field atributo : atributos) {
        log.debug("Atributo: {}, Tipo: {}, Modifiers: {}, Primitivo: {}", atributo.getName(), atributo.getType(),
            atributo.getModifiers(), this.esTipoAceptado(atributo.getType()));
        if (atributo.getModifiers() <= 4 && (this.esTipoAceptado(atributo.getType()))) {
          try {
            atributo.setAccessible(true); // Permitir acceso a atributos privados
            respDatos.put(atributo.getName(), atributo.get(clase));
          } catch (Exception e) {
            log.error("Error al extraer datos del atributo {}. Detalle: {}", atributo.getName(),
                e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage());
          }
        }
      }
      claseActual = claseActual.getSuperclass(); // Subir en la jerarquía
    }
    return respDatos;
  }

  private boolean esTipoAceptado(Class<?> atributo) {
    return atributo.isPrimitive() ||
        atributo.getName().startsWith("java.lang") ||
        atributo.getName().startsWith("java.util") ||
        Enum.class.isAssignableFrom(atributo);
  }
}
