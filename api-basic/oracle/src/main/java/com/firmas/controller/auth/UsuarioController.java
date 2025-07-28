package com.firmas.controller.auth;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.firmas.config.constantes.Mensajes;
import com.firmas.config.constantes.ResponseKeys;
import com.firmas.config.ldap.ILdapService;
import com.firmas.config.ldap.UserLdap;
import com.firmas.dto.auth.CambiarClaveReq;
import com.firmas.entity.auth.Persona;
import com.firmas.entity.auth.Rol;
import com.firmas.entity.auth.TypeAccount;
import com.firmas.entity.auth.TypeDoc;
import com.firmas.entity.auth.Usuario;
import com.firmas.entity.auth.UsuarioClientDetails;
import com.firmas.entity.auth.id.IdUSuarioClientDetails;
import com.firmas.entity.auth.oauth.ClientDetail;
import com.firmas.model.request.auth.ActualizarDatosReq;
import com.firmas.model.response.TypeArray;
import com.firmas.service.auth.IPersonaService;
import com.firmas.service.auth.IUsuarioClientDetailService;
import com.firmas.service.auth.IUsuarioRolService;
import com.firmas.service.auth.IUsuarioService;
import com.firmas.service.auth.oauth.IClientDetailService;
import com.firmas.service.correo.IMetodosAsincronos;
import com.firmas.service.util.IClaveValServiceUtil;
import com.firmas.service.util.IEntidadUtilService;
import com.firmas.service.util.IJwtAuthServiceUtil;
import com.firmas.util.proceso.BuildMap;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {
  private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);
  private static final String ID_USUARIO = "idUsuario";

  @Autowired
  private IUsuarioService usuarioService;
  @Autowired
  private IPersonaService personaService;
  @Autowired
  private IClaveValServiceUtil utilClaveService;
  @Autowired
  private IClientDetailService clientDetailsService;
  @Autowired
  private IUsuarioRolService usuarioRolService;
  @Autowired
  private IUsuarioClientDetailService usuarioClientDetailsService;
  @Autowired
  private ILdapService ldapService;
  @Autowired
  private IEntidadUtilService entidadService;
  @Autowired
  private IMetodosAsincronos metodosAsincronos;

  @Autowired
  private BCryptPasswordEncoder bcEncoder;
  @Autowired
  private IJwtAuthServiceUtil authJwtServiceUtil;

  @Value("${aplicacion.pass.ldap}")
  private String PASS_INST;

  @GetMapping("/types")
  public ResponseEntity<Map<String, Object>> listarTyes() {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);
    response.put(ResponseKeys.RESPUESTA.getKey(), null);

    Map<String, List<TypeArray>> resp = new HashMap<>();
    List<TypeArray> types = new ArrayList<>();
    // Para los tipos de cuenta
    for (TypeAccount type : TypeAccount.values()) {
      types.add(new TypeArray(type.name(), type.getDesc()));
    }
    resp.put("tipoCuenta", types);

    types = new ArrayList<>();
    // Para los tipos de documento
    for (TypeDoc type : TypeDoc.values()) {
      types.add(new TypeArray(type.name(), type.getDesc()));
    }
    resp.put("tipoDoc", types);

    response.put(ResponseKeys.RESPUESTA.getKey(), resp);
    response.put(ResponseKeys.MENSAJE.getKey(), "Se listaron los tipos");
    return ResponseEntity.ok(response);
  }

  @Secured({ "ROLE_ADMIN" })
  @GetMapping("/getAttrs")
  public ResponseEntity<Map<String, Object>> getAtributos() {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);
    response.put(ResponseKeys.RESPUESTA.getKey(), null);
    try {
      response.put(ResponseKeys.RESPUESTA.getKey(), entidadService.listarAtributos(new Usuario()));
      response.put(ResponseKeys.ESTADO.getKey(), true);
      response.put(ResponseKeys.MENSAJE.getKey(), "Se ha encontrado han listado los atributos.");
    } catch (Exception e) {
      log.error("Error al obtener los atributos. Detalle {}",
          (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
      response.put(ResponseKeys.MENSAJE.getKey(), "Ha ocurrido un error al intentar listar los atributos.");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Secured({ "ROLE_ADMIN" })
  @GetMapping("/paginado")
  public ResponseEntity<Map<String, Object>> listarPaginado(
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer size,
      @RequestParam(required = false) String numeroDocumento,
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String nombres) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);
    response.put(ResponseKeys.RESPUESTA.getKey(), null);

    try {
      Pageable pageable = PageRequest.of(Optional.ofNullable(page).orElse(0), Optional.ofNullable(size).orElse(10));
      response.put(ResponseKeys.RESPUESTA.getKey(),
          usuarioService.listarPaginado(pageable, numeroDocumento, username, nombres));
      response.put(ResponseKeys.ESTADO.getKey(), true);
      response.put(ResponseKeys.MENSAJE.getKey(), "Se ha encontrado los registros.");
    } catch (Exception e) {
      response.put(ResponseKeys.MENSAJE.getKey(), "Ha ocurrido un error al intentar listar los usuarios.");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Secured({ "ROLE_ADMIN" })
  @GetMapping
  public List<Usuario> listar() {
    return usuarioService.listar();
  }

  @Secured({ "ROLE_ADMIN", "ROLE_USER", "ROLE_PAGOS" })
  @GetMapping("/buscar-por-id")
  public ResponseEntity<Map<String, Object>> buscarPorId(@RequestParam(required = false) Integer idUsuario) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);
    response.put(ResponseKeys.RESPUESTA.getKey(), null);

    if (idUsuario == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "El parametro idUsuario es necesario para la operación.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    Usuario usuario = usuarioService.buscarPorId(idUsuario);
    if (usuario == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "No se ha encontrado coincidencias con el id proporcionado.");
    } else {
      response.put(ResponseKeys.RESPUESTA.getKey(), usuario);
      response.put(ResponseKeys.ESTADO.getKey(), true);
      response.put(ResponseKeys.MENSAJE.getKey(), "Se ha encontrado el id.");
    }
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Secured({ "ROLE_ADMIN" })
  @GetMapping("/buscar-por-usuario")
  public ResponseEntity<Map<String, Object>> buscarPorUsuario(@RequestParam(required = false) String usuario) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);
    response.put(ResponseKeys.RESPUESTA.getKey(), null);

    if (usuario == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "El parametro usuario es necesario para la operación.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    Usuario objUsuario = usuarioService.buscarPorUsuario(usuario, false);
    if (objUsuario == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "No se ha encontrado coincidencias con el usuario proporcionado.");
    } else {
      response.put(ResponseKeys.RESPUESTA.getKey(), objUsuario);
      response.put(ResponseKeys.ESTADO.getKey(), true);
      response.put(ResponseKeys.MENSAJE.getKey(), "Se ha encontrado el usuario.");
    }
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Secured({ "ROLE_ADMIN" })
  @PostMapping
  public ResponseEntity<Map<String, Object>> guardar(@Valid @RequestBody Usuario usuarioReq, BindingResult result) {
    Map<String, Object> response = BuildMap.initMap();

    if (usuarioReq.getValPass() == null)
      usuarioReq.setValPass((byte) 0);
    if (usuarioReq.getPassword() == null)
      usuarioReq.setPassword(utilClaveService.generarClave());

    if (usuarioReq.getTipo().equals(TypeAccount.INST)) {
      usuarioReq.setPassword(PASS_INST);
      usuarioReq.setValPass((byte) -1);
    }
    if (usuarioReq.getPersona() == null)
      result.rejectValue("persona", ResponseKeys.ERROR.getKey(), "No se ha enviado los datos de la persona");

    Optional<String> valClave = utilClaveService.validarClave(usuarioReq.getPassword(), usuarioReq.getValPass());
    if (valClave.isPresent())
      result.rejectValue("password", ResponseKeys.ERROR.getKey(), valClave.get());
    if (result.hasErrors()) {
      List<String> errors = result.getFieldErrors().stream()
          .map(e -> e.getField().concat(": ").concat(e.getDefaultMessage())).collect(Collectors.toList());
      response.put(ResponseKeys.ERRORS.getKey(), errors);

      response.put(ResponseKeys.MENSAJE.getKey(),
          "La solicitud tiene validaciones que no fueron cumplidas, revise y reenvie la solicitud");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    Integer valNombreUsuario = usuarioService.contarUsuario(usuarioReq.getUsername());
    if (valNombreUsuario > 0) {
      response.put(ResponseKeys.MENSAJE.getKey(), "El usuario: " + usuarioReq.getUsername() + ", ya esta en uso");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    Map<String, Object> datosUsuario = authJwtServiceUtil.getJwtContextAuth();
    if (datosUsuario.get(ID_USUARIO) instanceof Integer)
      usuarioReq.setIdUsuarioReg((Integer) datosUsuario.get(ID_USUARIO));

    String resClave = usuarioReq.getPassword();
    usuarioReq.setPassword(bcEncoder.encode(resClave));
    usuarioReq.setClaveWeb(UUID.randomUUID().toString());

    Persona persona = null;

    if (usuarioReq.getPersona() != null && usuarioReq.getPersona().getIdPersona() != -1)
      persona = personaService.buscarPorId(usuarioReq.getPersona().getIdPersona()).orElse(null);
    else
      persona = personaService.buscarPorNroDoc(usuarioReq.getPersona().getNumeroDoc(),
          usuarioReq.getPersona().getComDoc(), usuarioReq.getPersona().getTipoDoc()).orElse(null);
    try {
      if (persona == null)
        persona = personaService.guardar(usuarioReq.getPersona());
    } catch (Exception e) {
      String msg = String.format("No se pudo registar la persona. Detalle: %s",
          (e.getLocalizedMessage() == null ? "Sin detalle" : e.getLocalizedMessage()));
      log.error(msg);
      response.put(ResponseKeys.MENSAJE.getKey(), msg);

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    usuarioReq.setPersona(persona);

    Usuario nuevoUsuario = null;
    try {
      nuevoUsuario = usuarioService.guardar(usuarioReq, valNombreUsuario);
    } catch (Exception e) {
      response.put(ResponseKeys.MENSAJE.getKey(), "No se pudo completar el registro: "
          + (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    if (nuevoUsuario != null && usuarioClientDetailsService.asignarClientsJwt(nuevoUsuario, null))
      log.error("Se ha asignado al usuario el sistema solicitante.");

    response.put(ResponseKeys.REGISTRO.getKey(), nuevoUsuario);
    response.put(ResponseKeys.ESTADO.getKey(), true);
    response.put(ResponseKeys.MENSAJE.getKey(), "Se ha registrado un usuario");

    // Verificamos el correo si tiene institucional y se procede a enviar la
    // notificación de creación
    if (!usuarioReq.getTipo().equals(TypeAccount.INST))
      usuarioReq.setCorreo(usuarioReq.getUsername().concat("@umsa.bo"));
    // Servicio asincrono para el envio del correo y whatsapp
    metodosAsincronos.mandarCorreoVerificacion(usuarioReq, false, resClave);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @Secured({ "ROLE_ADMIN" })
  @PutMapping
  public ResponseEntity<Map<String, Object>> actualizar(@Valid @RequestBody Usuario usuarioReq,
      @RequestParam(required = false, defaultValue = "false") Boolean actClave, BindingResult result) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);
    response.put(ResponseKeys.ERRORS.getKey(), null);
    response.put("asignacion", null);

    List<String> errors = null;

    if (Boolean.TRUE.equals(actClave)) {
      Optional<String> valClave = utilClaveService.validarClave(usuarioReq.getPassword(), usuarioReq.getValPass());
      if (valClave.isPresent()) {
        result.rejectValue("password", ResponseKeys.ERROR.getKey(), valClave.get());
      }
    }
    if (result.hasErrors()) {
      errors = result.getFieldErrors().stream().map(e -> e.getField().concat(": ").concat(e.getDefaultMessage()))
          .collect(Collectors.toList());
      response.put(ResponseKeys.ERRORS.getKey(), errors);

      response.put(ResponseKeys.MENSAJE.getKey(),
          "La solicitud tiene validaciones que no fueron cumplidas, revise y reenvie la solicitud");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    if (usuarioReq.getIdUsuario() == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "Para actualizar debe enviar un idUsuario");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    if (usuarioService.contarUsuario(usuarioReq.getUsername()) > 1) {
      response.put(ResponseKeys.MENSAJE.getKey(), "El usuario: " + usuarioReq.getUsername() + ", ya esta en uso");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    Usuario oldUsuario = usuarioService.buscarPorId(usuarioReq.getIdUsuario());
    if (oldUsuario == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "No se encuentra los datos del usuario para actualizar");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    if (Boolean.TRUE.equals(actClave)) {
      oldUsuario.setPassword(bcEncoder.encode(usuarioReq.getPassword()));
      if (usuarioReq.getTipo().equals(TypeAccount.INST))
        oldUsuario.setTipo(TypeAccount.USER);
    } else
      oldUsuario.setTipo(usuarioReq.getTipo());

    oldUsuario.setEstado((usuarioReq.getEstado() != null && usuarioReq.getEstado()));
    oldUsuario.setValPass(usuarioReq.getValPass());
    oldUsuario.setUsername(usuarioReq.getUsername());
    oldUsuario.setCorreo(usuarioReq.getCorreo());
    oldUsuario.setEstado(usuarioReq.getEstado());
    oldUsuario.setTelefono(usuarioReq.getTelefono());

    if (usuarioReq.getPersona() != null && usuarioReq.getPersona().getIdPersona() != null) {
      Persona oldPersona = personaService.buscarPorId(usuarioReq.getPersona().getIdPersona()).orElse(null);
      if (oldPersona != null) {
        oldPersona.setApePaterno(usuarioReq.getPersona().getApePaterno());
        oldPersona.setApeMaterno(usuarioReq.getPersona().getApeMaterno());
        oldPersona.setNombres(usuarioReq.getPersona().getNombres());

        Integer count = personaService.contarNroDoc(usuarioReq.getPersona().getNumeroDoc(),
            usuarioReq.getPersona().getComDoc(), usuarioReq.getPersona().getTipoDoc());
        log.info("Count: {}", count);
        if (count <= 1) {
          oldPersona.setNumeroDoc(usuarioReq.getPersona().getNumeroDoc());
          oldPersona.setTipoDoc(usuarioReq.getPersona().getTipoDoc());
        } else {
          response.put(ResponseKeys.MENSAJE.getKey(),
              String.format("El tipo documento: %s, con número de documento: %s ya esta en uso",
                  usuarioReq.getPersona().getTipoDoc(), usuarioReq.getPersona().getNumeroDoc()));
          return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        oldPersona.setFechaNac(usuarioReq.getPersona().getFechaNac());
        oldUsuario.setPersona(personaService.guardar(oldPersona));
      }

    }

    usuarioService.guardar(oldUsuario);
    response.put("actualizar", oldUsuario);
    response.put(ResponseKeys.ESTADO.getKey(), true);
    response.put(ResponseKeys.MENSAJE.getKey(), "Se han actualizado los datos del usuario");

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Secured({ "ROLE_ADMIN" })
  @DeleteMapping
  public ResponseEntity<Map<String, Object>> eliminarPorId(@RequestParam(required = false) Integer idUsuario) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);

    if (idUsuario == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "El parametro idUsuario es necesario para la operación.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    if (Boolean.FALSE.equals(usuarioService.existePorId(idUsuario))) {
      response.put(ResponseKeys.MENSAJE.getKey(), "El idUsuario:" + idUsuario + ", no existe.");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    } else {
      if (Boolean.TRUE.equals(usuarioService.eliminarPorId(idUsuario))) {
        response.put(ResponseKeys.ESTADO.getKey(), true);
        response.put(ResponseKeys.MENSAJE.getKey(), "Se ha eliminado el registro con exito");
        return ResponseEntity.status(HttpStatus.OK).body(response);
      } else {
        response.put(ResponseKeys.ESTADO.getKey(), false);
        response.put(ResponseKeys.MENSAJE.getKey(), "No se ha eliminado el registro.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
      }
    }
  }

  @Secured({ "ROLE_ADMIN" })
  @PostMapping("/inst")
  public ResponseEntity<Map<String, Object>> guardarUsuarioInst(@RequestBody Usuario usuarioReq, BindingResult result) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);
    response.put(ResponseKeys.ERRORS.getKey(), null);

    usuarioReq.setValPass((byte) -1);

    if (usuarioReq.getUsername() == null)
      result.rejectValue("usuario", ResponseKeys.ERROR.getKey(), Mensajes.NO_NULO);
    else if (usuarioReq.getUsername().length() > 20)
      result.rejectValue("usuario", ResponseKeys.ERROR.getKey(), "solo se permiten 20 caracteres como máximo");

    if (result.hasErrors()) {
      List<String> errors = result.getFieldErrors().stream()
          .map(e -> e.getField().concat(": ").concat(e.getDefaultMessage())).collect(Collectors.toList());
      response.put(ResponseKeys.ERRORS.getKey(), errors);
      response.put(ResponseKeys.MENSAJE.getKey(),
          "La solicitud tiene validaciones que no fueron cumplidas, revise y reenvie la solicitud");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    Optional<UserLdap> userLdap = ldapService.searchUsername(usuarioReq.getUsername());
    if (!userLdap.isPresent()) {
      response.put(ResponseKeys.MENSAJE.getKey(), "El usuario no existe en el LDAP");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    usuarioReq.setTipo(TypeAccount.INST);
    usuarioReq.setCorreo(userLdap.get().getCorreo());
    usuarioReq.setTelefono(userLdap.get().getMobile());

    Integer valNombreUsuario = usuarioService.contarUsuario(usuarioReq.getUsername());

    usuarioReq.setPassword(bcEncoder.encode(PASS_INST));
    usuarioReq.setClaveWeb(UUID.randomUUID().toString());

    Usuario nuevoUsuario = null;
    try {
      nuevoUsuario = usuarioService.guardar(usuarioReq, valNombreUsuario);
    } catch (Exception e) {
      response.put(ResponseKeys.MENSAJE.getKey(), "No se pudo completar el registro: "
          + (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    if (nuevoUsuario != null) {

      OAuth2Request oatuh2 = authJwtServiceUtil.getOAuth2Request();

      if (oatuh2 != null) {
        ClientDetail sistema = clientDetailsService.buscarPorId(oatuh2.getClientId());
        UsuarioClientDetails asignarSistema = new UsuarioClientDetails();
        asignarSistema.setSistema(sistema);
        asignarSistema.setUsuario(nuevoUsuario);
        asignarSistema.setId(new IdUSuarioClientDetails(nuevoUsuario.getIdUsuario(), sistema.getClientId()));

        try {
          usuarioClientDetailsService.guardar(asignarSistema);
        } catch (Exception e) {
          e.printStackTrace();
        }

        log.info("Se ha asignado al usuario el sistema solicitante.");
      }
    }
    response.put(ResponseKeys.REGISTRO.getKey(), nuevoUsuario);
    metodosAsincronos.mandarCorreoVerificacion(nuevoUsuario, false, PASS_INST);

    if (nuevoUsuario.getTelefono() != null && !nuevoUsuario.getTelefono().equals("0")) {
      Boolean estadoEnvio = metodosAsincronos.mandarMensajeVerificacion(nuevoUsuario.getTelefono(),
          nuevoUsuario.getUsername(), nuevoUsuario.getClaveWeb());
      if (estadoEnvio) {
        log.info("Se envio el mensaje de activación al número de telefono: {}", nuevoUsuario.getTelefono());
      } else {
        log.error("No se pudo enviar el mensaje de activación al número de telefono: {}", nuevoUsuario.getTelefono());
      }
    }

    response.put(ResponseKeys.ESTADO.getKey(), true);
    response.put(ResponseKeys.MENSAJE.getKey(), "Se ha registrado un usuario");
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @Secured({ "ROLE_ADMIN", "ROLE_USER", "ROLE_PAGOS" })
  @PostMapping("/cambiarClave")
  public ResponseEntity<Map<String, Object>> cambiarClave(@RequestBody CambiarClaveReq req, BindingResult result) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);
    response.put(ResponseKeys.ERRORS.getKey(), null);

    if (result.hasErrors()) {
      List<String> errors = result.getFieldErrors().stream()
          .map(e -> e.getField().concat(": ").concat(e.getDefaultMessage())).collect(Collectors.toList());
      response.put(ResponseKeys.ERRORS.getKey(), errors);
      response.put(ResponseKeys.MENSAJE.getKey(), "Por favor revise los datos enviados");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    Usuario usuario = usuarioService.buscarPorId(req.getIdUsuario());
    if (usuario == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "No se ha encontrado el usuario o esta desactivado.");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    if (!bcEncoder.matches(req.getClaveActual(), usuario.getPassword())) {
      response.put(ResponseKeys.MENSAJE.getKey(), "La clave actual no coincide con la registrada.");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    if (usuarioService.cambiarClave(usuario.getIdUsuario(), bcEncoder.encode(req.getNuevaClave()))) {
      response.put(ResponseKeys.ESTADO.getKey(), true);
      response.put(ResponseKeys.MENSAJE.getKey(), "Se ha cambiado la clave con exito.");
      return new ResponseEntity<>(response, HttpStatus.OK);
    } else {
      response.put(ResponseKeys.MENSAJE.getKey(),
          "No se ha podido cambiar la clave, por favor contactese con sistemas.");
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Secured({ "ROLE_ADMIN" })
  @PostMapping("/asignar-roles")
  public ResponseEntity<Map<String, Object>> asignarRoles(@RequestBody List<Rol> roles,
      @RequestParam(required = false) Integer idUsuario,
      @RequestParam(required = false, defaultValue = "false") Boolean crearUsuario) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);
    response.put(ResponseKeys.RESPUESTA.getKey(), null);

    if (roles.isEmpty()) {
      response.put(ResponseKeys.MENSAJE.getKey(), "No se ha enviado roles para asignar");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    Usuario usuario = usuarioService.buscarPorId(idUsuario);
    if (usuario == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "No se ha encontrado el usuario");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    usuarioRolService.asignarRolPagos(usuario, roles, crearUsuario);

    return ResponseEntity.ok(response);
  }

  @Secured({ "ROLE_ADMIN" })
  @GetMapping("/reenviar-correo")
  public ResponseEntity<Map<String, Object>> reenviarCorreo(@RequestParam(required = true) String usuario,
      @RequestParam(required = false) String correoAlterno) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), false);

    Usuario oldUsuario = usuarioService.buscarPorUsuario(usuario, false);
    if (oldUsuario == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "Usuario ingresado incorrectamente o inexistente");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    if (correoAlterno != null)
      oldUsuario.setCorreo(correoAlterno);
    else if (oldUsuario.getCorreo() == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "El usuario: " + usuario
          + " no tiene un correo registrado, envie en el parametro correoAlterno para proseguir");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    oldUsuario.setClaveWeb(UUID.randomUUID().toString());
    oldUsuario.setFechaModificacion(new Date());
    usuarioService.guardar(oldUsuario);

    log.info("Se esta reenviando el correo de activación del usuario: {}, se enviara al correo: {}", usuario,
        oldUsuario.getCorreo());
    metodosAsincronos.mandarCorreoVerificacion(oldUsuario, true, null);
    response.put(ResponseKeys.ESTADO.getKey(), true);

    response.put(ResponseKeys.MENSAJE.getKey(),
        "Se esta enviando el correo de activación al: " + oldUsuario.getCorreo());
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Secured({ "ROLE_ADMIN", "ROLE_USER", "ROLE_PAGOS" })
  @PostMapping("/actualizar-datos")
  public ResponseEntity<Map<String, Object>> actualizarDatos(@RequestBody @Valid ActualizarDatosReq usuarioReq,
      @RequestHeader String authorization, BindingResult result) {
    Map<String, Object> response = BuildMap.initMap();

    if (result.hasErrors()) {
      List<String> errors = result.getFieldErrors().stream()
          .map(e -> e.getField().concat(": ").concat(e.getDefaultMessage())).collect(Collectors.toList());
      response.put(ResponseKeys.ERRORS.getKey(), errors);
      response.put(ResponseKeys.MENSAJE.getKey(),
          "La solicitud tiene validaciones que no fueron cumplidas, revise y reenvie la solicitud");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    Map<String, Object> dataJwt = new HashMap<>();
    try {
      dataJwt = authJwtServiceUtil.decodeJwt(authorization);
      if (dataJwt.isEmpty()) {
        response.put(ResponseKeys.ESTADO.getKey(), false);
        response.put(ResponseKeys.MENSAJE.getKey(), "No se ha podido leer el token o el token es invalido");
        return ResponseEntity.badRequest().body(response);
      }
    } catch (Exception e) {
      log.error("No se ha podido leer los datos del token. Detalle: {}",
          (e.getLocalizedMessage() == null ? "Sin detalle" : e.getLocalizedMessage()));
    }
    String username = dataJwt.get("user_name").toString();

    Usuario oldUsuario = usuarioService.buscarPorUsuario(username);
    if (oldUsuario == null) {
      response.put(ResponseKeys.ESTADO.getKey(), false);
      response.put(ResponseKeys.MENSAJE.getKey(), "No se ha encontrado el usuario");
      return ResponseEntity.badRequest().body(response);
    }

    oldUsuario.setCorreo(usuarioReq.getCorreo());
    oldUsuario.setTelefono(usuarioReq.getTelefono());

    Persona oldPersona = oldUsuario.getPersona();
    if (oldPersona != null) {
      oldPersona.setApePaterno(usuarioReq.getApePaterno());
      oldPersona.setApeMaterno(usuarioReq.getApeMaterno());
      oldPersona.setNombres(usuarioReq.getNombres());
      oldPersona.setFechaNac(usuarioReq.getFechaNac());
      oldUsuario.setPersona(personaService.guardar(oldPersona));
    }

    usuarioService.guardar(oldUsuario);
    response.put("actualizar", oldUsuario);
    response.put(ResponseKeys.ESTADO.getKey(), true);
    response.put(ResponseKeys.MENSAJE.getKey(), "Se han actualizado los datos del usuario");

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

}
