package com.farmacia.idao.adm;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.farmacia.entity.adm.Menu;

public interface IMenuDao extends JpaRepository<Menu, Integer> {

  @Modifying
  @Query(value = "UPDATE Menu SET eliminado = true WHERE idMenu = :idMenu")
  public void eliminarPorId(Integer idMenu);

  @Query(value = "SELECT m FROM Menu m WHERE m.eliminado = false ORDER BY m.idMenu DESC")
  public List<Menu> listar();

  @Query("SELECT m FROM Menu m WHERE m.eliminado = false AND m.idMenu = :id")
  public Optional<Menu> buscarPorId(Integer id);
}
