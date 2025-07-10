package edual.auth.service.auth.hist;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edual.auth.entity.auth.hist.HistEvento;
import edual.auth.idao.auth.hist.IHistEventoDao;

@Service
public class HistEventoService implements IHistEventoService {
  private static final Logger log = LoggerFactory.getLogger(HistEventoService.class);

  @Autowired
  private IHistEventoDao repository;

  @Transactional
  @Override
  public HistEvento guardar(HistEvento entidad) {
    log.info("Servicio guardar entidad: {}", HistEvento.class.getSimpleName());
    try {
      return repository.save(entidad);
    } catch (Exception e) {
      log.error("No se completo el servicio. Detalle {}",
          (e.getLocalizedMessage() == null ? "Sin detalle" : e.getLocalizedMessage()));
      return null;
    }
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<HistEvento> buscarPorId(Integer id) {
    return repository.buscarPorId(id);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<HistEvento> listarPaginado(Pageable pageable, String username) {
    return repository.listarPaginado(pageable, username);
  }

}
