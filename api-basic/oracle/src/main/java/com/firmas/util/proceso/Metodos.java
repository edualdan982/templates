package com.firmas.util.proceso;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;

import com.firmas.config.constantes.Mensajes;
import com.firmas.config.constantes.ResponseKeys;

public class Metodos {

  /**
   * 
   * @return Retorna un int de la gestión actual en formato yyyy
   */
  public static int getGestionActual() {
    SimpleDateFormat getFormatoAnio = new SimpleDateFormat("yyyy");
    return Integer.parseInt(getFormatoAnio.format(new Date()));
  }

  public static String getPeriodoGestion() {
    SimpleDateFormat getFormatoAnio = new SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("es-BO"));
    return getFormatoAnio.format(new Date()).toUpperCase();
  }

  public static GrantedAuthority buscarRole(Collection<? extends GrantedAuthority> authorities,
      Predicate<GrantedAuthority> predicate) {
    @SuppressWarnings("unchecked")
    Optional<GrantedAuthority> res = (Optional<GrantedAuthority>) authorities.stream().filter(predicate)
        .findFirst();
    return res.orElse(null);
  }

  public static String getPeriodo() {
    SimpleDateFormat getFormatoAnio = new SimpleDateFormat("MM-yyyy", Locale.forLanguageTag("es-BO"));
    return getFormatoAnio.format(new Date()).toUpperCase();
  }

  public static Map<String, Object> extraerErroes(BindingResult result) {
    Map<String, Object> response = BuildMap.initMapReg();
    response.put(ResponseKeys.MENSAJE.getKey(), Mensajes.VALID);
    response.put(ResponseKeys.ERRORS.getKey(),
        result.getFieldErrors().stream().map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.toList()));
    return response;
  }
}