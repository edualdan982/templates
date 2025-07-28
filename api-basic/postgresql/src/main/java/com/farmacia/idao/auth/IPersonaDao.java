package com.farmacia.idao.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.farmacia.entity.auth.Persona;
import com.farmacia.entity.auth.TypeDoc;

public interface IPersonaDao extends JpaRepository<Persona, Integer> {

  @Query("SELECT p FROM Persona p WHERE p.eliminado = false")
  List<Persona> listar();

  @Query("SELECT p FROM Persona p WHERE p.eliminado = false AND p.idPersona = ?1")
  Optional<Persona> buscarPorId(Integer id);

  @Modifying
  @Query("UPDATE Persona p SET p.eliminado = false WHERE p.idPersona = ?1")
  void eliminarPorId(Integer id);

  @Query("SELECT p FROM Persona p WHERE p.eliminado = false AND p.numeroDoc = ?1 AND p.tipoDoc = ?2 AND p.comDoc is null")
  Optional<Persona> buscarPorNroDoc(String nroDoc, TypeDoc typeDoc);

  @Query("SELECT p FROM Persona p WHERE p.eliminado = false AND p.numeroDoc = ?1 AND p.comDoc = ?2 AND p.tipoDoc = ?3")
  Optional<Persona> buscarPorNroDoc(String nroDoc, String comDoc, TypeDoc typeDoc);

  @Query("SELECT COALESCE(COUNT(p),0) FROM Persona p WHERE p.eliminado = false AND p.numeroDoc = ?1 AND p.comDoc = ?2 AND p.tipoDoc = ?3")
  Integer countPorNroDoc(String nroDoc, String comDoc, TypeDoc name);
}
