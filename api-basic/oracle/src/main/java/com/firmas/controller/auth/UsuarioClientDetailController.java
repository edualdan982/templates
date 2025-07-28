package com.firmas.controller.auth;

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
import org.springframework.security.oauth2.provider.OAuth2Request;
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
import com.firmas.entity.auth.Usuario;
import com.firmas.entity.auth.UsuarioClientDetails;
import com.firmas.entity.auth.id.IdUSuarioClientDetails;
import com.firmas.entity.auth.oauth.ClientDetail;
import com.firmas.service.auth.IUsuarioClientDetailService;
import com.firmas.service.auth.IUsuarioService;
import com.firmas.service.auth.oauth.IClientDetailService;
import com.firmas.service.util.IJwtAuthServiceUtil;

@RestController
@RequestMapping("/usuarioClientDetail")
@Secured({ "ROLE_ADMIN", "ROLE_REPORTES", "ROLE_PAGOS" })
public class UsuarioClientDetailController {
  private static final Logger log = LoggerFactory.getLogger(UsuarioClientDetailController.class);

  @Autowired
  private IUsuarioClientDetailService usuarioClientDetailsService;
  @Autowired
  private IJwtAuthServiceUtil jwtAuthServiceUtil;
  @Autowired
  private IClientDetailService clientsService;
  @Autowired
  private IUsuarioService usuarioService;

  @GetMapping
  public List<UsuarioClientDetails> listar(@RequestParam(required = false) Integer idUsuario) {
    if (idUsuario == null)
      return usuarioClientDetailsService.listar();
    return usuarioClientDetailsService.listarPorUsuario(idUsuario);
  }

  @PostMapping
  public ResponseEntity<Map<String, Object>> guardar(@RequestBody @Valid UsuarioClientDetails usuarioClientDetail,
      BindingResult result) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);
    response.put(ResponseKeys.REGISTRO.getKey(), null);
    response.put(ResponseKeys.ERRORS.getKey(), null);

    if (result.hasErrors()) {
      List<String> errors = result.getFieldErrors().stream()
          .map(err -> err.getField() + ":" + err.getDefaultMessage()).collect(Collectors.toList());

      response.put(ResponseKeys.ERRORS.getKey(), errors);
      response.put(ResponseKeys.MENSAJE.getKey(), "Las solicitud tiene validaciones no cumplidas, revise y reenvie la solicitud");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    ClientDetail sistema = null;
    if (usuarioClientDetail.getId() != null) {
      if (usuarioClientDetail.getId().getClientId() != null) {
        sistema = clientsService.buscarPorId(usuarioClientDetail.getId().getClientId());
      }
    } else {
      OAuth2Request authRequest = jwtAuthServiceUtil.getOAuth2Request();
      sistema = clientsService.buscarPorId(authRequest.getClientId());
    }
    if (sistema == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "No se pudo entontrar el clientDetails del token");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    Usuario usuario = usuarioService.buscarPorId(usuarioClientDetail.getId().getIdUsuario());
    if (usuario == null) {
      response.put(ResponseKeys.MENSAJE.getKey(),
          "No se pudo entontrar el usuario con el idUsuario: " + usuarioClientDetail.getId().getIdUsuario());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    usuarioClientDetail.setEliminado(false);

    if (usuarioClientDetail.getFechaVencimiento() == null) {
      Calendar calendarVec = Calendar.getInstance();
      calendarVec.setTime(new Date());
      calendarVec.add(Calendar.DAY_OF_MONTH, 30);
      usuarioClientDetail.setFechaVencimiento(calendarVec.getTime());
    }

    usuarioClientDetail.setSistema(sistema);
    usuarioClientDetail.setUsuario(usuario);
    try {
      response.put(ResponseKeys.REGISTRO.getKey(), usuarioClientDetailsService.guardar(usuarioClientDetail));
      response.put(ResponseKeys.MENSAJE.getKey(), "Se ha registrado con exito");
      response.put(ResponseKeys.ESTADO.getKey(), true);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("Error en el contrlador al persistir la entidad. Detalle"
          + (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
      response.put(ResponseKeys.MENSAJE.getKey(),
          "Ha ocurrido un error inesperado al persistir la entidad: " + UsuarioClientDetails.class);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @DeleteMapping
  public ResponseEntity<Map<String, Object>> eliminar(@RequestParam(required = false) Integer idUsuario,
      @RequestParam(required = false) String clientId) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);

    if (idUsuario == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "El parametro idUsuario es necesario para la solicitud.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    if (clientId == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "El parametro clientId es necesario para la solicitud.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    IdUSuarioClientDetails pk = new IdUSuarioClientDetails(idUsuario, clientId);
    if (!usuarioClientDetailsService.existe(pk)) {
      response.put(ResponseKeys.MENSAJE.getKey(), "No existe el registro: " + idUsuario + "-" + clientId);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    try {
      usuarioClientDetailsService.eliminarPorId(pk);
      response.put(ResponseKeys.ESTADO.getKey(), true);
      response.put(ResponseKeys.MENSAJE.getKey(), "Se ha eliminado el registro: " + idUsuario + "-" + clientId);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put(ResponseKeys.MENSAJE.getKey(), "No se ha podido eliminar el registro: " + idUsuario + "-" + clientId);
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
    }
  }

}
