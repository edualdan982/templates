package com.firmas.service.auth;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firmas.entity.auth.Persona;
import com.firmas.entity.auth.TypeDoc;
import com.firmas.idao.auth.IPersonaDao;

@Service
public class PersonaService implements IPersonaService {
  private static final Logger log = LoggerFactory.getLogger(PersonaService.class);

  @Autowired
  private IPersonaDao repository;

  @Transactional
  @Override
  public Persona guardar(Persona entidad) {
    log.info("Servicio guardar entidad: {}", Persona.class.getSimpleName());
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
    log.info("Servicio eliminarPorId entidad: {}", Persona.class.getSimpleName());
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
  public Optional<Persona> buscarPorId(Integer id) {
    if (id == null)
      return Optional.empty();

    return repository.buscarPorId(id);
  }

  @Transactional(readOnly = true)
  @Override
  public List<Persona> listar() {
    return repository.listar();
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<Persona> buscarPorNroDoc(String nroDoc, String comDoc, TypeDoc typeDoc) {
    log.info("Buscando la persona con el tipo de documento: {} y el numero de documento: {}{}", typeDoc, nroDoc,
        comDoc != null ? " - " + comDoc : "");
    if (!typeDoc.equals(TypeDoc.CI))
      comDoc = null;
    if (comDoc != null && comDoc != "")
      return repository.buscarPorNroDoc(nroDoc, comDoc, typeDoc);
    else
      return repository.buscarPorNroDoc(nroDoc, typeDoc);
  }

  @Transactional(readOnly = true)
  @Override
  public Integer contarNroDoc(String nroDoc, String comDoc, TypeDoc typeDoc) {
    if (!typeDoc.equals(TypeDoc.CI))
      comDoc = null;
    return repository.countPorNroDoc(nroDoc, comDoc, typeDoc);
  }
}
