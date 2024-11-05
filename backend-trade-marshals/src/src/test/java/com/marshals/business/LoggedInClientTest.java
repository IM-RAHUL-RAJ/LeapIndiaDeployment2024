package com.marshals.business;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoggedInClientTest {

	private LoggedInClient loggedInClient;
	private Client client;
	
	@BeforeEach
	void setUp() throws Exception {
		client = new Client("john.doe@gmail.com","739982664", "Password123", "John Doe", "08/11/2002", "India", 
				new ArrayList<>(List.of(new ClientIdentification("Aadhar","123456789102"))), false);
		loggedInClient = new LoggedInClient(client, 739982664);
	}

	@AfterEach
	void tearDown() throws Exception {
		client = null;
		loggedInClient = null;
	}

	@Test
	void testForEqualsLoggedInClientObject() {
		assertEquals(loggedInClient, new LoggedInClient(client, 739982664) , 
				"Logged In Client details should be equal");
	}
	
	@Test
	void testForNotEqualsLoggedInClientObject() {
		assertNotEquals(loggedInClient, new LoggedInClient(client, 134) , 
				"Logged In Client details should have equal hashcodes");
	}
	
	@Test
	void testForHashCodeLoggedInClientObject() {
		assertEquals(loggedInClient.hashCode(), new LoggedInClient(client,739982664).hashCode() , 
				"Logged In Client details should not have equal hashcodes");
	}
	
	//NullPointerException
	@Test
	void testNullClientInitializationOfLoggedInClient() {
		Exception e = assertThrows(NullPointerException.class, () -> {
			loggedInClient = new LoggedInClient(null, 739982664);
		});
		assertEquals("Logging in Client details cannot be null",e.getMessage());
	}

}
