package com.firmas.controller.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.firmas.config.constantes.ResponseKeys;
import com.firmas.entity.auth.Persona;
import com.firmas.entity.auth.TypeDoc;
import com.firmas.service.auth.IPersonaService;

@RestController
@RequestMapping("/persona")
@Secured("ROLE_ADMIN")
public class PersonaController {

  private static final Logger log = LoggerFactory.getLogger(PersonaController.class);

  @Autowired
  private IPersonaService personaService;

  @GetMapping
  public ResponseEntity<List<Persona>> getMethodName() {
    log.info("RestController listar: {}", Persona.class.getSimpleName());
    return ResponseEntity.ok().body(personaService.listar());
  }

  @PostMapping
  public ResponseEntity<Map<String, Object>> guardar(@Valid @RequestBody Persona req, BindingResult result) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);
    response.put(ResponseKeys.RESPUESTA.getKey(), null);

    if (result.hasErrors()) {
      List<String> errors = result.getFieldErrors().stream()
          .map(e -> e.getField().concat(": ").concat(e.getDefaultMessage())).collect(Collectors.toList());
      response.put(ResponseKeys.ERRORS.getKey(), errors);

      response.put(ResponseKeys.MENSAJE.getKey(),
          "La solicitud tiene validaciones que no fueron cumplidas, revise y reenvie la solicitud");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    Optional<Persona> persona = personaService.buscarPorNroDoc(req.getNumeroDoc(), req.getComDoc(), req.getTipoDoc());
    if (persona.isPresent()) {
      response.put(ResponseKeys.MENSAJE.getKey(),
          String.format("El número de documento %s, tipo %s ya se encuentra registrado", req.getNumeroDoc(),
              req.getTipoDoc().name()));
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    response.put(ResponseKeys.RESPUESTA.getKey(), personaService.guardar(req));
    response.put(ResponseKeys.MENSAJE.getKey(), "Registro guardado correctamente");
    return ResponseEntity.ok(response);
  }

  @PutMapping
  public ResponseEntity<Map<String, Object>> actualizar(@Valid @RequestBody Persona req, BindingResult result) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);
    response.put(ResponseKeys.RESPUESTA.getKey(), null);

    if (req.getIdPersona() == null)
      result.rejectValue("idPersona", ResponseKeys.ERROR.getKey(), "El identificador es requerido.");

    if (result.hasErrors()) {
      List<String> errors = result.getFieldErrors().stream()
          .map(e -> e.getField().concat(": ").concat(e.getDefaultMessage())).collect(Collectors.toList());
      response.put(ResponseKeys.ERRORS.getKey(), errors);

      response.put(ResponseKeys.MENSAJE.getKey(),
          "La solicitud tiene validaciones que no fueron cumplidas, revise y reenvie la solicitud");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    Integer countNroDoc = personaService.contarNroDoc(req.getNumeroDoc(), req.getComDoc(), req.getTipoDoc());
    if (countNroDoc <= 1) {
      response.put(ResponseKeys.MENSAJE.getKey(),
          String.format("El número de documento %s, tipo %s ya se encuentra registrado", req.getNumeroDoc(),
              req.getTipoDoc().name()));
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    response.put(ResponseKeys.RESPUESTA.getKey(), personaService.guardar(req));
    response.put(ResponseKeys.MENSAJE.getKey(), "Registro guardado correctamente");
    return ResponseEntity.ok(response);
  }

  @GetMapping("/buscarPorNroDoc")
  public ResponseEntity<Map<String, Object>> buscarPorNroDoc(
      @RequestParam String nroDoc,
      @RequestParam String comDoc,
      @RequestParam TypeDoc tipoDoc) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);
    response.put(ResponseKeys.RESPUESTA.getKey(), null);

    Optional<Persona> buscarPersona = personaService.buscarPorNroDoc(nroDoc, comDoc, tipoDoc);
    if (buscarPersona.isPresent()) {
      response.put(ResponseKeys.RESPUESTA.getKey(), buscarPersona.get());
      response.put(ResponseKeys.ESTADO.getKey(), true);
      response.put(ResponseKeys.MENSAJE.getKey(), "Registro encontrado");
    } else
      response.put(ResponseKeys.MENSAJE.getKey(), "Registro no encontrado");
    return ResponseEntity.ok(response);
  }

}
