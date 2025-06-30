package edual.auth.service.auth;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import edual.auth.entity.auth.Usuario;

public interface IUsuarioService {

  Usuario buscarPorUsuario(String username);

  Usuario buscarPorUsuario(String username, Boolean estado);

  Usuario buscarPorUsuarioFetchRole(String username);

  Usuario guardar(Usuario usuario);

  Usuario guardar(Usuario usuario, Integer valNombreUsuario);

  List<Usuario> listar();

  Usuario buscarPorId(Integer idUsuario);

  Integer contarUsuario(String usuario);

  Boolean eliminarPorId(Integer idUsuario);

  Boolean desactivarPorUsuario(String usuario);

  Boolean existePorId(Integer idUsuario);

  boolean actualizarPassword(String password, Integer idUsuario);

  Usuario buscarPorClaveWeb(String claveWeb);

  Boolean cambiarEstado(Integer idUsuario, Boolean estado);

  Page<Usuario> listarPaginado(Pageable pageable, String numeroDocumento, String usuario, String nombres);

  Boolean cambiarClave(Integer idUsuario, String clave);

  Boolean actualizarDatos();

  Integer obtenerIdUsuario(String username);

  Page<Usuario> listarUsuariosSinTelefono(Pageable pageable);

  Boolean actualizarTelefono(String telefono, Integer idUsuario);

  Boolean generarCodigoVerificacion(Integer idUsuario, Integer codigoVerificacion);

}
