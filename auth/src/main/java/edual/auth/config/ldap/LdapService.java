package edual.auth.config.ldap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.stereotype.Service;

@Service
public class LdapService implements ILdapService {
	private static final Logger log = LoggerFactory.getLogger(LdapService.class);

	private static final String[] ATRIBUTOS = { "userAccountControl", "correo", "countryCode", "description",
			"fechaNacimiento", "givenName", "homePhone", "mail", "sn", "mobile", "streetAddress", "uid", "whenChanged",
			"whenCreated", "samAccountName", "snMaternal", "snPaternal", "accountExpires" };

	private static final String T_STRING = "string";
	private static final String T_INT = "integer";
	private static final String T_DATE_TIME = "fechaHora";
	private static final String T_DATE = "fecha";
	private static final String T_TIME = "time";

	private static final String[] TIPOS_DATOS = { T_STRING, T_STRING, T_INT, T_STRING,
			T_DATE, T_STRING, T_STRING, T_STRING, T_STRING, T_STRING, T_STRING, T_STRING, T_DATE_TIME,
			T_DATE_TIME, T_STRING, T_STRING, T_STRING, T_TIME };

	private static final String ERROR_CONVERTER = "No se puedo convertir el tipoDato: {}, posición: {}";

	@Autowired
	private LdapContextSource contextSource;
	@Autowired
	private ILdapAtributeService atributeService;

	public boolean authenticateContext(String username, String password) {
		DirContext context = null;
		if (password.isEmpty())
			return false;
		try {
			context = contextSource.getContext(
					String.format("cn=%s,cn=users,%s", username, contextSource.getBaseLdapPathAsString()), password);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (context != null) {
				try {
					context.close();
				} catch (NamingException e) {
					if (log.isDebugEnabled())
						e.printStackTrace();
				}
			}
		}
	}

	@Override
	public Boolean pingLdap() {
		Context context = null;
		try {
			context = contextSource.getContext(contextSource.getUserDn(), contextSource.getPassword());
			log.info("LDAP server is up and running");
			return true;
		} catch (Exception e) {
			log.error("LDAP server is not available");
			return false;
		} finally {
			if (context != null) {
				try {
					context.close();
				} catch (NamingException e) {
					if (log.isDebugEnabled())
						e.printStackTrace();
				}
			}
		}
	}

	@Override
	public List<UserLdap> searchUser(AndFilter filter) throws LdapException {
		List<UserLdap> users = new ArrayList<>();
		DirContext context = null;

		try {
			// Autenticar al usuario en el servidor LDAP
			context = contextSource.getContext(contextSource.getUserDn(), contextSource.getPassword());
			log.info("Autenticación LDAP exitosa: {}", contextSource.getUserDn());

		} catch (Exception e) {
			String desc = "No se pudo autenticar o verificar la cuenta del usuario: " + contextSource.getUserDn()
					+ (e.getLocalizedMessage() == null ? "Sin detalle de la excepción" : "Detalle: " + e.getLocalizedMessage());
			log.error(desc);
			throw new LdapException(
					"No se pudo autenticar o verificar la cuenta del usuario-adm.", "searchUser", desc);
		}

		SearchControls searchCtls = new SearchControls();
		searchCtls.setReturningAttributes(ATRIBUTOS);
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		log.info("Filter: {}", filter);
		NamingEnumeration<SearchResult> answer = null;

		try {
			answer = context.search("", filter.toString(), searchCtls);
		} catch (NamingException e) {

			if (log.isDebugEnabled())
				e.printStackTrace();
		} finally {
			try {
				context.close();
			} catch (NamingException e) {
				if (log.isDebugEnabled())
					e.printStackTrace();
			}
		}
		if (answer == null)
			return Collections.emptyList();

		int totalResults = 0;

		while (answer.hasMoreElements()) {
			totalResults++;
			SearchResult sr = answer.nextElement();
			if (log.isDebugEnabled())
				log.info("Answer: {}: {}", totalResults, sr.toString());
			UserLdap user = new UserLdap();

			Attributes attributes = sr.getAttributes();
			for (int i = 0; i < ATRIBUTOS.length; i++) {
				selecionarTipoDato(user, attributes.get(ATRIBUTOS[i]), ATRIBUTOS[i], TIPOS_DATOS[i]);
			}
			users.add(user);
		}
		log.info("Total resultados: {}", totalResults);
		return users;
	}

	@Override
	public Boolean autenticar(String username, String password) throws LdapException {
		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("objectClass", "user")).and(new EqualsFilter("samAccountName", username));
		List<UserLdap> users = searchUser(filter);

		if (users.isEmpty())
			return false;

		if (users.size() != 1)
			return false;

		UserLdap unico = users.get(0);

		return authenticateContext(unico.getSamAccountName(), password);
	}

	private void selecionarTipoDato(UserLdap user, Attribute attribute, String nombreAtributo, String tipoDato) {
		try {

			switch (tipoDato) {
				case T_STRING: {
					if (attribute != null)
						this.ejutarSetter(user, nombreAtributo, attribute.get().toString());
					break;
				}
				case T_INT: {
					if (attribute != null)
						this.ejutarSetter(user, nombreAtributo,
								atributeService.convertirInteger(attribute.get().toString()));
					break;
				}

				case T_DATE: {
					if (attribute != null)
						this.ejutarSetter(user, nombreAtributo,
								atributeService.convertirFecha(attribute.get().toString()));
					break;
				}
				case T_DATE_TIME: {
					if (attribute != null)
						this.ejutarSetter(user, nombreAtributo,
								atributeService.convertirDateTime(attribute.get().toString()));
					break;
				}
				case T_TIME: {
					if (attribute != null)
						this.ejutarSetter(user, nombreAtributo,
								atributeService.convertirTime(attribute.get().toString()));
					break;
				}
				default:
					throw new IllegalArgumentException("Unexpected value: " + nombreAtributo);
			}
		} catch (Exception e) {
			log.error("No se pudo parsear el tipo de datos en el {}: {}", tipoDato, nombreAtributo);
		}
	}

	public void ejutarSetter(UserLdap objeto, String atributo, Object valor) {
		String nombreMetodo = String.format("set%s%s", atributo.substring(0, 1).toUpperCase(), atributo.substring(1));

		try {
			Method metodo = objeto.getClass().getMethod(nombreMetodo, valor.getClass());
			metodo.invoke(objeto, valor);
		} catch (Exception e) {
			if (log.isDebugEnabled())
				e.printStackTrace();
			log.error(ERROR_CONVERTER, valor.getClass(), atributo + ": " + valor.toString());
		}
	}

	@Override
	public Optional<UserLdap> searchUsername(String username) {
		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("objectClass", "user")).and(new EqualsFilter("samAccountName", username));
		List<UserLdap> users = new ArrayList<>();
		try {
			searchUser(filter);
		} catch (LdapException e) {
			log.error("Error al buscar el usuario: {}", e.getLocalizedMessage());
			users = Collections.emptyList();
		}

		if (!users.isEmpty()) {
			return Optional.ofNullable(users.get(0));
		} else {
			return Optional.empty();
		}
	}

}
