package com.firmas.controller.auth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.firmas.config.constantes.Mensajes;
import com.firmas.config.constantes.ResponseKeys;
import com.firmas.entity.auth.Rol;
import com.firmas.entity.auth.Usuario;
import com.firmas.entity.auth.UsuarioRol;
import com.firmas.entity.auth.id.IdUsuarioRol;
import com.firmas.service.auth.IRolService;
import com.firmas.service.auth.IUsuarioRolService;
import com.firmas.service.auth.IUsuarioService;

@RestController
@RequestMapping("/usuario-rol")
@Secured({ "ROLE_ADMIN" })
public class UsuarioRolController {

  private static final Logger log = LoggerFactory.getLogger(UsuarioRolController.class);

  @Autowired
  private IUsuarioRolService usuarioRolService;
  @Autowired
  private IUsuarioService usuarioService;
  @Autowired
  private IRolService rolService;

  @GetMapping
  public List<UsuarioRol> listar(@RequestParam(required = false) Integer idUsuario) {
    if (idUsuario == null)
      return new ArrayList<>();
    return usuarioRolService.listarPorUsuario(idUsuario);
  }

  @PostMapping
  public ResponseEntity<Map<String, Object>> guardar(@RequestBody @Valid UsuarioRol usuarioRolReq,
      BindingResult result) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);
    response.put(ResponseKeys.ERRORS.getKey(), null);
    response.put(ResponseKeys.REGISTRO.getKey(), null);

    if (result.hasErrors()) {
      List<String> errors = result.getFieldErrors().stream()
          .map(err -> err.getField() + ": " + err.getDefaultMessage()).collect(Collectors.toList());
      response.put(ResponseKeys.MENSAJE.getKey(), "Se han encontrado validaciones no cumplidades en la solicitud");
      response.put(ResponseKeys.ERRORS.getKey(), errors);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    Usuario usuario = usuarioService.buscarPorId(usuarioRolReq.getId().getIdUsuario());
    if (usuario == null) {
      response.put(ResponseKeys.MENSAJE.getKey(),
          "No existe el usuario con idUsuario: " + usuarioRolReq.getId().getIdUsuario());
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
    }

    Rol rol = rolService.buscarPorId(usuarioRolReq.getId().getIdRol());
    if (rol == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "No existe el rol con idRol: " + usuarioRolReq.getId().getIdRol());
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
    }

    usuarioRolReq.setEliminado(false);
    usuarioRolReq.setFechaRegistro(new Date());
    if (usuarioRolReq.getFechaVencimiento() == null) {
      Calendar calendarVec = Calendar.getInstance();
      calendarVec.setTime(new Date());
      calendarVec.add(Calendar.DAY_OF_MONTH, 30);
      usuarioRolReq.setFechaVencimiento(calendarVec.getTime());
    }
    usuarioRolReq.setUsuario(usuario);
    usuarioRolReq.setRol(rol);

    try {
      response.put(ResponseKeys.REGISTRO.getKey(), usuarioRolService.guardar(usuarioRolReq));
      response.put(ResponseKeys.ESTADO.getKey(), true);
      response.put(ResponseKeys.MENSAJE.getKey(), "Se ha completado el registro");
    } catch (Exception e2) {
      log.error("No se puede agregar los roles al usuario.");
      log.error("Detalle: {}",
          (e2.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e2.getLocalizedMessage()));

      response.put(ResponseKeys.MENSAJE.getKey(),
          "No se puedo completar el registro: " + usuarioRolReq.getId().getIdUsuario() + "-"
              + usuarioRolReq.getId().getIdRol());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    return ResponseEntity.ok(response);
  }

  @DeleteMapping
  public ResponseEntity<Map<String, Object>> eliminar(@RequestParam(required = false) Integer idUsuario,
      @RequestParam(required = false) Integer idRol) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);

    if (idUsuario == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "El parametro idUsuario es necesario para la solicitud.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    if (idRol == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "El parametro idRol es necesario para la solicitud.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    IdUsuarioRol pk = new IdUsuarioRol(idUsuario, idRol);
    if (Boolean.FALSE.equals(usuarioRolService.existe(pk))) {
      response.put(ResponseKeys.MENSAJE.getKey(), "No existe el registro: " + idUsuario + "-" + idRol);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    try {
      usuarioRolService.eliminarPorId(pk);
      response.put(ResponseKeys.ESTADO.getKey(), true);
      response.put(ResponseKeys.MENSAJE.getKey(), "Se ha eliminado el registro: " + idUsuario + "-" + idRol);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put(ResponseKeys.MENSAJE.getKey(), "No se ha podido eliminar el registro: " + idUsuario + "-" + idRol);
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
    }
  }

}
