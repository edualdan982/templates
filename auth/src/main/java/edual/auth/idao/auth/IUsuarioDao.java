package edual.auth.idao.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import edual.auth.entity.auth.Usuario;

public interface IUsuarioDao extends JpaRepository<Usuario, Integer> {

  @Query(value = "SELECT u FROM Usuario u WHERE u.username = :username AND u.eliminado = 0")
  Usuario findByUsuario(String username);

  @Query("select u from Usuario u where u.username=?1 and u.estado = 1 AND u.eliminado = 0")
  Usuario buscarPorUsuario(String username);

  @Query("SELECT u FROM Usuario u WHERE u.username = ?1 AND u.estado >= ?2")
  Optional<Usuario> buscarPorUsuario(String username, Boolean estado);

  @Query("SELECT u FROM Usuario u JOIN FETCH u.roles r WHERE u.username = :username AND (r.eliminado = 0 AND r.id.idUsuario = u.idUsuario)")
  Optional<Usuario> buscarPorUsuarioFetchRole(String username);

  @Query(value = "SELECT u FROM Usuario u WHERE u.eliminado = 0 ORDER BY u.idUsuario DESC")
  List<Usuario> listarUsuarios();

  @Query(value = " SELECT NVL(COUNT(*),0) FROM Usuario WHERE username = :username")
  Integer contarUsuario(String username);

  @Modifying
  @Query(value = "UPDATE Usuario SET eliminado = 1,username=CONCAT(username,'-',idUsuario), fechaModificacion = current_timestamp WHERE idUsuario = :idUsuario")
  void eliminarPorId(Integer idUsuario);

  @Modifying
  @Query(value = "UPDATE Usuario SET estado = 0, fechaModificacion = current_timestamp WHERE username = :username")
  void desactivarPorUsuario(String username);

  @Modifying
  @Query(value = "UPDATE Usuario SET password = :encode, fechaModificacion = current_timestamp WHERE idUsuario = :idUsuario")
  void actualizarPassword(String encode, Integer idUsuario);

  @Query(value = "SELECT u FROM Usuario u WHERE u.eliminado = 0 AND u.claveWeb =:claveWeb")
  Usuario buscarPorClaveWeb(String claveWeb);

  @Modifying
  @Query(value = "UPDATE Usuario SET estado = :estado, fechaModificacion = current_timestamp WHERE idUsuario = :idUsuario")
  void cambiarEstado(Integer idUsuario, Boolean estado);

  @Query(value = "SELECT u FROM Usuario u WHERE u.eliminado = 0 AND " +
      "(:numeroDoc is null or lower(u.persona.numeroDoc) LIKE lower(concat('%', :numeroDoc, '%'))) " +
      "and (:username is null or lower(u.username ) LIKE lower(concat('%', :username, '%'))) " +
      "and (:nombres is null or lower( u.persona.apePaterno||u.persona.apeMaterno||u.persona.nombres ) LIKE lower(concat('%', :nombres, '%'))) "
      +
      "order by u.idUsuario DESC")
  Page<Usuario> listarPaginado(Pageable pageable, String numeroDoc, String username, String nombres);

  @Modifying
  @Query("UPDATE Usuario u SET u.password = :clave WHERE u.idUsuario = :idUsuario AND u.eliminado = 0")
  void cambiarClave(Integer idUsuario, String clave);

  @Query("SELECT u.idUsuario FROM Usuario u WHERE u.username = :username AND u.eliminado = 0")
  Integer obtenerIdUsuario(String username);

}
