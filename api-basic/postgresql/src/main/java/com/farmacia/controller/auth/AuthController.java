package com.farmacia.controller.auth;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.farmacia.config.constantes.Mensajes;
import com.farmacia.config.constantes.ResponseKeys;
import com.farmacia.entity.auth.TypeOperacionAuth;
import com.farmacia.entity.auth.Usuario;
import com.farmacia.entity.auth.hist.HistEvento;
import com.farmacia.service.auth.IUsuarioService;
import com.farmacia.service.auth.hist.IHistEventoService;
import com.farmacia.service.util.IHttpServiceUtil;
import com.farmacia.service.util.IJwtAuthServiceUtil;
import com.farmacia.util.proceso.BuildMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private static final Logger log = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private TokenStore tokenStore;
	@Autowired
	private IUsuarioService usuarioService;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private IHttpServiceUtil httpServiceUtil;
	@Autowired
	private IHistEventoService histEventoService;
	@Autowired
	private IJwtAuthServiceUtil jwtAuthServiceUtil;

	@Autowired
	ResourceServerTokenServices resourceServerTokenServices;
	@Resource(name = "tokenServices")
	ConsumerTokenServices tokenServices;

	private WebResponseExceptionTranslator<OAuth2Exception> exceptionTranslator = new DefaultWebResponseExceptionTranslator();

	public void setExceptionTranslator(WebResponseExceptionTranslator<OAuth2Exception> exceptionTranslator) {
		this.exceptionTranslator = exceptionTranslator;
	}

	@GetMapping(value = "/check-token")
	public ResponseEntity<Map<String, Object>> checkToken(@RequestHeader("Authorization") String authorizationHeader) {

		Map<String, Object> response = new HashMap<>();
		response.put("active", false);
		response.put("authentication", null);
		response.put("user", null);
		response.put("token", null);

		String tokenValue = authorizationHeader.replace("Bearer", "").trim();
		OAuth2AccessToken token = resourceServerTokenServices.readAccessToken(tokenValue);

		if (token == null)
			throw new InvalidTokenException("No se reconoció el token");

		if (token.isExpired()) {
			response.put(ResponseKeys.MENSAJE.getKey(), "El token ha expirado");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}

		OAuth2Authentication authentication = resourceServerTokenServices.loadAuthentication(token.getValue());
		response.put("active", true);

		JsonNode node = objectMapper.valueToTree(authentication.getPrincipal());

		Usuario usuario = usuarioService.buscarPorUsuarioFetchRole(node.get("username").asText());
		// usuario.setContactos(null);
		response.put("authentication", node);
		response.put("user", usuario);
		response.put(ResponseKeys.MENSAJE.getKey(), "Token valido");
		response.put("token", tokenValue);

		return ResponseEntity.ok(response);
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<OAuth2Exception> handleException(Exception e) throws Exception {
		log.info("Error de manejo: " + e.getClass().getSimpleName() + ", " + e.getMessage());
		InvalidTokenException e400 = new InvalidTokenException(e.getMessage()) {
			@Override
			public int getHttpErrorCode() {
				return 400;
			}
		};
		return exceptionTranslator.translate(e400);
	}

	@Secured({ "ROLE_ADMIN" })
	@GetMapping(value = "/tokens")
	public ResponseEntity<Map<String, Object>> getTokens(@RequestParam(required = false) String clientId) {
		Map<String, Object> response = new HashMap<>();
		response.put(ResponseKeys.ESTADO.getKey(), false);
		log.info("************LISTANDO LOS TOKENS************");
		if (clientId == null) {
			response.put(ResponseKeys.MENSAJE.getKey(), "El paramatro clientId es necesario para la solicitud");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		if (clientId.isEmpty()) {

			response.put(ResponseKeys.MENSAJE.getKey(), "El paramatro clientId es longitud 0");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientId(clientId);
		if (tokens == null)
			tokens = new ArrayList<>();
		log.info("Cantidad de tokens: {}", tokens.size());
		if (tokens.isEmpty()) {
			response.put(ResponseKeys.MENSAJE.getKey(), "No se encontro ningun token al listar");
		} else
			response.put(ResponseKeys.ESTADO.getKey(), true);
		response.put("tokens", tokens);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@DeleteMapping(value = "/invalidar")
	public ResponseEntity<Map<String, Object>> invalidarToken(
			@RequestParam(required = false, defaultValue = "false") Boolean desactivar,
			@RequestHeader(required = false, name = "Authorization") String authorizationToken,
			@RequestParam(required = false, defaultValue = "false") Boolean invalidarTodo,
			@RequestParam(required = false) String clientIp) {
		Map<String, Object> response = new HashMap<>();
		response.put(ResponseKeys.ESTADO.getKey(), false);
		response.put("jti", null);

		if (authorizationToken == null) {
			response.put(ResponseKeys.MENSAJE.getKey(), "Envie en las cabeceras el dato: Authorization: Bearer JWT");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		String[] tokenSeparado = authorizationToken.split("\\.");
		if (tokenSeparado.length != 3) {
			response.put(ResponseKeys.MENSAJE.getKey(),
					"El token no cumple con las caracteristicas de tener 3 partes asociativas.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		Base64.Decoder decoder = Base64.getUrlDecoder();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> objectToken = new HashMap<>();

		try {
			objectToken = mapper.readValue(new String(decoder.decode(tokenSeparado[1])), HashMap.class);
		} catch (JsonProcessingException e) {
			log.warn("No se puedo convertir el token a objeto.");
			log.warn("Detalle: "
					+ (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
			e.printStackTrace();

			response.put(ResponseKeys.MENSAJE.getKey(),
					"El token no es valido para una conversión correcta, revise los datos.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		if (objectToken.get("user_name") == null) {
			response.put(ResponseKeys.MENSAJE.getKey(),
					"El token no tiene inmerso un user_name, por tanto no es un token valido");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		String userName = objectToken.get("user_name").toString();
		Integer idUsuario = objectToken.get("idUsuario") == null ? null
				: Integer.valueOf(objectToken.get("idUsuario").toString());

		if (objectToken.get("client_id") == null) {
			response.put(ResponseKeys.MENSAJE.getKey(),
					"El token no tiene inmerso un client_id, por tanto no es un token valido");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		String clientId = objectToken.get("client_id").toString();
		log.info("Token perteniciente a client_id: {}", clientId);
		log.info("Token perteniciente a user_name: {}", userName);

		Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientIdAndUserName(clientId, userName);
		if (tokens == null)
			tokens = new ArrayList<>();

		log.info("Cantidad de tokens: {}", tokens.size());
		if (tokens.isEmpty()) {
			response.put(ResponseKeys.ESTADO.getKey(), false);
			response.put(ResponseKeys.MENSAJE.getKey(),
					"No se encontraron tokens para invalidar con el client_id: " + clientId
							+ "y username: " + userName);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		} else {
			final String jti = objectToken.get("jti").toString();
			if (Boolean.TRUE.equals(invalidarTodo)) {
				tokens.stream().forEach(tk -> tokenServices.revokeToken(tk.getValue()));
				response.put("jti",
						tokens.stream().map(tk -> tk.getAdditionalInformation().get("jti").toString() + ", "));
			} else {
				tokens.stream().forEach(tk -> {
					if (jti.equals(tk.getAdditionalInformation().get("jti").toString())) {
						tokenServices.revokeToken(tk.getValue());
					}
				});
			}
			response.put("jti", jti);
			if (Boolean.TRUE.equals(desactivar)) {
				Boolean resulOperacion = usuarioService.desactivarPorUsuario(userName);
				log.info("USUARIO DESACTIVADO: {}", resulOperacion);
				response.put(ResponseKeys.MENSAJE.getKey(),
						String.format("Se ha revocado los token del username: %s, la desactivación del usuario fue: %s",
								userName, (Boolean.TRUE.equals(resulOperacion) ? "EXISTOSA" : "FALLIDA")));
			} else
				response.put(ResponseKeys.MENSAJE.getKey(), "Se ha revocado los token del username: " + userName);
			response.put(ResponseKeys.ESTADO.getKey(), true);

			HttpServletRequest request = httpServiceUtil.getContextServerlet();

			HistEvento histEvento = new HistEvento(clientIp, request.getRemoteAddr(), authorizationToken, TypeOperacionAuth.LOGOUT, jti);
			histEvento.setUsername(userName);
			histEvento.setIdUsuario(idUsuario);
			histEvento = histEventoService.guardar(histEvento);
			log.info("Se ha guardado el evento de invalidación de token: {}", userName);

		}

		log.info("Se ha invalido los tokens del usuario: {}", userName);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/decode")
	public ResponseEntity<Map<String, Object>> decodificarToken(@RequestParam(required = false) String token) {
		Map<String, Object> response = new HashMap<>();
		response.put(ResponseKeys.ESTADO.getKey(), false);

		String[] tokenSeparado = token.split("\\.");
		if (token.length() < 3) {
			response.put(ResponseKeys.MENSAJE.getKey(),
					"El token no cumple con las caracteristicas de tener 3 partes asociativas.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		Base64.Decoder decoder = Base64.getUrlDecoder();

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> objectToken = new HashMap<>();
		try {

			objectToken = mapper.readValue(new String(decoder.decode(tokenSeparado[1])), HashMap.class);
		} catch (JsonProcessingException e) {
			log.warn("No se puedo convertir el token a objeto.");
			log.warn("Detalle: "
					+ (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
			e.printStackTrace();

			response.put(ResponseKeys.MENSAJE.getKey(),
					"El token no es valido para una conversión correcta, revise los datos.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		response.put(ResponseKeys.ESTADO.getKey(), true);
		response.put(ResponseKeys.RESPUESTA.getKey(), objectToken);

		return ResponseEntity.ok(response);
	}

	@Secured({ "ROLE_ADMIN" })
	@DeleteMapping(value = "/eliminarTokens")
	public ResponseEntity<Map<String, Object>> eliminarTokensUsuario(
			@RequestHeader(required = false, name = "Authorization") String authorization,
			@RequestParam(required = true) String clientId,
			@RequestParam(required = true) String username,
			@RequestParam(required = false) String clientIp) {
		Map<String, Object> response = BuildMap.initMap();

		Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientIdAndUserName(clientId, username);
		if (tokens == null)
			tokens = new ArrayList<>();

		log.info("Cantidad de tokens: {}", tokens.size());
		if (tokens.isEmpty()) {
			response.put(ResponseKeys.ESTADO.getKey(), false);
			response.put(ResponseKeys.MENSAJE.getKey(),
					"No se encontraron tokens para invalidar con el client_id: " + clientId
							+ "y username: " + username);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		} else {
			tokens.stream().forEach(tk -> tokenServices.revokeToken(tk.getValue()));
			response.put(ResponseKeys.ESTADO.getKey(), true);
			response.put(ResponseKeys.MENSAJE.getKey(), "Se ha revocado los token del username: " + username);

			Map<String, Object> dataJwt = new HashMap<>();
			try {
				dataJwt = jwtAuthServiceUtil.decodeJwt(authorization);
				if (dataJwt.isEmpty()) {
					response.put(ResponseKeys.ESTADO.getKey(), false);
					response.put(ResponseKeys.MENSAJE.getKey(), "No se ha podido leer el token o el token es invalido");
					return ResponseEntity.badRequest().body(response);
				}
			} catch (Exception e) {
				log.error("No se ha podido leer los datos del token. Detalle: {}",
						(e.getLocalizedMessage() == null ? "Sin detalle" : e.getLocalizedMessage()));
			}

			String msg = String.format("Se ha invalidado todos los tokens(%s) del usuario por", clientId,
					dataJwt.get("user_name"));
			HttpServletRequest request = httpServiceUtil.getContextServerlet();
			HistEvento histEvento = new HistEvento(clientIp, request.getRemoteAddr(), authorization,
					TypeOperacionAuth.INVALID_SESSION, msg);

			histEvento.setUsername(username);
			histEvento.setIdUsuario(usuarioService.obtenerIdUsuario(username));
			histEvento = histEventoService.guardar(histEvento);
			log.info("Se ha guardado el evento de invalidado los tokens del usuario: {}", username);
		}
		log.info("Se ha invalido los tokens del usuario: {}", username);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
