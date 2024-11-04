package com.marshals.business;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientTest {
	
	private Client client;

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
		client = null;
	}

	@Test
	void testForEqualsClientObject() {
		client = new Client("john.doe@gmail.com","739982664", "Password123", "John Doe", "08/11/2002", "India", 
						new ArrayList<>(List.of(new ClientIdentification("Aadhar","123456789102"))), false);
		assertEquals(client, new Client("john.doe@gmail.com","739982664", "Password123", "John Doe", "08/11/2002", "India", 
				new ArrayList<>(List.of(new ClientIdentification("Aadhar","123456789102"))), false) , "Client details should be equal");
	}
	
	@Test
	void testForNotEqualsOfClientObjectWithDiffEmails() {
		client = new Client("john.doe@gmail.com","739982664", "Password123", "John Doe", "08/11/2002", "India", 
						new ArrayList<>(List.of(new ClientIdentification("Aadhar","123456789102"))), false);
		assertNotEquals(client, new Client("john@gmail.com","1234567890", "Password123", "John Doe", "08/11/2002", "India", 
				new ArrayList<>(List.of(new ClientIdentification("Aadhar","123456789102"))), false) , "Client details should not be equal");
	}
	
	@Test
	void testForNotEqualsOfClientObjectWithDiffIDs() {
		client = new Client("john.doe@gmail.com","739982664", "Password123", "John Doe", "08/11/2002", "India", 
						new ArrayList<>(List.of(new ClientIdentification("Aadhar","123456789102"))), false);
		assertNotEquals(client, new Client("john.doe@gmail.com","1234567890", "Password123", "John Doe", "08/11/2002", "India", 
				new ArrayList<>(List.of(new ClientIdentification("PAN","ABCDE1234F"))), false) , "Client details should not be equal");
	}
	
	@Test
	void testForEqualHashCodeOfClientObject() {
		client = new Client("john.doe@gmail.com","739982664", "Password123", "John Doe", "08/11/2002", "India", 
				new ArrayList<>(List.of(new ClientIdentification("Aadhar","123456789102"))), false);
		Client expectedClient = new Client("john.doe@gmail.com","739982664", "Password123", "John Doe", "08/11/2002", "India", 
				new ArrayList<>(List.of(new ClientIdentification("Aadhar","123456789102"))), false);
		assertEquals(client.hashCode(), expectedClient.hashCode());
	}
	
	//NullPointerException
	@Test
	void testNullClientIDInitialization() {
		Exception e = assertThrows(NullPointerException.class, () -> {
			client = new Client("john.doe@gmail.com",null, "Password123", "John Doe", "08/11/2002", "India", 
					new ArrayList<>(List.of(new ClientIdentification("Aadhar","123456789102"))), false);
		});
		assertEquals("Client Details cannot be null",e.getMessage());
	}
	
	//IllegalArgumentException
	@Test
	void testInvalidCountryInitialization() {
		Exception e = assertThrows(IllegalArgumentException.class, () -> {
			client = new Client("john.doe@gmail.com","739982664", "Password123", "John Doe", "08/11/2002", "Singapore", 
					new ArrayList<>(List.of(new ClientIdentification("Aadhar","123456789102"))), false);
		});
		assertEquals("Country not covered",e.getMessage());
	}
	
	@Test
	void testInvalidGovtIDDetailsInitialization() {
		Exception e = assertThrows(IllegalArgumentException.class, () -> {
			client = new Client("john.doe@gmail.com","739982664", "Password123", "John Doe", "08/11/2002", "India", 
					new ArrayList<>(List.of(new ClientIdentification("Passport","A1234567"))), false);
		});
		assertEquals("Invalid Govt ID Type",e.getMessage());
	}

}
