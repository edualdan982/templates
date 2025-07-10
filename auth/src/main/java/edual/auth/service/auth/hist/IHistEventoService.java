package edual.auth.service.auth.hist;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import edual.auth.entity.auth.hist.HistEvento;

public interface IHistEventoService {

  HistEvento guardar(HistEvento entidad);

  Optional<HistEvento> buscarPorId(Integer id);

  Page<HistEvento> listarPaginado(Pageable pageable, String username);
}
