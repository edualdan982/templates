package edual.auth.config.auth;

import java.util.Arrays;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Override
	public void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeRequests(requests -> requests
				.antMatchers(
					HttpMethod.GET, "/estado", "/clave/**", "/prueba/**", "/renotificar/**",
						"/images/**", "/css/**", "/js/**", "/param/**", "/callback/pago-exitoso/**",
						"/open/**", "/gestion/listar", 
						"/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/pago-exitoso/**", "/comunicado/**"
				)
				.permitAll()
				.antMatchers(
					HttpMethod.POST, "/simulacion/**", "/libra/**", "/open/**")
				.permitAll()
				.anyRequest()
				.authenticated()).cors(cors -> cors.configurationSource(corsConfigurationSource()));
		//@formatter:on 
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

		config
				.setAllowedOrigins(Arrays.asList("http://localhost:4200", "https://pasarela.umsa.bo", "http://localhost:55351",
						"https://venta-valores.umsa.bo", "http://localhost:8081"));

		config.setAllowCredentials(true);
		config.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {
		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(
				new CorsFilter(corsConfigurationSource()));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}

}
