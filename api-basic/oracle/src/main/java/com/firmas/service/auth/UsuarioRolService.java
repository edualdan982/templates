package com.firmas.service.auth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firmas.config.constantes.Mensajes;
import com.firmas.entity.auth.Rol;
import com.firmas.entity.auth.Usuario;
import com.firmas.entity.auth.UsuarioRol;
import com.firmas.entity.auth.id.IdUsuarioRol;
import com.firmas.idao.auth.IUsuarioRolDao;
import com.firmas.service.util.IFechaServiceUtil;

@Service
public class UsuarioRolService implements IUsuarioRolService {

	private static final Logger log = LoggerFactory.getLogger(UsuarioRolService.class);

	@Autowired
	private IUsuarioRolDao repository;
	@Autowired
	private IRolService rolService;
	@Autowired
	private IFechaServiceUtil fechaServiceUtil;

	@Transactional
	@Override
	public UsuarioRol guardar(UsuarioRol entidad) {
		try {
			log.info("Servicio: guardando entidad UsuarioRol");
			return repository.save(entidad);
		} catch (Exception e) {
			log.error("Error al guardar la entidad UsuarioRol");
			log.error("Detalle: {}", (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
			return null;
		}
	}

	@Transactional(readOnly = true)
	@Override
	public UsuarioRol buscarPorId(IdUsuarioRol id) {
		return repository.findById(id).orElse(null);
	}

	@Transactional
	@Override
	public Boolean eliminarPorId(IdUsuarioRol id) {
		try {
			log.info("Servicio: eliminarPorId entidad UsuarioRol");
			repository.eliminarPorId(id);
			return true;
		} catch (Exception e) {
			log.error("Error al eliminarPorId la entidad UsuarioRol");
			log.error("Detalle:  {}", (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
			return false;
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<UsuarioRol> listarPorUsuario(Integer idUsuario) {
		return repository.buscarPorIdUsuarioTodo(idUsuario);
	}

	@Transactional(readOnly = true)
	@Override
	public Boolean existe(IdUsuarioRol id) {
		return repository.existsById(id);
	}

	@Transactional
	@Override
	public List<UsuarioRol> asignarRolPagos(Usuario nuevoUsuario, List<Rol> roles, Boolean crearRol) {
		List<UsuarioRol> usuarioRoles = new ArrayList<>();
		if (roles.isEmpty())
			return usuarioRoles;

		Iterator<Rol> iterator = roles.iterator();
		while (iterator.hasNext()) {
			Rol rol = iterator.next();

			if (crearRol && rol.getIdRol() == null) {
				Optional<Rol> findRol = rolService.buscarPorNombre(rol.getNombre());
				if (findRol.isPresent())
					rol = findRol.get();
				else {
					findRol = Optional.ofNullable(rolService.guardar(rol));
				}

				UsuarioRol nuevoRolPago = new UsuarioRol();
				nuevoRolPago.setId(new IdUsuarioRol(nuevoUsuario.getIdUsuario(), rol.getIdRol()));
				nuevoRolPago.setUsuario(nuevoUsuario);
				nuevoRolPago.setRol(findRol.get());

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.set(Calendar.DAY_OF_MONTH, 31);
				calendar.set(Calendar.MONTH, Calendar.DECEMBER);
				calendar.set(Calendar.YEAR, fechaServiceUtil.obtenerGestionActual());
				calendar.set(Calendar.HOUR_OF_DAY, 23);
				calendar.set(Calendar.MINUTE, 59);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				nuevoRolPago.setFechaVencimiento(calendar.getTime());

				if (!existe(nuevoRolPago.getId()))
					usuarioRoles.add(guardar(nuevoRolPago));
				else
					usuarioRoles.add(nuevoRolPago);
			}
		}

		return usuarioRoles;
	}

}
