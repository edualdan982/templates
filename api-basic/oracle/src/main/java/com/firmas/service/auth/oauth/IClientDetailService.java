package com.firmas.service.auth.oauth;

import java.util.List;

import com.firmas.entity.auth.oauth.ClientDetail;

public interface IClientDetailService {

  public ClientDetail guardar(ClientDetail entidad);

  public ClientDetail buscarPorId(String clientId);

  public List<ClientDetail> listar();
}
