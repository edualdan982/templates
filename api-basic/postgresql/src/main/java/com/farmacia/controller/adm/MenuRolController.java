package com.farmacia.controller.adm;

import java.util.ArrayList;
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

import com.farmacia.entity.adm.Menu;
import com.farmacia.entity.adm.MenuRol;
import com.farmacia.entity.auth.Rol;
import com.farmacia.service.adm.IMenuRolService;
import com.farmacia.service.adm.IMenuService;
import com.farmacia.service.auth.IRolService;
import com.farmacia.config.constantes.ResponseKeys;
import com.farmacia.config.constantes.Mensajes;

@RestController
@RequestMapping("/menu-rol")
@Secured({ "ROLE_ADMIN", "ROLE_USER", "ROLE_PAGOS" })
public class MenuRolController {
	private static final Logger log = LoggerFactory.getLogger(MenuRolController.class);

	@Autowired
	private IMenuService menuService;
	@Autowired
	private IMenuRolService menuRolService;
	@Autowired
	private IRolService rolService;

	@Secured({ "ROLE_ADMIN", "ROLE_USER", "ROLE_PAGOS" })
	@GetMapping
	public ResponseEntity<Map<String, Object>> listar(@RequestParam(required = false) Integer idRol,
			@RequestParam(required = false, defaultValue = "false") Boolean fetchEnlaces) {
		Map<String, Object> response = new HashMap<>();
		response.put(ResponseKeys.ESTADO.getKey(), false);
		response.put(ResponseKeys.RESPUESTA.getKey(), null);

		if (idRol == null) {
			response.put(ResponseKeys.MENSAJE.getKey(), "Es necesario el parametro idRol.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		List<MenuRol> lista;

		if (Boolean.TRUE.equals(fetchEnlaces))
			lista = menuRolService.listarConEnlaces(idRol);
		else
			lista = menuRolService.listar(idRol);

		if (lista == null)
			lista = new ArrayList<>();
		response.put(ResponseKeys.RESPUESTA.getKey(), lista);
		response.put(ResponseKeys.ESTADO.getKey(), true);
		return ResponseEntity.ok(response);
	}

	@Secured({ "ROLE_ADMIN", "ROLE_USER", "ROLE_PAGOS" })
	@GetMapping("/buscarPorRol")
	public ResponseEntity<Map<String, Object>> listarPorRol(@RequestParam(required = false) String nombreRol) {
		Map<String, Object> response = new HashMap<>();
		response.put(ResponseKeys.ESTADO.getKey(), false);
		response.put(ResponseKeys.RESPUESTA.getKey(), null);

		if (nombreRol == null) {
			response.put(ResponseKeys.MENSAJE.getKey(), "Es necesario el parametro nombreRol:String.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		List<MenuRol> lista = menuRolService.listarPorMenu(nombreRol);

		if (lista == null)
			lista = new ArrayList<>();
		if (lista.isEmpty()) {
			response.put(ResponseKeys.MENSAJE.getKey(), "No se encontraron registros para el rol: " + nombreRol);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		response.put(ResponseKeys.RESPUESTA.getKey(), lista);
		response.put(ResponseKeys.ESTADO.getKey(), true);
		return ResponseEntity.ok(response);
	}

	@Secured("ROLE_ADMIN")
	@PostMapping
	public ResponseEntity<Map<String, Object>> guardar(@Valid @RequestBody MenuRol menuRolReq, BindingResult result) {
		Map<String, Object> response = new HashMap<>();
		response.put(ResponseKeys.ESTADO.getKey(), false);
		response.put(ResponseKeys.REGISTRO.getKey(), null);
		response.put(ResponseKeys.ERRORS.getKey(), null);

		if (menuRolReq.getRol() == null)
			result.rejectValue("rol", ResponseKeys.ERROR.getKey(), Mensajes.NO_NULO);
		if (menuRolReq.getMenu() == null)
			result.rejectValue("menu", ResponseKeys.ERROR.getKey(), Mensajes.NO_NULO);

		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream().map(e -> e.getField() + ": " + e.getDefaultMessage())
					.collect(Collectors.toList());
			response.put(ResponseKeys.MENSAJE.getKey(), "La solcitud tiene validaciones no cumplidas.");
			response.put(ResponseKeys.ERRORS.getKey(), errors);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		if (menuRolReq.getRol().getIdRol() == null) {
			response.put(ResponseKeys.MENSAJE.getKey(), "No es posble crear el registro sin un rol.idRol");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		if (menuRolReq.getMenu().getIdMenu() == null) {
			response.put(ResponseKeys.MENSAJE.getKey(), "No es posble crear el registro sin un menu.idMenu");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		Rol asignarRol = rolService.buscarPorId(menuRolReq.getRol().getIdRol());
		if (asignarRol == null) {
			response.put(ResponseKeys.MENSAJE.getKey(), "No se enctontro el Rol con rol.idRol: " + menuRolReq.getRol().getIdRol());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		menuRolReq.setRol(asignarRol);
		Menu asignarMenu = menuService.buscarPorId(menuRolReq.getMenu().getIdMenu()).orElse(null);
		if (asignarMenu == null) {
			response.put(ResponseKeys.MENSAJE.getKey(), "No se enctontro el Menu con menu.idMenu: " + menuRolReq.getMenu().getIdMenu());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		menuRolReq.setMenu(asignarMenu);
		try {
			response.put(ResponseKeys.REGISTRO.getKey(), menuRolService.guardar(menuRolReq));
			response.put(ResponseKeys.MENSAJE.getKey(), "Se ha registrado el MenuRol");
			response.put(ResponseKeys.ESTADO.getKey(), true);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			String msg = "No se pudo completar el registro. Detalle: "
					+ (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage());
			log.error(msg);
			response.put(ResponseKeys.MENSAJE.getKey(), msg);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@Secured("ROLE_ADMIN")
	@PutMapping
	public ResponseEntity<Map<String, Object>> actualizar(@RequestBody @Valid MenuRol menuRolReq, BindingResult result) {
		Map<String, Object> response = new HashMap<>();
		response.put(ResponseKeys.ESTADO.getKey(), false);
		response.put(ResponseKeys.REGISTRO.getKey(), null);
		response.put(ResponseKeys.ERRORS.getKey(), null);

		if (menuRolReq.getIdMenuRol() == null)
			result.rejectValue("idMenuRol", ResponseKeys.ERROR.getKey(), Mensajes.NO_NULO);

		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream().map(e -> e.getField() + ": " + e.getDefaultMessage())
					.collect(Collectors.toList());
			response.put(ResponseKeys.MENSAJE.getKey(), "La solcitud tiene validaciones no cumplidas.");
			response.put(ResponseKeys.ERRORS.getKey(), errors);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		MenuRol oldMenuRol = menuRolService.buscarPorId(menuRolReq.getIdMenuRol()).orElse(menuRolReq);
		if (oldMenuRol == null) {
			response.put(ResponseKeys.MENSAJE.getKey(),
					"No se enctontro el MenuRol para actualizar idMenuRol: " + menuRolReq.getIdMenuRol());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		if (menuRolReq.getRol() != null) {
			if (menuRolReq.getRol().getIdRol() == null) {
				response.put(ResponseKeys.MENSAJE.getKey(), "No es posble crear el registro sin un rol.idRol");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}
			Rol asignarRol = rolService.buscarPorId(menuRolReq.getRol().getIdRol());
			if (asignarRol == null) {
				response.put(ResponseKeys.MENSAJE.getKey(), "No se enctontro el Rol con rol.idRol: " + menuRolReq.getRol().getIdRol());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
			oldMenuRol.setRol(asignarRol);
		}
		if (menuRolReq.getMenu() != null) {
			if (menuRolReq.getMenu().getIdMenu() == null) {
				response.put(ResponseKeys.MENSAJE.getKey(), "No es posble crear el registro sin un menu.idMenu");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}
			Menu asignarMenu = menuService.buscarPorId(menuRolReq.getMenu().getIdMenu()).orElse(null);
			if (asignarMenu == null) {
				response.put(ResponseKeys.MENSAJE.getKey(), "No se enctontro el Menu con menu.idMenu: " + menuRolReq.getMenu().getIdMenu());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
			oldMenuRol.setMenu(asignarMenu);
		}

		try {
			response.put(ResponseKeys.REGISTRO.getKey(), menuRolService.guardar(oldMenuRol));
			response.put(ResponseKeys.MENSAJE.getKey(), "Se ha actualizado el MenuRol");
			response.put(ResponseKeys.ESTADO.getKey(), true);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			String msg = "No se pudo completar el registro. Detalle: "
					+ (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage());
			log.error(msg);
			response.put(ResponseKeys.MENSAJE.getKey(), msg);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@Secured("ROLE_ADMIN")
	@DeleteMapping
	public ResponseEntity<Map<String, Object>> eliminar(@RequestParam Integer idMenuRol) {
		Map<String, Object> response = new HashMap<>();
		if (idMenuRol == null) {
			response.put(ResponseKeys.MENSAJE.getKey(), "El parametro idMenuRol es necesario para la operación");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		MenuRol oldMenuRol = menuRolService.buscarPorId(idMenuRol).orElse(null);
		if (oldMenuRol == null) {
			response.put(ResponseKeys.MENSAJE.getKey(), "No se encuentran el MenuRol para eliminar con idMenuRol: " + idMenuRol);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		if (Boolean.TRUE.equals(menuRolService.eliminarPorId(idMenuRol))) {
			response.put(ResponseKeys.ESTADO.getKey(), true);
			response.put(ResponseKeys.MENSAJE.getKey(), "Se ha eliminado el MenuRol: " + idMenuRol);
			return ResponseEntity.ok(response);
		} else {
			response.put(ResponseKeys.MENSAJE.getKey(), "No pudo completar la operacion de eliminación");
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
		}
	}
}
