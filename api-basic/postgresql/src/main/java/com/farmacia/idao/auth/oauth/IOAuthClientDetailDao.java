package com.farmacia.idao.auth.oauth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.farmacia.entity.auth.oauth.ClientDetail;

public interface IOAuthClientDetailDao extends JpaRepository<ClientDetail, String> {

	@Query(value = "SELECT cd FROM ClientDetail cd WHERE cd.clientId = :clientId")
	public ClientDetail buscarPorId(String clientId);
}
