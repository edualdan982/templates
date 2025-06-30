package edual.auth.entity.auth.oauth;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import edual.auth.validators.MatchStrings;

/**
 * @author Edual Dan
 *         17 abr. 2023 16:32:23
 *
 */
@Entity
@Table(name = "oauth_client_details")
public class ClientDetail implements Serializable {

	@Id
	@NotEmpty
	private String clientId;

	private String resourceIds;

	private String clientSecret;

	@MatchStrings(fieldMatch = { "read", "write" })
	private String scope;

	@MatchStrings(fieldMatch = { "password", "refresh_token", "client_credentials", "authorization_code" })
	private String authorizedGrantTypes;

	private String webServerRedirectUri;

	@MatchStrings(fieldMatch = { "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT" })
	private String authorities;
	private Integer accessTokenValidity;
	private Integer refreshTokenValidity;

	@Column(length = 4000)
	private String additionalInformation;
	private String autoapprove;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(String resourceIds) {
		this.resourceIds = resourceIds;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		if (scope != null)
			this.scope = scope.replaceAll(" +", "").toLowerCase();
		else
			this.scope = scope;
	}

	public String getAuthorizedGrantTypes() {
		return authorizedGrantTypes;
	}

	public void setAuthorizedGrantTypes(String authorizedGrantTypes) {
		if (authorizedGrantTypes != null)
			this.authorizedGrantTypes = authorizedGrantTypes.replaceAll(" +", "").toLowerCase();
		else
			this.authorizedGrantTypes = authorizedGrantTypes;
	}

	public String getWebServerRedirectUri() {
		return webServerRedirectUri;
	}

	public void setWebServerRedirectUri(String webServerRedirectUri) {
		this.webServerRedirectUri = webServerRedirectUri;
	}

	public String getAuthorities() {
		return authorities;
	}

	public void setAuthorities(String authorities) {
		if (authorities != null)
			this.authorities = authorities.replaceAll(" +", "").toUpperCase();
		else
			this.authorities = authorities;
	}

	public Integer getAccessTokenValidity() {
		return accessTokenValidity;
	}

	public void setAccessTokenValidity(Integer accessTokenValidity) {
		this.accessTokenValidity = accessTokenValidity;
	}

	public Integer getRefreshTokenValidity() {
		return refreshTokenValidity;
	}

	public void setRefreshTokenValidity(Integer refreshTokenValidity) {
		this.refreshTokenValidity = refreshTokenValidity;
	}

	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	public String getAutoapprove() {
		return autoapprove;
	}

	public void setAutoapprove(String autoapprove) {
		this.autoapprove = autoapprove;
	}

	private static final long serialVersionUID = 1L;
}
