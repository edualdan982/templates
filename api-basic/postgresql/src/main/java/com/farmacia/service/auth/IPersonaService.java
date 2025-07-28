package com.farmacia.service.auth;

import java.util.Optional;

import com.farmacia.entity.auth.Persona;
import com.farmacia.entity.auth.TypeDoc;
import com.farmacia.service.IGenericService;

public interface IPersonaService extends IGenericService<Persona, Integer> {

  Optional<Persona> buscarPorNroDoc(String nroDoc, String comDoc, TypeDoc typeDoc);

  Integer contarNroDoc(String nroDoc, String comDoc,  TypeDoc typeDoc);
}
