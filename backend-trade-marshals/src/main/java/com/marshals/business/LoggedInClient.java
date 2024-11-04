package com.marshals.business;

import java.math.BigDecimal;
import java.util.Objects;

public class LoggedInClient {
	private Client client;
	private Integer token;
	
	public LoggedInClient() {}
	
	public LoggedInClient(Client client, Integer token) {
		try {
			if(client == null || token == null) throw new NullPointerException("Logging in Client details cannot be null");
			this.client = client;
			this.token = token;
		} catch(NullPointerException e) {
			throw e;
		}
	}
	
	public Client getClient() {
		return client;
	}
	
	public Integer getToken() {
		return token;
	}

	@Override
	public int hashCode() {
		return Objects.hash(client, token);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoggedInClient other = (LoggedInClient) obj;
		return Objects.equals(client, other.client) && Objects.equals(token, other.token);
	}

	@Override
	public String toString() {
		return "LoggedInClient [client=" + client + ", token=" + token + "]";
	}
	
	
}
