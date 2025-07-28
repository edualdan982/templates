package com.firmas.service.adm;

import java.util.List;

import com.firmas.entity.adm.MenuRol;
import com.firmas.service.IGenericService;

public interface IMenuRolService extends IGenericService<MenuRol, Integer>{
  
  List<MenuRol> listar(Integer idRol);

  List<MenuRol> listarConEnlaces(Integer idRol);

  List<MenuRol> listarPorMenu(String nombreRol);
}
