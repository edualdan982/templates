package edual.auth.entity.auth.oauth;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "oauth_access_token", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "clientId", "userName" }, name = "access_username_uk") })
public class AccessToken implements Serializable {

	private String tokenId;

	@Lob
	@Column(columnDefinition = "BLOB")
	private String token;

	@Id
	private String authenticationId;

	private String userName;
	private String clientId;

	@Lob
	@Column(columnDefinition = "BLOB")
	private String authentication;

	private String refreshToken;

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAuthenticationId() {
		return authenticationId;
	}

	public void setAuthenticationId(String authenticationId) {
		this.authenticationId = authenticationId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	private static final long serialVersionUID = 1L;
}
