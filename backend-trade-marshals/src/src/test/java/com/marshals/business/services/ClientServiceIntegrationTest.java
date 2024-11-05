package com.marshals.business.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTable;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTableWhere;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.marshals.business.Client;
import com.marshals.business.ClientIdentification;
import com.marshals.business.LoggedInClient;

@SpringBootTest
@Transactional
class ClientServiceIntegrationTest {

	@Autowired
	private ClientService service;
	
	@Autowired
	private JdbcTemplate testJdbcTemplate;
	
	//Few test client
	Client client1654658069 = new Client("sowmya@gmail.com","1654658069", "Marsh2024", "Sowmya", "11/12/2002", "India", 
			new ArrayList<>(List.of(new ClientIdentification("Aadhar","123456789102"))), true); //Existing client
	Client client1654658000 = new Client("nonexistent.client@gmail.com","1654658000", "Password1234", "Non Existent client", "12/10/1994", "USA", 
			new ArrayList<>(List.of(new ClientIdentification("SSN","1953826343"))), false); //Non existent client
	Client client1654658070 = new Client("sam@gmail.com","1654658070", "Password1234", "Sam", "12/11/2000", "USA", 
			new ArrayList<>(List.of(new ClientIdentification("SSN","1643846323"))), false); //New client to insert
	
	@AfterEach
	void tearDown() throws Exception {
		service = null;
	}
	
	/*CLIENT*/
	 
	/*TESTS FOR CLIENT EMAIL VERIFICATION*/
	//Existing email to be found
	@Test
	void testForClientWithExistingEmailToBeFound() {
		String existingEmail = "sowmya@gmail.com";
		Boolean clientExists = service.verifyClientEmail(existingEmail);
		assertTrue(clientExists, "Client must be found");
	}
	//Non existing email to not be found
	@Test
	void testForClientWithNonExistingEmailToNotBeFound() {
		String nonExistingEmail = "client_nonexist@gmail.com";
		Boolean clientExists = service.verifyClientEmail(nonExistingEmail);
		assertFalse(clientExists, "Client must not be found");
	}
	//Invalid email to throw exception
	@Test
	void testForClientWithInvalidEmailToThrowException() {
		String invalidEmail = "client_invalid";
		Exception e = assertThrows(IllegalArgumentException.class, () -> {
			service.verifyClientEmail(invalidEmail);
		});
		assertEquals(e.getMessage(),"Client Email Format is invalid");
	}
	
	/*TESTS FOR CLIENT LOGIN*/
	//Successful login of client
	@Test
	void testForSuccessfulRetrievalOfClientDetailsWithValidCredentialsFromService() {
		String validClientEmail = "sowmya@gmail.com";
		String validClientPassword = "Marsh2024";
		LoggedInClient client = service.loginExistingClient(validClientEmail, validClientPassword);
		assertEquals(client1654658069, client.getClient(), "Successful login must return Client details");
	}
	//Login of client with non existent email
	@Test
	void testForLoginOfNonExistentClientThrowsExceptionFromService() {
		Exception e = assertThrows(IllegalArgumentException.class, () -> {
			service.loginExistingClient(client1654658000.getEmail(), client1654658000.getPassword());
		}); 
		assertEquals(e.getMessage(), "Client with given email is not registered");
	}
	//Login of existing client with invalid credentials - password mismatch
	@Test
	void testForLoginOfExistingClientWithInvalidCredentailsThrowsExceptionFromService() {
		String existingClientEmail = "sowmya@gmail.com";
		String invalidClientPassword = "Password123";
		Exception e = assertThrows(IllegalArgumentException.class, () -> {
			service.loginExistingClient(existingClientEmail, invalidClientPassword);
		}); 
		assertEquals(e.getMessage(), "Password does not match logging in Client's credentials");
	}
	
//	/*TESTS FOR CLIENT REGISTRATION*/
	
	//Successful insetion must increase row count in client table
	@Test
	void testForSuccessfulAdditionOfNewClientDetailsInClientTableFromService() {
		int oldCount = countRowsInTable(testJdbcTemplate, "client");
		List<ClientIdentification> identificationList = new ArrayList<>(List.of(new ClientIdentification("SSN","1643846323")));
		LoggedInClient client = service.registerNewClient("sam@gmail.com","Password1234", "Sam", "12/11/2000", "USA", identificationList);
		int newCount = countRowsInTable(testJdbcTemplate, "client");
		assertTrue(newCount == oldCount+1, "Client Table count must increase by one");
	}
	//Successful insertion must add correct data in client identification table
	@Test
	void testForSuccessfulAdditionOfNewClientDetailsInClientIdentificationTableFromService() {
		
		List<ClientIdentification> identificationList = new ArrayList<>(List.of(new ClientIdentification("SSN","1643846323")));
		LoggedInClient newClient = service.registerNewClient("sam@gmail.com","Password1234", "Sam", "12/11/2000", "USA", identificationList);
		String newClientId = newClient.getClient().getClientId();
		String whereCondition = "client_id = "+ newClientId;
		int newCount = countRowsInTableWhere(testJdbcTemplate, "client_identification", whereCondition);
		System.out.println(newCount);
		assertTrue(newCount >= 1, "One or more client identification details added");
	}
	//Exisitng client registration throws error
	@Test
	void testForAdditionOfExistingClientDetailsShouldThrowExceptionFromService(){
		Exception e = assertThrows(IllegalArgumentException.class, () -> {
			List<ClientIdentification> identificationList = new ArrayList<>(List.of(new ClientIdentification("Aadhar","123456789102")));
			service.registerNewClient("sowmya@gmail.com", "Marsh2024", "Sowmya", "11/12/2002", "India", identificationList);
		});
		assertEquals(e.getMessage(),"Client with given email is already registered");
	}
	
	@Test
	void testShouldHandleRegisterationOfClientWithIDDetailsOfExistingClientFromService() {
		List<ClientIdentification> identificationList = new ArrayList<>(List.of(new ClientIdentification("Aadhar","123456789102")));
		Exception e = assertThrows(IllegalArgumentException.class, () -> {
			service.registerNewClient("sam@gmail.com","Password1234", "Sam", "12/11/2000", "USA",
					identificationList);
		});
		assertEquals("Client with given Identification Details is already registered with another email",e.getMessage());
	}

}
