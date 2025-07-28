package com.firmas.controller.adm;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.firmas.config.constantes.Mensajes;
import com.firmas.config.constantes.ResponseKeys;
import com.firmas.entity.adm.Menu;
import com.firmas.service.adm.IMenuService;

@RestController
@RequestMapping("/menu")
@Secured("ROLE_ADMIN")
public class MenuController {

  private static final Logger log = LoggerFactory.getLogger(MenuController.class);
  @Autowired
  private IMenuService menuService;

  @GetMapping
  public List<Menu> listar() {
    return menuService.listar();
  }

  @GetMapping("/buscar")
  public ResponseEntity<Map<String, Object>> buscarPorId(@RequestParam Integer idMenu) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), true);
    response.put(ResponseKeys.RESPUESTA.getKey(), null);

    Optional<Menu> menu = menuService.buscarPorId(idMenu);
    if (menu.isPresent()) {
      response.put(ResponseKeys.RESPUESTA.getKey(), menu.get());
      response.put(ResponseKeys.MENSAJE.getKey(), "Se ha encontrado el Menu, idMenu: " + idMenu);
      return ResponseEntity.ok(response);
    }
    response.put(ResponseKeys.MENSAJE.getKey(), "No se tiene registro del idMenu:" + idMenu);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @PostMapping
  public ResponseEntity<Map<String, Object>> guardar(@Valid @RequestBody Menu menuReq, BindingResult result) {
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
    try {
      response.put(ResponseKeys.REGISTRO.getKey(), menuService.guardar(menuReq));
      response.put(ResponseKeys.ESTADO.getKey(), true);
      response.put(ResponseKeys.MENSAJE.getKey(), "Se ha registrado el Menu");
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
  public ResponseEntity<Map<String, Object>> actualizar(@Valid @RequestBody Menu menuReq, BindingResult result) {
    Map<String, Object> response = new HashMap<>();
    response.put(ResponseKeys.ESTADO.getKey(), true);
    response.put(ResponseKeys.REGISTRO.getKey(), null);
    response.put(ResponseKeys.ERRORS.getKey(), null);

    if (menuReq.getIdMenu() == null)
      result.rejectValue("idMenu", ResponseKeys.ERROR.getKey(), "no puede ser nulo o vacio");

    if (result.hasErrors()) {
      List<String> errors = result.getFieldErrors().stream().map(e -> e.getField() + ": " + e.getDefaultMessage())
          .collect(Collectors.toList());
      response.put(ResponseKeys.ERRORS.getKey(), errors);
      response.put(ResponseKeys.MENSAJE.getKey(), Mensajes.VALID);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    Menu oldMenu = menuService.buscarPorId(menuReq.getIdMenu()).orElse(null);
    if (oldMenu == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "No se encuentran el menu para actualizar con el idMenu: " + menuReq.getIdMenu());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    oldMenu.setAbreviatura(menuReq.getAbreviatura());
    oldMenu.setDescripcion(menuReq.getDescripcion());
    oldMenu.setImagen(menuReq.getImagen());
    oldMenu.setOrden(menuReq.getOrden());

    try {
      response.put(ResponseKeys.REGISTRO.getKey(), menuService.guardar(oldMenu));
      response.put(ResponseKeys.ESTADO.getKey(), true);
      response.put(ResponseKeys.MENSAJE.getKey(), "Se ha actualizado el Menu");
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
  public ResponseEntity<Map<String, Object>> eliminar(@RequestParam Integer idMenu) {
    Map<String, Object> response = new HashMap<>();
    if (idMenu == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "El parametro idMenu es necesario para la operación");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    Menu oldMenu = menuService.buscarPorId(idMenu).orElse(null);
    if (oldMenu == null) {
      response.put(ResponseKeys.MENSAJE.getKey(), "No se encuentran el menu para eliminar con el idMenu: " + idMenu);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    if (Boolean.TRUE.equals(menuService.eliminarPorId(idMenu))) {
      response.put(ResponseKeys.ESTADO.getKey(), true);
      response.put(ResponseKeys.MENSAJE.getKey(), "Se ha eliminado el Menu: " + idMenu);
      return ResponseEntity.ok(response);
    } else {
      response.put(ResponseKeys.MENSAJE.getKey(), "No pudo completar la operacion de eliminación");
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
    }
  }
}
