package edual.auth.config.ldap;

import java.util.List;
import java.util.Optional;

import org.springframework.ldap.filter.AndFilter;

public interface ILdapService {
	boolean authenticateContext(String username, String password);

	Boolean pingLdap();

	List<UserLdap> searchUser(AndFilter filter) throws LdapException;

	Boolean autenticar(String username, String password) throws LdapException;

	Optional<UserLdap> searchUsername(String username);
}
