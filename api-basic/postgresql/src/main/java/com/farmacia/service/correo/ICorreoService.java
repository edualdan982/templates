package com.farmacia.service.correo;

import java.io.File;
import java.util.Map;

import com.farmacia.entity.dt.CorreoData;

public interface ICorreoService {

	/**
	 * 
	 * @param correo      [0] correo a utilizar para enviar y [1] la clave generara
	 *                    del correo.
	 * @param datosEnviar [0] correo destino a enviar, [1] asunto que se adjuntara
	 *                    en el correo y [2] mensaje
	 * @return String que describe el proceso que siguio el servicio.
	 */
	String enviarTexto(CorreoData correo, String[] datosEnviar, String[] correos);

	/**
	 * 
	 * @param correo      [0] correo a utilizar para enviar y [1] la clave generara
	 *                    del correo.
	 * @param datosEnviar [0] correo destino a enviar y [1] asunto que se adjuntara
	 *                    en el correo
	 * @param datosEnviar HashMap Object Datos
	 * @return String que describe el proceso que siguio el servicio.
	 */
	String enviarTextoAndObjeto(CorreoData correo, String[] datosEnviviar, Map<String, File> files);
}
