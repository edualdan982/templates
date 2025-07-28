package com.farmacia.service.auth;

import java.util.List;

import com.farmacia.entity.auth.Usuario;
import com.farmacia.entity.auth.UsuarioClientDetails;
import com.farmacia.entity.auth.id.IdUSuarioClientDetails;

public interface IUsuarioClientDetailService {

  /**
   * @param entidad
   * @return El usuario que se persistio en la base de datos
   */
  UsuarioClientDetails guardar(UsuarioClientDetails entidad);

  /**
   * Lista los usuario que no estan eliminados
   * @return
   */
  List<UsuarioClientDetails> listar();

  /**
   * @param id IdUSuarioClientDetails
   * @return El usuario que concidia con el id.
   */
  UsuarioClientDetails buscarPorId(IdUSuarioClientDetails id);

  /**
   * @param pk IdUSuarioClientDetails
   * @return Boolean Verifica si existe la entidad con ese id
   */
  boolean existe(IdUSuarioClientDetails pk);
  
  /**
   * Elimina la entidad con el id especificado
   * @param pk
   * @return
   */
  Boolean eliminarPorId(IdUSuarioClientDetails pk);

  /**
   * Lista por el idUsuario, de los clientes asignados.
   * @param idUsuario
   * @return
   */
  List<UsuarioClientDetails> listarPorUsuario(Integer idUsuario);

  Boolean asignarClientsJwt(Usuario nuevoUsuario, String clientId);

}
