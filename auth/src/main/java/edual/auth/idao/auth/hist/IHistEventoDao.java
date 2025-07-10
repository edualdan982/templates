package edual.auth.idao.auth.hist;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import edual.auth.entity.auth.hist.HistEvento;

public interface IHistEventoDao extends JpaRepository<HistEvento, Integer> {

  @Query("SELECT h FROM HistEvento h WHERE h.id = :id")
  Optional<HistEvento> buscarPorId(Integer id);

  @Query("SELECT h FROM HistEvento h WHERE h.username = :username")
  List<HistEvento> listar(String username);

  @Query("SELECT h FROM HistEvento h WHERE h.username = :username ORDER BY h.fechaRegistro DESC")
  Page<HistEvento> listarPaginado(Pageable pageable, String username);
}
