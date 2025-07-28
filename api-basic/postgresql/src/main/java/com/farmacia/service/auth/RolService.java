package com.farmacia.service.auth;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmacia.config.constantes.Mensajes;
import com.farmacia.entity.auth.Rol;
import com.farmacia.idao.auth.IRolDao;

@Service
public class RolService implements IRolService {

	private static final Logger log = LoggerFactory.getLogger(RolService.class);

	@Autowired
	private IRolDao repository;

	@Transactional
	@Override
	public Rol guardar(Rol entidad) {
		try {
			log.info("Servicio: guardando entidad Rol");
			return repository.save(entidad);
		} catch (Exception e) {
			log.error("Error al guardar la entidad Rol");
			log.error("Detalle: {}", (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
			return null;
		}
	}

	@Transactional(readOnly = true)
	@Override
	public Rol buscarPorId(Integer idRol) {
		return repository.findById(idRol).orElse(null);
	}

	@Transactional
	@Override
	public Boolean eliminarPorId(Integer idRol) {
		try {
			log.info("Servicio: eliminarPorId entidad Rol");
			repository.eliminarPorId(idRol);
			return true;
		} catch (Exception e) {
			log.error("Error al eliminarPorId la entidad Rol");
			log.error("Detalle:  {}", (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
			return false;
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<Rol> listar() {
		return repository.listar();
	}

	@Transactional(readOnly = true)
	@Override
	public Integer contarNombre(String nombre) {
		try {
			log.info("Servicio: validarNombre entidad Rol");
			return repository.contarNombre(nombre);
		} catch (Exception e) {
			log.error("Error al validarNombre la entidad Rol");
			log.error("Detalle:  {}", (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
			return 0;
		}
	}

	@Transactional
	@Override
	public Boolean existePorId(Integer idRol) {
		return repository.existsById(idRol);
	}

	@Transactional
	@Override
	public Optional<Rol> buscarPorNombre(String nombre) {
		return repository.buscarPorNombre(nombre);
	}

}
