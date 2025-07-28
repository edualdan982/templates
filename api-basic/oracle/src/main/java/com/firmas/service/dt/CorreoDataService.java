package com.firmas.service.dt;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firmas.entity.dt.CorreoData;
import com.firmas.idao.dt.ICorreoDataDao;

@Service
public class CorreoDataService implements ICorreoDataService {
  private static final Logger log = LoggerFactory.getLogger(CorreoDataService.class);

  @Autowired
  private ICorreoDataDao repository;
  @Value("${spring.profiles.active}")
  private String ENTORNO;

  @Override
  @Transactional
  @CacheEvict(value = "correoDataCache", allEntries = true)
  public CorreoData guardar(CorreoData entidad) {
    try {
      log.info("Ejecutandose el servicio: guardar entidad CorreoData");
      entidad.setEntorno(ENTORNO);
      return repository.save(entidad);
    } catch (Exception e) {
      log.error("No se pudo persistir la entidad. Detalle: "
          + (e.getLocalizedMessage() == null ? "Sin detalle" : e.getLocalizedMessage()));
      return null;
    }
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<CorreoData> buscarPorId(String id) {
    return repository.buscarPorId(id, ENTORNO);
  }

  @Transactional(readOnly = true)
  @Override
  public List<CorreoData> listar() {
    return repository.listar(ENTORNO);
  }

  @Transactional
  @Override
  @CacheEvict(value = "correoDataCache", allEntries = true)
  public Boolean eliminarPorId(String id) {
    try {
      log.info("Ejecutandose el servicio: eliminarPorId entidad CorreoData");
      repository.eliminarPorId(id, ENTORNO);
      return true;
    } catch (Exception e) {
      log.error("No se pudo eliminar la entidad. Detalle: "
          + (e.getLocalizedMessage() == null ? "Sin detalle" : e.getLocalizedMessage()));
      return false;
    }
  }

  @Transactional(readOnly = true)
  @Override
  @Cacheable(value = "correoDataCache")
  public CorreoData buscarPorEtorno() {
    long inicio = System.nanoTime();
    CorreoData data = repository.buscarPorEntorno(ENTORNO).orElse(null);
    long tFinal = System.nanoTime();
    log.info("Tiempo transcurrido en el servicio: " + (tFinal - inicio) / 1e6 + " ms");
    return data;
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<CorreoData> buscarPorEntorno() {
    return repository.buscarPorEntorno(ENTORNO);
  }

  @Transactional(readOnly = true)
  @CacheEvict(value = "correoDataCache", allEntries = true)
  @Override
  public CorreoData cargarPrimeraVez() {
    Optional<CorreoData> data = repository.buscarPorEntorno(ENTORNO);
    if (data.isPresent())
      return data.get();
    return null;
  }

}