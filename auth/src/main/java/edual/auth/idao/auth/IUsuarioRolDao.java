package edual.auth.idao.auth;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edual.auth.entity.auth.UsuarioRol;
import edual.auth.entity.auth.id.IdUsuarioRol;

public interface IUsuarioRolDao extends JpaRepository<UsuarioRol, IdUsuarioRol> {

  @Modifying
  @Query(value = "UPDATE UsuarioRol ur SET eliminado = 1 "
      + "WHERE ur.id.idUsuario = :#{#id.idUsuario} AND ur.id.idRol = :#{#id.idRol}")
  public void eliminarPorId(@Param(value = "id") IdUsuarioRol id);

  @Query(value = "SELECT ur FROM UsuarioRol ur "
      + "WHERE ur.eliminado = 0 AND ur.id.idUsuario = :idUsuario AND ur.fechaVencimiento >= current_timestamp ORDER BY ur.id.idRol ASC")
  public List<UsuarioRol> buscarPorIdUsuario(Integer idUsuario);

  @Query(value = "SELECT ur FROM UsuarioRol ur "
      + "WHERE ur.eliminado = 0 AND ur.id.idUsuario = :idUsuario ORDER BY ur.id.idRol ASC")
  public List<UsuarioRol> buscarPorIdUsuarioTodo(Integer idUsuario);
}
