package com.farmacia.service.auth.hist;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.farmacia.entity.auth.hist.HistEvento;

public interface IHistEventoService {

  HistEvento guardar(HistEvento entidad);

  Optional<HistEvento> buscarPorId(Integer id);

  Page<HistEvento> listarPaginado(Pageable pageable, String username);
}
