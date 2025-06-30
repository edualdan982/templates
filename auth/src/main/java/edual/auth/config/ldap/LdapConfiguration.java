package edual.auth.config.ldap;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
public class LdapConfiguration {

	@Value("${spring.ldap.urls}")
	private String ldapUrls;

	@Value("${spring.ldap.base}")
	private String ldapBaseDn;

	@Value("${spring.ldap.username}")
	private String ldapUsername;

	@Value("${spring.ldap.password}")
	private String ldapPassword;

	@Bean
	public LdapContextSource contextSource() {

		Map<String, Object> baseEnv = new HashMap<>();
		baseEnv.put("com.sun.jndi.ldap.connect.timeout", "1000");
		baseEnv.put("com.sun.jndi.ldap.read.timeout", "5000");

		baseEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		baseEnv.put(Context.SECURITY_AUTHENTICATION, "simple");

		LdapContextSource contextSource = new LdapContextSource();
		contextSource.setBaseEnvironmentProperties(baseEnv);

		contextSource.setUrl(ldapUrls);
		contextSource.setUserDn(ldapUsername);
		contextSource.setPassword(ldapPassword);
		contextSource.setBase(ldapBaseDn);

		contextSource.afterPropertiesSet();

		return contextSource;
	}

	@Bean
	public LdapTemplate ldapTemplate() {
		LdapTemplate template = new LdapTemplate(contextSource());
		template.setDefaultTimeLimit(5);
		return template;
	}

}
