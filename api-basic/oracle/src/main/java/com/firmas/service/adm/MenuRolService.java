package com.firmas.service.adm;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firmas.config.constantes.Mensajes;
import com.firmas.entity.adm.MenuRol;
import com.firmas.idao.adm.IMenuRolDao;

@Service
public class MenuRolService implements IMenuRolService {
  private static final Logger log = LoggerFactory.getLogger(MenuRolService.class);

  @Autowired
  private IMenuRolDao repository;

  @Transactional
  @Override
  public MenuRol guardar(MenuRol entidad) {
    log.info("Servicio: persistir entidad MenuEnlace");
    try {
      return repository.save(entidad);
    } catch (Exception e) {
      log.error("Error al persistir la entidad MenuEnlace");
      log.error("Detalle:  {}", (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
      return null;
    }
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<MenuRol> buscarPorId(Integer id) {
    return repository.buscarPorId(id);
  }

  @Transactional
  @Override
  public Boolean eliminarPorId(Integer id) {
    try {
      log.info("Servicio: eliminarPorId entidad: MenuEnlace");
      repository.eliminarPorId(id);
      return true;
    } catch (Exception e) {
      log.error("Error al eliminarPorId la entidad: MenuEnlace");
      log.error("Detalle:  {}", (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
      return false;
    }
  }

  @Transactional(readOnly = true)
  @Override
  public List<MenuRol> listar() {
    return repository.listar();
  }

  @Transactional(readOnly = true)
  @Override
  public List<MenuRol> listar(Integer idRol) {
    return repository.listar(idRol);
  }

  @Transactional(readOnly = true)
  @Override
  public List<MenuRol> listarConEnlaces(Integer idRol) {
    return repository.listarConEnlaces(idRol);
  }

  @Transactional(readOnly = true)
  @Override
  public List<MenuRol> listarPorMenu(String nombreRol) {
    int indice = nombreRol.indexOf("_");
    if (indice > -1)
      return repository.listarPorMenu(nombreRol.substring(indice + 1));

    return repository.listarPorMenu(nombreRol);
  }

}
