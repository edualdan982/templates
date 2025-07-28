package com.farmacia.idao.dt;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.farmacia.entity.dt.CorreoData;

public interface ICorreoDataDao extends JpaRepository<CorreoData, String> {

  @Query(value = "SELECT cd FROM CorreoData cd WHERE cd.correo = :id AND cd.entorno = :entorno")
  public Optional<CorreoData> buscarPorId(String id, String entorno);

  @Query(value = "SELECT cd FROM CorreoData cd WHERE cd.entorno = :entorno")
  public Optional<CorreoData> buscarPorEntorno(String entorno);

  @Query(value = "SELECT cd FROM CorreoData cd WHERE cd.entorno = :entorno")
  public List<CorreoData> listar(String entorno);

  @Modifying
  @Query(value = "DELETE FROM CorreoData cd WHERE cd.correo = :id AND cd.entorno = :entorno")
  public void eliminarPorId(String id, String entorno);
}
