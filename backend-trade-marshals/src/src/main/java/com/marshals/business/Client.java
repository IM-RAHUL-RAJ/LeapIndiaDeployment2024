package com.marshals.business;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class Client {
	
	private String email;
	private String clientId;
	private String password;
	
	private String name;
	//@JsonDeserialize(using = CustomDateDeserializer.class)
	private String dateOfBirth;
	private String country;
	private List<ClientIdentification> identification;
	@JsonDeserialize(using = CustomBooleanDeserializer.class)
	private boolean isAdmin;
	
	public Client() {}

	public Client(String email, String clientId, String password, String name, String dateOfBirth, String country,
			List<ClientIdentification> identification, boolean isAdmin) {
		try {
			if(email==null || clientId==null || password==null || name==null 
					||dateOfBirth==null || country==null || identification==null) 
				throw new NullPointerException("Client Details cannot be null");
			//Only checking validity of fields that are not explicitly covered in Angular
			if(!country.equals("India") && !country.equals("USA"))
				throw new IllegalArgumentException("Country not covered");
			this.email = email;
			this.clientId = clientId;
			this.password = password;
			this.name = name;
			this.dateOfBirth = dateOfBirth;
			this.country = country;
			this.identification = identification;
			this.isAdmin = isAdmin;
		}  catch(NullPointerException e) {
			throw e;
		} catch(IllegalArgumentException e) {
			throw e;
		}
		
	}
		
	//Getters
	public String getEmail() {
		return email;
	}

	public String getClientId() {
		return clientId;
	}
	
	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}
	
	public Date getDateOfBirth() {
		if(this.dateOfBirth!=null) {
			java.util.Date parsedDate;
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy"); //doB Format stored in client model
			try {
				parsedDate = dateFormat.parse(this.dateOfBirth);
			} catch (ParseException e) { //If there is an error, assign it will date 18 years back (Min age)
				parsedDate = java.sql.Date.valueOf(LocalDate.now().minusYears(18)); 
			}
            return new Date(parsedDate.getTime());
		}
		return null;
	}

	public String getCountry() {
		return country;
	}

	public List<ClientIdentification> getIdentification() {
		return identification;
	}

	public String getIsAdmin() {
		return this.isAdmin?"Y":"N";
	}
	
	//Setters
	public void setEmail(String email) {
		this.email = email;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDateOfBirth(Date dateOfBirth) {
	    if (dateOfBirth != null) {
    		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy"); //doB Format stored in client model
            this.dateOfBirth = dateFormat.format(dateOfBirth); 
	    } else {
            this.dateOfBirth = null; // Or handle null appropriately
	    }
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setIdentification(List<ClientIdentification> identification) {
		this.identification = identification;
	}

	public void setAdmin(String isAdmin) {
		this.isAdmin = isAdmin.equals("Y");
	}

	@Override
	public int hashCode() {
		return Objects.hash(clientId, country, dateOfBirth, email, identification, isAdmin, name, password);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Client other = (Client) obj;
		return Objects.equals(clientId, other.clientId) && Objects.equals(country, other.country)
				&& Objects.equals(dateOfBirth, other.dateOfBirth) && Objects.equals(email, other.email)
				&& Objects.equals(identification, other.identification) && isAdmin == other.isAdmin
				&& Objects.equals(name, other.name) && Objects.equals(password, other.password);
	}

	@Override
	public String toString() {
		return "Client [email=" + email + ", clientId=" + clientId + ", password=" + password + ", name=" + name
				+ ", dateOfBirth=" + dateOfBirth + ", country=" + country + ", identification=" + identification
				+ ", isAdmin=" + isAdmin + "]";
	}
	
}
