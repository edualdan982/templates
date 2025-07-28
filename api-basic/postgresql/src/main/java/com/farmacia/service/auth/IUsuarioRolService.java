package com.farmacia.service.auth;

import java.util.List;

import com.farmacia.entity.auth.Rol;
import com.farmacia.entity.auth.Usuario;
import com.farmacia.entity.auth.UsuarioRol;
import com.farmacia.entity.auth.id.IdUsuarioRol;

public interface IUsuarioRolService {

  UsuarioRol guardar(UsuarioRol entidad);

  UsuarioRol buscarPorId(IdUsuarioRol id);

  Boolean eliminarPorId(IdUsuarioRol id);

  List<UsuarioRol> listarPorUsuario(Integer idUsuario);

  Boolean existe(IdUsuarioRol id);

  List<UsuarioRol> asignarRolPagos(Usuario nuevoUsuario, List<Rol> roles, Boolean crearRol);

}
