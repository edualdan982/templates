package com.firmas.service.auth;

import java.util.List;
import java.util.Optional;

import com.firmas.entity.auth.Rol;

public interface IRolService {

  Rol guardar(Rol entidad);

  Rol buscarPorId(Integer idRol);

  Boolean eliminarPorId(Integer idRol);

  List<Rol> listar();

  Integer contarNombre(String nombre);

  Boolean existePorId(Integer idRol);

  Optional<Rol> buscarPorNombre(String nombre);
}
