package edual.auth.entity.auth.oauth;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Table(name = "oauth_code")
public class Code implements Serializable {
	@Id
	private String code;

	@Lob
	@Column(columnDefinition = "BLOB")
	private String authentication;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}

	private static final long serialVersionUID = 1L;

}
