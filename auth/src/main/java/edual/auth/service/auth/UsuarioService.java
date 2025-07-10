package edual.auth.service.auth;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edual.auth.config.constantes.Mensajes;
import edual.auth.config.ldap.ILdapService;
import edual.auth.config.ldap.LdapException;
import edual.auth.entity.auth.Rol;
import edual.auth.entity.auth.TypeAccount;
import edual.auth.entity.auth.Usuario;
import edual.auth.entity.auth.hist.HistEvento;
import edual.auth.entity.auth.hist.TypeEstadoLogin;
import edual.auth.idao.auth.IUsuarioClientDetailDao;
import edual.auth.idao.auth.IUsuarioDao;
import edual.auth.idao.auth.IUsuarioRolDao;
import edual.auth.service.auth.hist.IHistEventoService;
import edual.auth.service.util.IEntidadUtilService;
import edual.auth.service.util.IHttpServiceUtil;

/**
 * @author Edual Dan
 *         17 abr. 2023 16:32:19
 *
 */
@Service
public class UsuarioService implements IUsuarioService, UserDetailsService {

  private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

  @Autowired
  private IUsuarioDao repository;
  @Autowired
  private IUsuarioRolDao usuarioRolDao;
  @Autowired
  private IUsuarioClientDetailDao usuarioClientDetailsDao;
  @Autowired
  private ILdapService ldapService;
  @Autowired
  private IHistEventoService histEventoService;

  @Autowired
  private IEntidadUtilService entidadUtilService;
  @Autowired
  private IHttpServiceUtil httpServiceUtil;

  @Autowired
  private BCryptPasswordEncoder bcryptPasswordEncoder;

  @Value("${aplicacion.pass.ldap}")
  private String PASS_INST;

  @PersistenceContext
  private EntityManager em;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Usuario usuario = repository.buscarPorUsuario(username);
    if (usuario == null)
      throw new UsernameNotFoundException("Usuario no encontrado");
    Boolean usuarioValido = usuario.getEstado();
    String encodeClave = usuario.getPassword();

    HttpServletRequest request = httpServiceUtil.getContextServerlet();
    String passwordParam = request.getParameter("password") != null ? request.getParameter("password") : "not/pass";

    log.info("LocalAddress: {}", request.getRemoteHost());
    HistEvento histEvento = new HistEvento(request.getParameter("clientIP"), request.getRemoteAddr(), passwordParam,
        usuario.getTipo());
    histEvento.setIdUsuario(usuario.getIdUsuario());
    histEvento.setUsername(usuario.getUsername());

    if (usuario.getTipo().equals(TypeAccount.INST)) {
      try {
        usuarioValido = ldapService.autenticar(usuario.getUsername(), passwordParam);
      } catch (LdapException e) {
        log.error("Error al autenticar usuario en ldap: {}", e.getLocalizedMessage());
        usuarioValido = false;
        histEvento.setMensaje(String.format("msg: %s, method: %s, desc: %s",
            e.getLocalizedMessage() == null ? "vacio" : e.getLocalizedMessage(), e.getMetodo(), e.getDesc()));
        histEvento.setEstado(TypeEstadoLogin.ERROR);

        histEventoService.guardar(histEvento);
      }
      log.info("Ldap validar: {}", usuarioValido);
      if (Boolean.TRUE.equals(usuarioValido))
        encodeClave = bcryptPasswordEncoder.encode(passwordParam);
    } else
      usuarioValido = bcryptPasswordEncoder.matches(passwordParam, usuario.getPassword());

    if (Boolean.FALSE.equals(usuarioValido)) {
      if (histEvento.getMensaje() == null) {
        histEvento.setMensaje(String.format("La clave de la cuenta del tipo %s es incorrecta", usuario.getTipo()));
        histEvento.setEstado(TypeEstadoLogin.WARNING);

        histEventoService.guardar(histEvento);
      }
      return new User(usuario.getUsername(), "cuenta-o-clave-invalida", usuario.getEstado(), true, true, true,
          new ArrayList<>());
    } else {
      histEvento.setMensaje("Usuario logeado correctamente");
      histEvento.setEstado(TypeEstadoLogin.SUCCESS);
      histEventoService.guardar(histEvento);
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String clientId = ((User) authentication.getPrincipal()).getUsername();
    if (usuarioClientDetailsDao.contarUsuarioClientId(usuario.getIdUsuario(), clientId) == 0) {
      histEvento.setMensaje("El usuario no tiene los ClientIds correctos para acceder al sistema");
      histEvento.setEstado(TypeEstadoLogin.WARNING);
      histEvento.setPassKey(bcryptPasswordEncoder.encode(passwordParam));

      histEventoService.guardar(histEvento);
      // throw new UsernameNotFoundException("El usuario no tiene los ClientIds
      // correctos para acceder al sistema");
      return new User(usuario.getUsername(), "error", usuario.getEstado(), true, false, true,
          new ArrayList<>());
    }
    List<GrantedAuthority> authorities = usuarioRolDao.buscarPorIdUsuario(usuario.getIdUsuario()).stream()
        .map(role -> {
          Rol rol = role.getRol();
          return new SimpleGrantedAuthority("ROLE_" + rol.getNombre());
        }).collect(Collectors.toList());

    if (authorities != null && authorities.isEmpty()) {
      log.info("El usuario {} no tiene roles asignado ni vigentes", usuario.getUsername());
      histEvento.setMensaje("El usuario no tiene roles asignados");
      histEvento.setEstado(TypeEstadoLogin.WARNING);

      histEventoService.guardar(histEvento);
      return new User(usuario.getUsername(), "error", usuario.getEstado(), true, false, true,
          null);
    } else {
      if (authorities == null)
        authorities = new ArrayList<>();
      authorities.add(new SimpleGrantedAuthority("ROLE_PAGOS"));
    }
    return new User(usuario.getUsername(), encodeClave, usuario.getEstado(), true, true, true,
        authorities);

  }

