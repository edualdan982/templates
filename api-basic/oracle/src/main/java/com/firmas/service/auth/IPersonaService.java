package com.firmas.service.auth;

import java.util.Optional;

import com.firmas.entity.auth.Persona;
import com.firmas.entity.auth.TypeDoc;
import com.firmas.service.IGenericService;

public interface IPersonaService extends IGenericService<Persona, Integer> {

  Optional<Persona> buscarPorNroDoc(String nroDoc, String comDoc, TypeDoc typeDoc);

  Integer contarNroDoc(String nroDoc, String comDoc,  TypeDoc typeDoc);
}
