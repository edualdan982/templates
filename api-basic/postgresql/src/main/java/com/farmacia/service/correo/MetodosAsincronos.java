package com.farmacia.service.correo;

import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.farmacia.entity.auth.TypeAccount;
import com.farmacia.entity.auth.Usuario;
import com.farmacia.service.dt.ICorreoDataService;
import com.farmacia.service.util.IFechaServiceUtil;
import com.farmacia.util.proceso.FechaConverter;

@Service
public class MetodosAsincronos implements IMetodosAsincronos {
	private static final Logger log = LoggerFactory.getLogger(MetodosAsincronos.class);

	private static final String FORMATO_FECHA = "dd/MM/yyyy HH:mm:ss";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	@Autowired
	private IMensajeriaService mensajeriaService;
	@Autowired
	private IFechaServiceUtil fechaServiceUtil;
	@Autowired
	private ICorreoService correoService;

	@Autowired
	private ICorreoDataService correoDataService;

	@Value("${aplicacion.url}")
	private String APP_URL;
	@Value("${server.port}")
	private String PORT;

	@Value("${spring.profiles.active}")
	private String ENTORNO;
	@Value("${server.servlet.context-path}")
	private String CONTEXT_API;

	@Async
	@Override
	public void notificarErrorSistema(String detalle, String sistema) {
		log.info("Ejecutando en segundo plano: " + Thread.currentThread().getName());
		String fechaEmision = "";
		try {
			fechaEmision = FechaConverter.convertirFecha(new Date(), "dd/MM/yyyy HH:mm:ss");
		} catch (NullPointerException | IllegalArgumentException e) {
			fechaEmision = new Date().toString();
			e.printStackTrace();
		}
		String mensaje = String.join(System.getProperty("line.separator"),
				"<table role=\"presentation\" style=\"background-color:#e9e9e6;\">\n"
						+ "<tbody><tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>\n"
						+ "<div style=\"text-align: justify; background-color:#FFFFFF;padding-left: 5px;padding-right: 5px;padding-top: 5px;padding-bottom: 5px;\">\n"
						+ "<p style=\"font-size: larger;\"><b>Se produjo una eventualidad </b>" + fechaEmision
						+ "</p><p style=\"font-size: medium;\">Eventualidad producido en el servicio o sistema: <br>"
						+ sistema + "</p><p><b>Detalle: </b><p>" + detalle
						+ "</p><p></b>Consultas: sysfinancieros@umsa.bo</p></div></td>\n"
						+ "<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>\n" + "</tr>\n" + "</tbody>\n" + "</table>");

		String resCorreo = correoService.enviarTexto(correoDataService.buscarPorEtorno(),
				new String[] { null, "Notificacion de error: " + sistema, mensaje },
				new String[] { "edsarmiento1@umsa.bo" });
		log.info(resCorreo);
	}

	@Async
	@Override
	public void mandarCorreoVerificacion(Usuario nuevoUsuario, Boolean reenviarCorreo, String clave) {
		log.info("Ejecutando en segundo plano: " + Thread.currentThread().getName());
		String fechaRegistro = "";
		try {
			fechaRegistro = fechaServiceUtil.convertirFecha(new Date(), FORMATO_FECHA);
		} catch (ParseException e) {
			fechaRegistro = new Date().toString();
			e.printStackTrace();
		}
		String urlVerificacion = "";

		if (ENTORNO.equals("dev"))
			urlVerificacion = String.format("%s:%s%s/open/activarUsuario?claveWeb=%s", APP_URL, PORT, CONTEXT_API,
					nuevoUsuario.getClaveWeb());
		else
			urlVerificacion = String.format("%s%s/open/activarUsuario?claveWeb=%s", APP_URL, CONTEXT_API,
					nuevoUsuario.getClaveWeb());

		String mensaje = String.join(LINE_SEPARATOR,
				"<div style=\"text-align: justify; background-color:#FFFFFF;padding-left: 5px;padding-right: 5px;padding-top: 5px;padding-bottom: 5px;\">",
				"<table role=\"presentation\" style=\"background-color:#e9e9e6;\"><tbody><tr><td>",
				Boolean.TRUE.equals(reenviarCorreo) ? "<p style=\"font-size: larger;\"><b> ACTIVACIÓN DE CUENTA</b></p>"
						: "<p style=\"font-size: larger;\"><b> REGISTRO DE USUARIO</b></p>",
				"<table role=\"presentation\" style=\"background-color:#e9e9e6;\"><tbody><tr><td>",
				nuevoUsuario.getTipo().equals(TypeAccount.INST)
						? "<p><b>Tipo de cuenta</b> Cuenta institucional, si no recuerda acceda a <a href=\"https://usuarios.umsa.bo\"> PRESIONE AQUI</a></p>"
						: "<p><b>Tipo de cuenta: </b> Cuenta local del sistema</p>",
				Boolean.TRUE.equals(reenviarCorreo)
						? "<p>Para poder acceder a su cuenta se reenvio este correo y procededer en la activación de la cuenta: <br></p>"
						: "<p>En fecha y hora: " + fechaRegistro
								+ "</p><p>Su registro fue realizo con exito.<br></p>"
								+ "<p>La UMSA agredece por registrarse en nuestro sistema, puede acceder con el USUARIO y CONTRASEÑA que registro. <br></p>",
				"<p><b>&nbsp;USUARIO</b>: " + nuevoUsuario.getUsername(),
				Boolean.TRUE.equals(reenviarCorreo) ? "" : "<br><b>&nbsp;CLAVE</b>: " + clave,
				"</p><br></td></tr><tr><td><div style=\"border: 5px outset #00A4BD; text-align: center;\">",
				"<p style=\"font-size: medium;\"> Para activar su cuenta presione el botón: </p>",
				"<p><a style='padding: 4px 25px;background: rgb(95, 158, 160);border: 1px solid #1161B0;color: #fff;border-radius: 4px;text-decoration:none;' ",
				"href=\"" + urlVerificacion + "\">PRESIONE AQUI</a></p></div></td></tr>",
				"<tr><td><p></b>Consultas: sysfinancieros@umsa.bo</p></td></tr></tbody></table></div>");

		String resCorreo = correoService.enviarTexto(correoDataService.buscarPorEtorno(),
				new String[] { null, "Activación de cuenta - Sistema Zodiaco Acuario", mensaje },
				!nuevoUsuario.getTipo().equals(TypeAccount.INST) ? new String[] { nuevoUsuario.getCorreo() }
						: new String[] { nuevoUsuario.getCorreo(), nuevoUsuario.getUsername() + "@umsa.bo" });
		log.info(resCorreo);
	}

