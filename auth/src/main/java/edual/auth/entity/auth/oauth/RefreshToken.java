package edual.auth.entity.auth.oauth;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "oauth_v1_refresh_token")
public class RefreshToken implements Serializable {

	@Id
	private String tokenId;

	@Lob
	@Column(columnDefinition = "BLOB")
	private String token;

	@Lob
	@Column(columnDefinition = "BLOB")
	private String authentication;

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

	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}

	private static final long serialVersionUID = 1L;

}
