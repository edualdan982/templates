package edual.auth.service;

import java.util.List;
import java.util.Optional;

public abstract interface IGenericService<E, I> {

  E guardar(E entidad);

  Boolean eliminarPorId(I id);

  Optional<E> buscarPorId(I id);

  List<E> listar();
}
