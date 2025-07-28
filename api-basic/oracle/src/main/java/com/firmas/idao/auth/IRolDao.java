package com.firmas.idao.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.firmas.entity.auth.Rol;

public interface IRolDao extends JpaRepository<Rol, Integer> {

  @Modifying
  @Query(value = "UPDATE Rol SET eliminado = true WHERE idRol = :idRol")
  public void eliminarPorId(Integer idRol);

  @Query(value = "SELECT COALESCE(COUNT(nombre), 0) FROM Rol WHERE nombre = :nombre")
  public Integer contarNombre(String nombre);

  @Query(value = "SELECT r FROM Rol r WHERE r.eliminado = false ORDER BY r.idRol DESC")
  public List<Rol> listar();

  @Query(value = "SELECT r FROM Rol r WHERE r.eliminado = false AND r.nombre = :nombre")
  public Optional<Rol> buscarPorNombre(String nombre);

}
