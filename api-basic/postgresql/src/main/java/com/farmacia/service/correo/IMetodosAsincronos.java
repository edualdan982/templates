package com.farmacia.service.correo;

import com.farmacia.entity.auth.Usuario;

public interface IMetodosAsincronos {

	void notificarErrorSistema(String detalle, String sistema);

	void mandarCorreoVerificacion(Usuario nuevoUsuario, Boolean reenviarCorreo, String clave);

	void recuperarClave(Usuario nuevoUsuario, String clave);

	Boolean recuperarClavePhone(String usuario, String telefono, String clave);

	Boolean mandarMensajeVerificacion(String telefono, String usuario, String claveWeb);
}
