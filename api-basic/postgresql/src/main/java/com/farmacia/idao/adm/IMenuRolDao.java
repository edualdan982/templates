package com.farmacia.idao.adm;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.farmacia.entity.adm.MenuRol;

public interface IMenuRolDao extends JpaRepository<MenuRol, Integer> {

  @Modifying
  @Query(value = "UPDATE MenuRol SET eliminado = true WHERE idMenuRol = :idMenuRol")
  public void eliminarPorId(Integer idMenuRol);

  @Query(value = "SELECT me FROM MenuRol me WHERE me.eliminado = false")
  public List<MenuRol> listar();

  @Query(value = "SELECT me FROM MenuRol me WHERE me.eliminado = false AND me.rol.idRol = :idRol ORDER BY me.idMenuRol DESC")
  public List<MenuRol> listar(Integer idRol);

  @Query(value = "SELECT DISTINCT me FROM MenuRol me JOIN FETCH me.menu m JOIN FETCH m.enlaces e WHERE me.eliminado = false AND m.eliminado = false AND e.eliminado = false AND me.rol.idRol = :idRol AND m.eliminado = false ORDER BY me.menu.orden")
  public List<MenuRol> listarConEnlaces(Integer idRol);

  @Query(value = "SELECT me FROM MenuRol me JOIN FETCH me.menu " +
      "WHERE me.eliminado = false AND me.rol.nombre = :nombreRol AND me.rol.eliminado = false ORDER BY me.menu.orden DESC")
  public List<MenuRol> listarPorMenu(String nombreRol);

  @Query("SELECT me FROM MenuRol me WHERE me.eliminado = false AND me.idMenuRol = :id")
  public Optional<MenuRol> buscarPorId(Integer id);
}
