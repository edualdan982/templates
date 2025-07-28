package com.farmacia.service.auth.oauth;

import java.util.List;

import com.farmacia.entity.auth.oauth.ClientDetail;

public interface IClientDetailService {

  public ClientDetail guardar(ClientDetail entidad);

  public ClientDetail buscarPorId(String clientId);

  public List<ClientDetail> listar();
}
