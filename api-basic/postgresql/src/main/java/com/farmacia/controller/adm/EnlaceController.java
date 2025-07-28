package com.farmacia.controller.adm;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.farmacia.entity.adm.Enlace;
import com.farmacia.entity.adm.Menu;
import com.farmacia.service.adm.IEnlaceService;
import com.farmacia.service.adm.IMenuService;
import com.farmacia.config.constantes.ResponseKeys;
import com.farmacia.config.constantes.Mensajes;

@RestController
@RequestMapping("/enlace")
@Secured("ROLE_ADMIN")
public class EnlaceController {
  private static final Logger log = LoggerFactory.getLogger(EnlaceController.class);
  @Autowired
  private IEnlaceService enlaceService;
  @Autowired
  private IMenuService menuService;

  @GetMapping
  public List<Enlace> listar() {
    return enlaceService.listar();
  }

  @PostMapping
  public ResponseEntity<Map<String, Object>> guardar(@Valid @RequestBody Enlace enlaceReq, BindingResult result) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), true);
    response.put(ResponseKeys.REGISTRO.getKey(), null);
    response.put(ResponseKeys.ERRORS.getKey(), null);

    if (result.hasErrors()) {
      List<String> errors = result.getFieldErrors().stream().map(e -> e.getField() + ": " + e.getDefaultMessage())
          .collect(Collectors.toList());
      response.put(ResponseKeys.ERRORS.getKey(), errors);
      response.put(ResponseKeys.MENSAJE.getKey(), Mensajes.VALID);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    if (enlaceReq.getMenu() != null) {
      enlaceReq.getMenu().setIdMenu(enlaceReq.getMenu().getIdMenu() == null ? 0 : enlaceReq.getMenu().getIdMenu());
      Menu verificarMenu = menuService.buscarPorId(enlaceReq.getMenu().getIdMenu()).orElse(null);
      if (verificarMenu == null) {
        response.put(ResponseKeys.MENSAJE.getKey(), "El menu a relacionar no existe con idMenu: " + enlaceReq.getMenu().getIdMenu());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }
      enlaceReq.setMenu(verificarMenu);
    }

    try {
      response.put(ResponseKeys.REGISTRO.getKey(), enlaceService.guardar(enlaceReq));
      response.put(ResponseKeys.ESTADO.getKey(), true);
      response.put(ResponseKeys.MENSAJE.getKey(), "Se ha registrado el enlace");
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      String msg = "No se puedo completar el registro. Detalle: "
          + (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage());
      log.error(msg);
      response.put(ResponseKeys.MENSAJE.getKey(), msg);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @PutMapping
  public ResponseEntity<Map<String, Object>> actualizar(@Valid @RequestBody Enlace enlaceReq, BindingResult result) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), true);
    response.put(ResponseKeys.REGISTRO.getKey(), null);
    response.put(ResponseKeys.ERRORS.getKey(), null);

    if (enlaceReq.getIdEnlace() == null)
      result.rejectValue("idEnlace", ResponseKeys.ERROR.getKey(), "no puede ser nulo o vacio");

    if (result.hasErrors()) {
      List<String> errors = result.getFieldErrors().stream().map(e -> e.getField() + ": " + e.getDefaultMessage())
          .collect(Collectors.toList());
      response.put(ResponseKeys.ERRORS.getKey(), errors);
      response.put(ResponseKeys.MENSAJE.getKey(), Mensajes.VALID);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    Enlace oldEnlace = enlaceService.buscarPorId(enlaceReq.getIdEnlace()).orElse(null);
    if (oldEnlace == null) {
      response.put(ResponseKeys.MENSAJE.getKey(),
          "No se encuentran el menu para actualizar con el idEnlace: " + enlaceReq.getIdEnlace());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    oldEnlace.setAbreviatura(enlaceReq.getAbreviatura());
    oldEnlace.setDescripcion(enlaceReq.getDescripcion());
    oldEnlace.setImagen(enlaceReq.getImagen());
    oldEnlace.setOrden(enlaceReq.getOrden());
    oldEnlace.setEstado(enlaceReq.getEstado());
    oldEnlace.setRuta(enlaceReq.getRuta());

    if (enlaceReq.getMenu() != null) {
      if (enlaceReq.getMenu().getIdMenu() == null) {
        response.put(ResponseKeys.MENSAJE.getKey(), "Debe enviar en el Menu el atributo idMenu para poder reasignarlo");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }

      Menu reasignarMenu = menuService.buscarPorId(enlaceReq.getMenu().getIdMenu()).orElse(null);
      if (reasignarMenu == null) {
        response.put(ResponseKeys.MENSAJE.getKey(),
            "El menu a relacionar no existe con idMenu: " + enlaceReq.getMenu().getIdMenu());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }
      oldEnlace.setMenu(reasignarMenu);
      log.info("Se ha resignado un Menu al Enlace: {}", oldEnlace.getIdEnlace());
    }

    try {
      response.put(ResponseKeys.REGISTRO.getKey(), enlaceService.guardar(oldEnlace));
      response.put(ResponseKeys.ESTADO.getKey(), true);
      response.put(ResponseKeys.MENSAJE.getKey(), "Se ha actualizado el Enlace");
    } catch (Exception e) {
      String msg = "No se puedo completar la actualización de datos. Detalle: "
          + (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage());
      log.error(msg);
      response.put(ResponseKeys.MENSAJE.getKey(), msg);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    return ResponseEntity.ok(response);
  }

  @DeleteMapping
  public ResponseEntity<Map<String, Object>> eliminar(@RequestParam Integer idEnlace) {
    Map<String, Object> response = new HashMap<>();
    if (idEnlace == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "El parametro idEnlace es necesario para la operación");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    Enlace oldMenu = enlaceService.buscarPorId(idEnlace).orElse(null);
    if (oldMenu == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "No se encuentran el menu para eliminar con el idEnlace: " + idEnlace);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    if (Boolean.TRUE.equals(enlaceService.eliminarPorId(idEnlace))) {
      response.put(ResponseKeys.ESTADO.getKey(), true);
      response.put(ResponseKeys.MENSAJE.getKey(), "Se ha eliminado el Enlace: " + idEnlace);
      return ResponseEntity.ok(response);
    } else {
      response.put(ResponseKeys.MENSAJE.getKey(), "No pudo completar la operacion de eliminación");
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
    }
  }

}
