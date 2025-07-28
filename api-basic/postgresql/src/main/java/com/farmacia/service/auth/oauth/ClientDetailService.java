package com.farmacia.service.auth.oauth;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmacia.config.constantes.Mensajes;
import com.farmacia.entity.auth.oauth.ClientDetail;
import com.farmacia.idao.auth.oauth.IOAuthClientDetailDao;

@Service
public class ClientDetailService implements IClientDetailService {
  private static final Logger log = LoggerFactory.getLogger(ClientDetailService.class);

  @Autowired
  private IOAuthClientDetailDao repository;

  @Transactional
  @Override
  public ClientDetail guardar(ClientDetail entidad) {
    log.info("Servicio para persistir la entidad - OAuthClientDetails");
    try {
      return repository.save(entidad);
    } catch (Exception e) {
      log.error("Error al persistir la entidad - OAuthClientDetails");
      log.error("Detalle:  {}", (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
      return null;
    }
  }

  @Transactional(readOnly = true)
  @Override
  public ClientDetail buscarPorId(String clientId) {
    return repository.buscarPorId(clientId);
  }

  @Transactional(readOnly = true)
  @Override
  public List<ClientDetail> listar() {
    return repository.findAll();
  }

}
