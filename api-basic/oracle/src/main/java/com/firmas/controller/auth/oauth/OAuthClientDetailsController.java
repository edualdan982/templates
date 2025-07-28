package com.firmas.controller.auth.oauth;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.firmas.config.constantes.Mensajes;
import com.firmas.config.constantes.ResponseKeys;
import com.firmas.entity.auth.oauth.ClientDetail;
import com.firmas.service.auth.oauth.IClientDetailService;
import com.firmas.service.util.IClaveValServiceUtil;

@RestController
@RequestMapping("/clientDetails")
public class OAuthClientDetailsController {
  private static final Logger log = LoggerFactory.getLogger(OAuthClientDetailsController.class);

	@Autowired
	private IClientDetailService clientDetailServicie;
	@Autowired
	private IClaveValServiceUtil utilClaveService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@GetMapping
	public List<ClientDetail> listar() {
		return clientDetailServicie.listar();
	}

	@PostMapping
	public ResponseEntity<Map<String, Object>> guardar(@RequestBody @Valid ClientDetail clientDetails,
			BindingResult result) {
		Map<String, Object> response = new HashMap<>();
		response.put(ResponseKeys.ESTADO.getKey(), false);
		response.put(ResponseKeys.REGISTRO.getKey(), null);

		Optional<String> valClave = utilClaveService.validarClave(clientDetails.getClientSecret(), (byte) 2);
		if (valClave.isPresent())
			result.rejectValue("clientSecret", ResponseKeys.ERROR.getKey(), valClave.get());

		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(err -> err.getField() + ": " + err.getDefaultMessage()).collect(Collectors.toList());
			response.put(ResponseKeys.ERRORS.getKey(), errors);
			response.put(ResponseKeys.MENSAJE.getKey(), "Se han encontrado validaciones no cumplidades en la solicitud");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		try {
			response.put(ResponseKeys.REGISTRO.getKey(), clientDetailServicie.guardar(clientDetails));
			response.put(ResponseKeys.MENSAJE.getKey(), "Se ha registrado con exito");
			response.put(ResponseKeys.ESTADO.getKey(), true);

			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			log.error("Ha ocurrido un error al tratar de persitir la entidad OAuthClientDetails");
			response.put(ResponseKeys.MENSAJE.getKey(), "Error al persistir en la BD. Detalle: "
					+ (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@PutMapping
	public ResponseEntity<Map<String, Object>> actualizar(@Valid @RequestBody ClientDetail clientDetails,
			BindingResult result,
			@RequestParam(required = false, defaultValue = "false") Boolean actClave) {
		Map<String, Object> response = new HashMap<>();
		response.put(ResponseKeys.ESTADO.getKey(), false);
		response.put(ResponseKeys.REGISTRO.getKey(), null);
		if (Boolean.TRUE.equals(actClave)) {
			Optional<String> valClave = utilClaveService.validarClave(clientDetails.getClientSecret(), (byte) 2);
			if (valClave.isPresent())
				result.rejectValue("password", ResponseKeys.ERROR.getKey(), valClave.get());
		}
		if (result.hasErrors()) {
			List<String> errors = result.getFieldErrors().stream()
					.map(err -> err.getField() + ": " + err.getDefaultMessage()).collect(Collectors.toList());
			response.put(ResponseKeys.ERRORS.getKey(), errors);
			response.put(ResponseKeys.MENSAJE.getKey(), "Se han encontrado validaciones no cumplidades en la solicitud");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		ClientDetail oldClientDetails = clientDetailServicie.buscarPorId(clientDetails.getClientId());

		if (oldClientDetails == null) {
			response.put(ResponseKeys.MENSAJE.getKey(),
					"No existe el clientDetails para actualizar, con clientId: " + clientDetails.getClientId());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		oldClientDetails.setAccessTokenValidity(clientDetails.getAccessTokenValidity());
		oldClientDetails.setAdditionalInformation(clientDetails.getAdditionalInformation());
		oldClientDetails.setAuthorities(clientDetails.getAuthorities());
		oldClientDetails.setAuthorizedGrantTypes(clientDetails.getAuthorizedGrantTypes());
		oldClientDetails.setAutoapprove(clientDetails.getAutoapprove());
		if (Boolean.TRUE.equals(actClave))
			oldClientDetails.setClientSecret(passwordEncoder.encode(clientDetails.getClientSecret()));
		oldClientDetails.setRefreshTokenValidity(clientDetails.getRefreshTokenValidity());
		oldClientDetails.setResourceIds(clientDetails.getResourceIds());
		oldClientDetails.setScope(clientDetails.getScope());
		oldClientDetails.setWebServerRedirectUri(clientDetails.getWebServerRedirectUri());
		try {
			response.put(ResponseKeys.REGISTRO.getKey(), clientDetailServicie.guardar(oldClientDetails));
			response.put(ResponseKeys.MENSAJE.getKey(), "Se ha actualiado con exito");
			response.put(ResponseKeys.ESTADO.getKey(), false);

			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			log.error("Ha ocurrido un error al tratar de persitir la entidad OAuthClientDetails");
			response.put(ResponseKeys.MENSAJE.getKey(), "Error al persistir en la BD. Detalle: "
					+ (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
