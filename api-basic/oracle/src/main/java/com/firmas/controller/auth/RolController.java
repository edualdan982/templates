package com.firmas.controller.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

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
import com.firmas.entity.auth.Rol;
import com.firmas.service.auth.IRolService;

@RestController
@RequestMapping("/rol")
@Secured({ "ROLE_ADMIN" })
public class RolController {

	@Autowired
	private IRolService rolService;

	@GetMapping
	public List<Rol> listar() {
		return rolService.listar();
	}

	@GetMapping("/buscarPorId")
	public ResponseEntity<Map<String, Object>> buscarPorId(@RequestParam(required = false) Integer idRol) {
		Map<String, Object> response = new HashMap<>();
		response.put(ResponseKeys.ESTADO.getKey(), false);
		response.put(ResponseKeys.RESPUESTA.getKey(), null);

		if (idRol == null) {
			response.put(ResponseKeys.MENSAJE.getKey(), "El parametro idRol es necesario para la operación.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		Rol rol = rolService.buscarPorId(idRol);
		if (rol == null) {
			response.put(ResponseKeys.MENSAJE.getKey(), "No se ha encontrado coincidencias con el id proporcionado.");
		} else {
			response.put(ResponseKeys.RESPUESTA.getKey(), rol);
			response.put(ResponseKeys.ESTADO.getKey(), true);
			response.put(ResponseKeys.MENSAJE.getKey(), "Se ha encontrado el id.");
		}

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping
	public ResponseEntity<Map<String, Object>> guardar(@Valid @RequestBody Rol rol, BindingResult result) {
		Map<String, Object> response = new HashMap<>();
		response.put(ResponseKeys.ESTADO.getKey(), false);
		response.put(ResponseKeys.REGISTRO.getKey(), null);
		response.put(ResponseKeys.ERRORS.getKey(), null);

		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(err -> err.getField().concat(": ").concat(err.getDefaultMessage()))
					.collect(Collectors.toList());

			response.put(ResponseKeys.ERRORS.getKey(), errors);
			response.put(ResponseKeys.MENSAJE.getKey(), Mensajes.VALID);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		rol.setNombre(rol.getNombre().toUpperCase());

		if (rolService.contarNombre(rol.getNombre()) > 0) {
			response.put(ResponseKeys.MENSAJE.getKey(), "El Nombre-Rol: " + rol.getNombre() + ", ya esta registrado.");

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		if (rol.getNombre().indexOf("ROLE_") > -1) {
			response.put(ResponseKeys.MENSAJE.getKey(), "El nombre del Rol no debe contener 'ROLE_'.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		response.put(ResponseKeys.REGISTRO.getKey(), rolService.guardar(rol));
		response.put(ResponseKeys.MENSAJE.getKey(), "Se ha registro el rol");
		response.put(ResponseKeys.ESTADO.getKey(), true);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping
	public ResponseEntity<Map<String, Object>> actualizar(@Valid @RequestBody Rol rol, BindingResult result) {
		Map<String, Object> response = new HashMap<>();
		response.put(ResponseKeys.ESTADO.getKey(), false);
		response.put(ResponseKeys.REGISTRO.getKey(), null);
		response.put(ResponseKeys.ERRORS.getKey(), null);

		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(err -> err.getField().concat(": ").concat(err.getDefaultMessage()))
					.collect(Collectors.toList());

			response.put(ResponseKeys.ERRORS.getKey(), errors);
			response.put(ResponseKeys.MENSAJE.getKey(), Mensajes.VALID);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		if (rol.getIdRol() == null) {
			response.put(ResponseKeys.MENSAJE.getKey(), "Para actualizar debe enviar un idRol");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		Rol oldRol = rolService.buscarPorId(rol.getIdRol());
		if (oldRol == null) {
			response.put(ResponseKeys.MENSAJE.getKey(), "El rol no se puede editar porque no existe.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		oldRol.setNombre(rol.getNombre().toUpperCase());
		oldRol.setDescripcion(rol.getDescripcion());

		if (rolService.contarNombre(oldRol.getNombre()) > 1) {
			response.put(ResponseKeys.MENSAJE.getKey(), "El Nombre-Rol: " + rol.getNombre() + ", ya esta siendo utilizado.");

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		if (oldRol.getNombre().indexOf("ROLE_") > -1) {
			response.put(ResponseKeys.MENSAJE.getKey(), "El nombre del Rol no debe contener 'ROLE_'.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		response.put(ResponseKeys.REGISTRO.getKey(), rolService.guardar(oldRol));
		response.put(ResponseKeys.MENSAJE.getKey(), "Se ha actualizado el rol");
		response.put(ResponseKeys.ESTADO.getKey(), true);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@DeleteMapping
	public ResponseEntity<Map<String, Object>> eliminar(@RequestParam(required = false) Integer idRol) {
		Map<String, Object> response = new HashMap<>();
		response.put(ResponseKeys.ESTADO.getKey(), false);

		if (idRol == null) {
			response.put(ResponseKeys.MENSAJE.getKey(), "El parametro idRol es necesario para la operación.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		if (Boolean.FALSE.equals(rolService.existePorId(idRol))) {
			response.put(ResponseKeys.MENSAJE.getKey(), "El idRol:" + idRol + ", no existe.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		} else {
			if (Boolean.TRUE.equals(rolService.eliminarPorId(idRol))) {
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
}
