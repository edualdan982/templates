package com.farmacia.service.auth;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmacia.config.constantes.Mensajes;
import com.farmacia.entity.auth.Usuario;
import com.farmacia.entity.auth.UsuarioClientDetails;
import com.farmacia.entity.auth.id.IdUSuarioClientDetails;
import com.farmacia.entity.auth.oauth.ClientDetail;
import com.farmacia.idao.auth.IUsuarioClientDetailDao;
import com.farmacia.service.auth.oauth.IClientDetailService;
import com.farmacia.service.util.IFechaServiceUtil;
import com.farmacia.service.util.IJwtAuthServiceUtil;

@Service
public class UsuarioClientDetailService implements IUsuarioClientDetailService {
  private static final Logger log = LoggerFactory.getLogger(UsuarioClientDetailService.class);
  @Autowired
  private IUsuarioClientDetailDao repository;
  @Autowired
  private IJwtAuthServiceUtil authJwtServiceUtil;
  @Autowired
  private IClientDetailService clientDetailsService;
  @Autowired
  private IFechaServiceUtil fechaServiceUtil;

  @Transactional
  @Override
  public UsuarioClientDetails guardar(UsuarioClientDetails entidad) {
    try {
      log.info("Servicio: guardando entidad UsuarioClientDetails");
      return repository.save(entidad);
    } catch (Exception e) {
      log.error("Error al guardar la entidad UsuarioClientDetails");
      log.error("Detalle: {}", (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
      return null;
    }
  }

  @Transactional(readOnly = true)
  @Override
  public List<UsuarioClientDetails> listar() {
    return repository.listar();
  }

  @Transactional(readOnly = true)
  @Override
  public UsuarioClientDetails buscarPorId(IdUSuarioClientDetails id) {
    return repository.buscarPorId(id);
  }

  @Transactional
  @Override
  public boolean existe(IdUSuarioClientDetails pk) {
    return repository.existsById(pk);
  }

  @Transactional
  @Override
  public Boolean eliminarPorId(IdUSuarioClientDetails pk) {
    try {
      log.info("Servicio: eliminarPorId entidad USuarioClientDetails");
      repository.eliminarPorId(pk);
      return true;
    } catch (Exception e) {
      log.error("Error al eliminarPorId la entidad USuarioClientDetails");
      log.error("Detalle:  {}", (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
      return false;
    }
  }

  @Transactional(readOnly = true)
  @Override
  public List<UsuarioClientDetails> listarPorUsuario(Integer idUsuario) {
    return repository.buscarPorIdUsuario(idUsuario);
  }

  @Transactional
  @Override
  public Boolean asignarClientsJwt(Usuario nuevoUsuario, String clientId) {
    OAuth2Request oatuh2 = authJwtServiceUtil.getOAuth2Request();
    
    ClientDetail sistema = clientDetailsService.buscarPorId(clientId != null ? clientId : oatuh2.getClientId());
    UsuarioClientDetails asignarSistema = new UsuarioClientDetails();
    asignarSistema.setSistema(sistema);
    asignarSistema.setUsuario(nuevoUsuario);
    asignarSistema.setId(new IdUSuarioClientDetails(nuevoUsuario.getIdUsuario(), sistema.getClientId()));
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.set(Calendar.DAY_OF_MONTH, 31);
    calendar.set(Calendar.MONTH, Calendar.DECEMBER);
    calendar.set(Calendar.YEAR, fechaServiceUtil.obtenerGestionActual());
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    asignarSistema.setFechaVencimiento(calendar.getTime());

    try {
      repository.save(asignarSistema);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    log.info("Se ha asignado al usuario el sistema solicitante.");
    return false;
  }
}
