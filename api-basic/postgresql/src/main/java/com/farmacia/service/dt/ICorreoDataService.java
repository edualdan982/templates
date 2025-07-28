package com.farmacia.service.dt;

import java.util.Optional;

import com.farmacia.entity.dt.CorreoData;
import com.farmacia.service.IGenericService;

public interface ICorreoDataService extends IGenericService<CorreoData, String> {

  CorreoData buscarPorEtorno();

  Optional<CorreoData> buscarPorEntorno();

  CorreoData cargarPrimeraVez();
}
