package com.farmacia.service.adm;

import java.util.Optional;

import com.farmacia.entity.adm.Gestion;
import com.farmacia.service.IGenericService;

public interface IGestionService extends IGenericService<Gestion, Integer> {
  Optional<Gestion> buscarVigente();
}
