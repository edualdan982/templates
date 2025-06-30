package edual.auth.idao.auth;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import edual.auth.entity.auth.UsuarioClientDetails;
import edual.auth.entity.auth.id.IdUSuarioClientDetails;

public interface IUsuarioClientDetailDao extends JpaRepository<UsuarioClientDetails, IdUSuarioClientDetails> {

  @Query(value = "SELECT ucd FROM UsuarioClientDetails ucd WHERE eliminado = 0 AND ucd.id.idUsuario = :idUsuario ")
  public List<UsuarioClientDetails> buscarPorIdUsuario(Integer idUsuario);

  @Query(value = "SELECT NVL(COUNT(ucd), 0) FROM UsuarioClientDetails ucd WHERE ucd.id.idUsuario = :idUsuario AND ucd.id.clientId = :clientId AND ucd.fechaVencimiento >= current_timestamp")
  public Integer contarUsuarioClientId(Integer idUsuario, String clientId);

  @Query(value = "SELECT ucd FROM UsuarioClientDetails ucd WHERE ucd.eliminado = 0 ORDER BY ucd.id.idUsuario DESC")
  public List<UsuarioClientDetails> listar();

  @Query(value = "SELECT ucd FROM UsuarioClientDetails ucd WHERE ucd.eliminado = 0 AND ucd.id.idUsuario = :#{#id.idUsuario} AND ucd.id.clientId = :#{#id.clientId}")
  public UsuarioClientDetails buscarPorId(IdUSuarioClientDetails id);

  @Modifying
  @Query(value = "UPDATE UsuarioClientDetails ucd SET ucd.eliminado = 1 WHERE ucd.id.idUsuario = :#{#id.idUsuario} AND ucd.id.clientId = :#{#id.clientId}")
  public void eliminarPorId(IdUSuarioClientDetails id);

}