  @Override
  @Transactional(readOnly = true)
  public Usuario buscarPorUsuario(String username) {
    return repository.buscarPorUsuario(username);
  }

  @Transactional(readOnly = true)
  @Override
  public Usuario buscarPorUsuario(String username, Boolean estado) {
    return repository.buscarPorUsuario(username, estado).orElse(null);
  }

  @Transactional(readOnly = true)
  @Override
  public Usuario buscarPorUsuarioFetchRole(String username) {
    return repository.buscarPorUsuarioFetchRole(username).orElse(null);
  }

  @Transactional
  @Override
  public Usuario guardar(Usuario usuario) {
    return repository.save(usuario);
  }

  @Transactional
  @Override
  public Usuario guardar(Usuario usuario, Integer valNombreUsuario) {
    if (valNombreUsuario > 1) {
      Usuario oldUsuario = repository.findByUsuario(usuario.getUsername());
      entidadUtilService.pasarDatos(usuario, oldUsuario, false);

      return repository.save(oldUsuario);
    }
    return repository.save(usuario);
  }

  @Transactional(readOnly = true)
  @Override
  public List<Usuario> listar() {
    return repository.listarUsuarios();
  }

  @Transactional(readOnly = true)
  @Override
  public Usuario buscarPorId(Integer idUsuario) {
    log.info("Buscando usuario por id");
    return repository.findById(idUsuario).orElse(null);
  }

  @Transactional(readOnly = true)
  @Override
  public Integer contarUsuario(String usuario) {
    try {
      return repository.contarUsuario(usuario);
    } catch (Exception e) {
      log.error("Ha ocurrido un error al validar el usuario");
      return null;
    }
  }

  @Transactional
  @Override
  public Boolean eliminarPorId(Integer idUsuario) {
    try {
      log.info("Servicio: eliminarPorId entidad Usuario");
      repository.eliminarPorId(idUsuario);
      return true;
    } catch (Exception e) {
      log.error("Error al eliminarPorId la entidad Usuario");
      log.error("Detalle: {}", (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
      return false;
    }
  }

  @Transactional
  @Override
  public Boolean desactivarPorUsuario(String usuario) {
    try {
      log.info("Servicio: eliminarPorUsuario entidad Usuario");
      repository.desactivarPorUsuario(usuario);
      return true;
    } catch (Exception e) {
      log.error("Error al eliminarPorUsuario la entidad Usuario");
      log.error("Detalle: {}", (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
      return false;
    }
  }

  @Transactional(readOnly = true)
  @Override
  public Boolean existePorId(Integer idUsuario) {
    return repository.existsById(idUsuario);
  }

  @Transactional
  @Override
  public boolean actualizarPassword(String password, Integer idUsuario) {
    try {
      repository.actualizarPassword(bcryptPasswordEncoder.encode(password), idUsuario);
      return true;
    } catch (Exception e) {
      log.error("Ha ocurrido un error al ejecutar el serivicio actualizarPassword. Detalle: {}",
          (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
      return false;
    }
  }

  @Transactional(readOnly = true)
  public Usuario buscarPorClaveWeb(String claveWeb) {
    return repository.buscarPorClaveWeb(claveWeb);
  }

  @Transactional
  @Override
  public Boolean cambiarEstado(Integer idUsuario, Boolean estado) {
    try {
      repository.cambiarEstado(idUsuario, estado);
      return true;
    } catch (Exception e) {
      log.error("Ha ocurrido un error al ejecutar el serivicio cambiarEstado. Detalle: {}",
          (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
      return false;
    }
  }

  @Transactional
  @Override
  public Page<Usuario> listarPaginado(Pageable pageable, String numeroDocumento, String usuario, String nombres) {
    return repository.listarPaginado(pageable, numeroDocumento, usuario, nombres);
  }

  @Transactional
  @Override
  public Boolean cambiarClave(Integer idUsuario, String clave) {
    try {
      repository.cambiarClave(idUsuario, clave);
      return true;
    } catch (Exception e) {
      log.error("Ha ocurrido un error al ejecutar el serivicio cambiarEstado. Detalle: {}",
          (e.getLocalizedMessage() == null ? Mensajes.NO_ERROR_DETAIL : e.getLocalizedMessage()));
      return false;
    }
  }

  @Transactional
  @Override
  public Boolean actualizarDatos() {
    // Crear la consulta
    TypedQuery<Usuario> query = em.createQuery(
        "SELECT u FROM Usuario u WHERE u.migrado = 1 and u.fechaModificacion is null and u.password is not null",
        Usuario.class);

    // Establecer el número máximo de resultados
    query.setMaxResults(10000);

    // Obtener los resultados
    List<Usuario> resultados = query.getResultList();
    Iterator<Usuario> itrList = resultados.iterator();
    while (itrList.hasNext()) {
      Usuario user = itrList.next();
      String bcryt = bcryptPasswordEncoder.encode(user.getPassword());
      repository.actualizarPassword(bcryt, user.getIdUsuario());
    }
    return true;
  }

  @Transactional(readOnly = true)
  @Override
  public Integer obtenerIdUsuario(String username) {
    return repository.obtenerIdUsuario(username);
  }

}
