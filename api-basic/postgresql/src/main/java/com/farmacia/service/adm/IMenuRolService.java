package com.farmacia.service.adm;

import java.util.List;

import com.farmacia.entity.adm.MenuRol;
import com.farmacia.service.IGenericService;

public interface IMenuRolService extends IGenericService<MenuRol, Integer>{
  
  List<MenuRol> listar(Integer idRol);

  List<MenuRol> listarConEnlaces(Integer idRol);

  List<MenuRol> listarPorMenu(String nombreRol);
}
