package com.firmas.service.auth;

import java.util.List;

import com.firmas.entity.auth.Rol;
import com.firmas.entity.auth.Usuario;
import com.firmas.entity.auth.UsuarioRol;
import com.firmas.entity.auth.id.IdUsuarioRol;

public interface IUsuarioRolService {

  UsuarioRol guardar(UsuarioRol entidad);

  UsuarioRol buscarPorId(IdUsuarioRol id);

  Boolean eliminarPorId(IdUsuarioRol id);

  List<UsuarioRol> listarPorUsuario(Integer idUsuario);

  Boolean existe(IdUsuarioRol id);

  List<UsuarioRol> asignarRolPagos(Usuario nuevoUsuario, List<Rol> roles, Boolean crearRol);

}
