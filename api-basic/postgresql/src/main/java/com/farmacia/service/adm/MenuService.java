package com.farmacia.service.adm;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmacia.entity.adm.Menu;
import com.farmacia.idao.adm.IMenuDao;

@Service
public class MenuService implements IMenuService {
  private static final Logger log = LoggerFactory.getLogger(MenuService.class);

  @Autowired
  private IMenuDao repository;

  @Transactional
  @Override
  public Menu guardar(Menu entidad) {
    log.info("Servicio guardar entidad {}", Menu.class.getSimpleName());
    try {
      return repository.save(entidad);
    } catch (Exception e) {
      log.error("No se completo el servicio. Detalle {}",
          (e.getLocalizedMessage() == null ? "Sin detealle" : e.getLocalizedMessage()));
      return null;
    }
  }

  @Transactional
  @Override
  public Boolean eliminarPorId(Integer id) {
    log.info("Servicio eliminarPorId entidad {}", Menu.class.getSimpleName());
    try {
      repository.eliminarPorId(id);
      return true;
    } catch (Exception e) {
      log.error("No se completo el servicio. Detalle {}",
          (e.getLocalizedMessage() == null ? "Sin detealle" : e.getLocalizedMessage()));
      return false;
    }
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<Menu> buscarPorId(Integer id) {
    return repository.buscarPorId(id);
  }

  @Transactional(readOnly = true)
  @Override
  public List<Menu> listar() {
    return repository.listar();
  }

}