	@Async
	@Override
	public void recuperarClave(Usuario usuario, String clave) {
		log.info("Ejecutando en segundo plano: " + Thread.currentThread().getName());
		String fechaRegistro = "";
		try {
			fechaRegistro = fechaServiceUtil.convertirFecha(new Date(), FORMATO_FECHA);
		} catch (ParseException e) {
			fechaRegistro = new Date().toString();
			e.printStackTrace();
		}
		String mensaje = String.join(LINE_SEPARATOR,
				"<div style=\"text-align: justify; background-color:#FFFFFF;padding-left: 5px;padding-right: 5px;padding-top: 5px;padding-bottom: 5px;\">",
				"<table role=\"presentation\" style=\"background-color:#e9e9e6;\"><tbody><tr><td>",
				"<p style=\"font-size: larger;\"><b> RECUPERACIÓN DE CLAVE</b></p>",
				"<p>En fecha y hora: " + fechaRegistro
						+ "</p><p>Se ha restablecido su clave, si usted no ha realizado este proceso por favor comuniquese al correo de soporte técnico.<br></p>"
						+ (usuario.getTipo().equals(TypeAccount.INST)
								? "<p><b>NOTA.-<b/> Para poder restablecer la clave de una cuenta institucional dirigase a: <a href=\"https://usuarios.umsa.bo\">USUARIO-UMSA</a><br></p>"
								: "")
						+ "<p>Sus datos para el acceso con su nueva clave son: <br></p>",
				"<p><b>&nbsp;USUARIO</b>: " + usuario.getUsername(), "<br><b>&nbsp;CLAVE</b>: " + clave,
				"</p><br></td></tr>",
				"<tr><td><p></b>Consultas: sysfinancieros@umsa.bo</p></td></tr></tbody></table></div>");

		String resCorreo = correoService.enviarTexto(correoDataService.buscarPorEtorno(),
				new String[] { null, "Recuperación de clave - Sistema Zodiaco Acuario", mensaje },
				new String[] { usuario.getCorreo() });
		log.info(resCorreo);
	}

	@Override
	public Boolean recuperarClavePhone(String usuario, String telefono, String clave) {
		String mensajeEnviar = String.format(
				"Se ha restablecido su clave del usuario *%s*, Se recomienda cambiarla una vez ingrese al sistema. Su nueva clave de acceso:",
				usuario);

		Integer numeroTelefono = 0;
		try {
			numeroTelefono = Integer.parseInt(telefono);
		} catch (NumberFormatException e) {
			log.error("Error al convertir el número de teléfono: " + telefono, e);
			return false;
		} catch (Exception e) {
			log.info("El número de teléfono no es válido: " + telefono);
		}
		mensajeriaService.mandarMensaje(591, numeroTelefono, mensajeEnviar);
		return mensajeriaService.mandarMensaje(591, numeroTelefono, clave);
	}

	@Override
	public Boolean mandarMensajeVerificacion(String telefono, String usuario, String claveWeb) {
		String urlVerificacion = String.format("%s%s/open/activarUsuario?claveWeb=%s", APP_URL, CONTEXT_API,
				claveWeb);
		String mensajeEnviar = String.format(
				"*ACTIVACIÓN DE CUENTA - ACUARIO* \n Usuario:\t *%s*;\n \uD83D\uDC49 Para poder acceder a su cuenta y procededer en la activación de la cuenta. \uD83D\uDC48",
				usuario, urlVerificacion);

		Integer numeroTelefono = 0;
		try {
			numeroTelefono = Integer.parseInt(telefono);
		} catch (NumberFormatException e) {
			log.error("Error al convertir el número de teléfono: " + telefono, e);
			return false;
		} catch (Exception e) {
			log.info("El número de teléfono no es válido: " + telefono);
		}
		mensajeriaService.mandarMensaje(591, numeroTelefono, mensajeEnviar);
		return mensajeriaService.mandarMensaje(591, numeroTelefono, urlVerificacion);

	}
}
