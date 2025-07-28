package com.firmas.service.adm;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firmas.entity.adm.Gestion;
import com.firmas.idao.adm.IGestionDao;

@Service
public class GestionService implements IGestionService {
  private static final Logger log = LoggerFactory.getLogger(GestionService.class);

  @Autowired
  private IGestionDao repository;

  @Transactional
  @Override
  public Gestion guardar(Gestion entidad) {
    log.info("Servicio guardar entidad {}", Gestion.class.getSimpleName());
    try {
      return repository.save(entidad);
    } catch (Exception e) {
      log.error("No se completo el servicio. Detalle: {}",
          (e.getLocalizedMessage() == null ? "Sin detalle" : e.getLocalizedMessage()));
      return null;
    }
  }

  @Transactional
  @Override
  public Boolean eliminarPorId(Integer id) {
    log.info("Servicio eliminarPorId entidad {}", Gestion.class.getSimpleName());
    try {
      repository.eliminarPorId(id);
      return true;
    } catch (Exception e) {
      log.error("No se completo el servicio. Detalle: {}",
          (e.getLocalizedMessage() == null ? "Sin detalle" : e.getLocalizedMessage()));
      return false;
    }
  }

  @Transactional
  @Override
  public Optional<Gestion> buscarPorId(Integer id) {
    return repository.buscarPorId(id);
  }

  @Transactional
  @Override
  public List<Gestion> listar() {
    return repository.listar();
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<Gestion> buscarVigente() {
    return repository.buscarVigente();
  }

}
