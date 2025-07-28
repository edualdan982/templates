package com.firmas.service.adm;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firmas.entity.adm.Enlace;
import com.firmas.idao.adm.IEnlaceDao;

@Service
public class EnlaceService implements IEnlaceService {
  private static final Logger log = LoggerFactory.getLogger(EnlaceService.class);

  @Autowired
  private IEnlaceDao repository;

  @Transactional
  @Override
  public Enlace guardar(Enlace entidad) {
    log.info("Servicio guardar entidad {}", Enlace.class.getSimpleName());
    try {
      return repository.save(entidad);
    } catch (Exception e) {
      log.error("No se completo el servicio. Detalle {}",
          (e.getLocalizedMessage() == null ? "Sin detalle" : e.getLocalizedMessage()));
      return null;
    }
  }

  @Transactional
  @Override
  public Boolean eliminarPorId(Integer id) {
    log.info("Servicio eliminarPorId entidad {}", Enlace.class.getSimpleName());
    try {
      repository.eliminarPorId(id);
      return true;
    } catch (Exception e) {
      log.error("No se completo el servicio. Detalle {}",
          (e.getLocalizedMessage() == null ? "Sin detalle" : e.getLocalizedMessage()));
      return false;
    }
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<Enlace> buscarPorId(Integer id) {
    return repository.buscarPorId(id);
  }

  @Transactional(readOnly = true)
  @Override
  public List<Enlace> listar() {
    return repository.listar();
  }

}
