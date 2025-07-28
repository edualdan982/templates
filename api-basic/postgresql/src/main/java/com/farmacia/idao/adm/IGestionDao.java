package com.farmacia.idao.adm;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.farmacia.entity.adm.Gestion;

public interface IGestionDao extends JpaRepository<Gestion, Integer> {

  @Query("SELECT g FROM Gestion g WHERE g.eliminado = false ORDER BY g.gestion DESC")
  List<Gestion> listar();

  @Query("SELECT g FROM Gestion g WHERE g.eliminado = false AND g.vigente = ?1")
  List<Gestion> listar(Boolean vigente);

  @Query("SELECT g FROM Gestion g WHERE g.eliminado = false AND g.vigente = 1")
  Optional<Gestion> buscarVigente();

  @Modifying
  @Query("UPDATE Gestion g SET eliminado = true WHERE g.idGestion = ?1")
  void eliminarPorId(Integer id);

  @Query("SELECT g FROM Gestion g WHERE g.idGestion = ?1 AND g.eliminado = false")
  Optional<Gestion> buscarPorId(Integer id);
}
