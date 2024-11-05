package com.marshals.business;
 
import java.math.BigDecimal;
import java.util.Objects;
 
public class FMTSValidatedClient {
	private String clientId;
	private String email;
	private Integer token;
	public FMTSValidatedClient() {}
	public FMTSValidatedClient(String clientId, String email, Integer token) {
		try {
			if(email==null || clientId==null || token == null) 
				throw new NullPointerException("Validated Client Details cannot be null");
			this.email = email;
			this.clientId = clientId;
			this.token = token;
		} catch(NullPointerException e) {
			throw e;
		}
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
 
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getToken() {
		return token;
	}
	public void setToken(Integer token) {
		this.token = token;
	}
	@Override
	public int hashCode() {
		return Objects.hash(clientId, email, token);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FMTSValidatedClient other = (FMTSValidatedClient) obj;
		return Objects.equals(clientId, other.clientId) && Objects.equals(email, other.email)
				&& Objects.equals(token, other.token);
	}
	@Override
	public String toString() {
		return "FMTSValidatedClient [clientId=" + clientId + ", email=" + email + ", token=" + token + "]";
	}
 
}