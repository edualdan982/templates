package edual.auth.service.util;

import java.util.List;
import java.util.Map;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public interface IEntidadUtilService {

  <T> T pasarDatos(T entidadOrigen, T entidadDestino, Boolean transferirTodo);

  <T> List<FieldError> validarDatos(T objetoValidar, List<String> excepciones);

  <T> void validarDatos(T objetoValidar, BindingResult bindingResult);

  <T> Map<String, String> listarAtributos(T clase, List<String> excepciones);

  <T> Map<String, String> listarAtributos(T clase);

  String darFormatoCadena(final String cadena);

  <T> Map<String, Object> extraerDatos(T clase);
}