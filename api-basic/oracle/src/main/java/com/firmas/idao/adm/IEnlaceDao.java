package com.firmas.idao.adm;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.firmas.entity.adm.Enlace;

public interface IEnlaceDao extends JpaRepository<Enlace, Integer> {

  @Query("SELECT e FROM Enlace e WHERE e.eliminado = false ORDER BY e.idEnlace DESC")
  List<Enlace> listar();

  @Query("SELECT e FROM Enlace e WHERE e.eliminado = false AND e.idEnlace = :id")
  Optional<Enlace> buscarPorId(Integer id);

  @Modifying
  @Query("UPDATE Enlace e SET e.eliminado = true WHERE e.idEnlace = :id")
  void eliminarPorId(Integer id);
}
