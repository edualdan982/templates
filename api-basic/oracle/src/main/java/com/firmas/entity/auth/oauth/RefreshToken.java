package com.firmas.entity.auth.oauth;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "oauth_refresh_token")
public class RefreshToken implements Serializable {

	@Id
	private String tokenId;

	@Lob
	@Column(columnDefinition = "BLOB")
	private byte[] token;

	@Lob
	@Column(columnDefinition = "BLOB")
	private byte[] authentication;

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public byte[] getToken() {
		return token;
	}

	public void setToken(byte[] token) {
		this.token = token;
	}

	public byte[] getAuthentication() {
		return authentication;
	}

	public void setAuthentication(byte[] authentication) {
		this.authentication = authentication;
	}

	private static final long serialVersionUID = 1L;

}
