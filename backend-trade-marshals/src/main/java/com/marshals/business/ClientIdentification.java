package com.marshals.business;

import java.util.Objects;

public class ClientIdentification {
	
	private String type;
	private String value;
	
	public ClientIdentification() {}
	
	public ClientIdentification(String type, String value) {
		try {
			if(type==null || value==null) throw new NullPointerException("Govt ID Details cannot be null");
			//Only checking validity of fields that are not explicitly covered in Angular
			if(!type.equals("Aadhar") && !type.equals("PAN") && !type.equals("SSN"))
				throw new IllegalArgumentException("Invalid Govt ID Type");
			this.type = type;
			this.value = value;
		
		} catch(NullPointerException e) {
			throw e;
		} catch(IllegalArgumentException e) {
			throw e;
		}
	}

	//Getters
	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}
	
	//Setters
	public void setType(String type) {
		this.type = type;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClientIdentification other = (ClientIdentification) obj;
		return Objects.equals(type, other.type) && Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return "ClientIdentification [type=" + type + ", value=" + value + "]";
	}
	

}
