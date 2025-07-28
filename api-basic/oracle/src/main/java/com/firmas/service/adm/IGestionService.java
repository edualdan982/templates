package com.firmas.service.adm;

import java.util.Optional;

import com.firmas.entity.adm.Gestion;
import com.firmas.service.IGenericService;

public interface IGestionService extends IGenericService<Gestion, Integer> {
  Optional<Gestion> buscarVigente();
}
