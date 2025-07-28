package com.firmas.service.dt;

import java.util.Optional;

import com.firmas.entity.dt.CorreoData;
import com.firmas.service.IGenericService;

public interface ICorreoDataService extends IGenericService<CorreoData, String> {

  CorreoData buscarPorEtorno();

  Optional<CorreoData> buscarPorEntorno();

  CorreoData cargarPrimeraVez();
}
