package com.marshals.restcontroller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import com.marshals.business.Client;
import com.marshals.business.ClientIdentification;
import com.marshals.business.LoggedInClient;

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"},
     executionPhase = ExecutionPhase.AFTER_TEST_METHOD) 
class ClientControllerE2ETest {
	@Autowired
	private TestRestTemplate restTemplate; // for executing rest endpoints

	@Autowired
	private JdbcTemplate jdbcTemplate;  // for executing SQL queries
	
	// Test Client details
	List<Client> clientList = new ArrayList<Client>(List.of(
			new Client("sowmya@gmail.com", "1654658069", "Marsh2024", "Sowmya", "11/12/2002", "India",
					new ArrayList<>(List.of(new ClientIdentification("Aadhar", "123456789102"))), true), // Existing
																											// client
			new Client("nonexistent.client@gmail.com", "1654658000", "Password1234", "Non Existent client",
					"12/10/1994", "USA", new ArrayList<>(List.of(new ClientIdentification("SSN", "1953826343"))),
					false), // Non Existent client
			new Client("sam@gmail.com", "767836496", "Password1234", "Sam", "12/11/2000", "USA",
					new ArrayList<>(List.of(new ClientIdentification("SSN", "1643846323"))), false) // New client to be
																									// inserted
	));
	
	// Smoke Test for Mock MVC object
	@Test
	public void testRestTemplateToBeInstantiated() {
		assertNotNull(restTemplate);
	}
	
	/*Tests for Client Email Verification*/
	@Test
	public void testSuccessfulEmailVerificationOfExistingEmail_RespondsWith200() {
		String email = clientList.get(0).getEmail();
		String requestUrl = "/client/verify-email/"+email;
		ResponseEntity<VerificationRequestResult> response = 
			restTemplate.getForEntity(requestUrl, VerificationRequestResult.class);
		// ASSERT
		assertEquals(HttpStatus.OK, response.getStatusCode()); // verify the response HTTP status is OK
		VerificationRequestResult responseVerification = response.getBody();
		assertTrue(responseVerification.getIsVerified());  
	}
	@Test
	public void testSuccessfulEmailVerificationOfNonExistingEmail_RespondsWith200() {
		String email = clientList.get(1).getEmail();
		String requestUrl = "/client/verify-email/"+email;
		ResponseEntity<VerificationRequestResult> response = 
			restTemplate.getForEntity(requestUrl, VerificationRequestResult.class);
		// ASSERT
		assertEquals(HttpStatus.OK, response.getStatusCode()); // verify the response HTTP status is OK
		VerificationRequestResult responseVerification = response.getBody();
		assertFalse(responseVerification.getIsVerified());  
	}
	@Test
	public void testEmailVerificationWithInvalidEmail_RepondsWith406() {
		String email = "invalid-email";
		String requestUrl = "/client/verify-email/"+email;
		ResponseEntity<VerificationRequestResult> response = 
			restTemplate.getForEntity(requestUrl, VerificationRequestResult.class);
		// ASSERT
		assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode()); 
	}
	
	/*Tests for Client Registration*/
	@Test
	public void testSuccessfulNewClientRegistration_RespondsWith200() {
		Client client = clientList.get(2);
		
		int rowsBeforeInsert = countRowsInTable(jdbcTemplate, "client");
		
		String requestUrl = "/client/register";
		ResponseEntity<LoggedInClient> response = 
				restTemplate.postForEntity(requestUrl, client, LoggedInClient.class);
		
		int rowsAfterInsert = countRowsInTable(jdbcTemplate, "client");
		// ASSERT
		assertEquals(HttpStatus.OK, response.getStatusCode()); // verify the response HTTP status is OK
		LoggedInClient newClient = response.getBody();
		assertEquals(newClient.getClient(),client);  
		assertTrue(rowsAfterInsert == rowsBeforeInsert+1); //1 row to have been added
	}
	@Test
	public void testClientRegistrationOfExistingClient_RespondsWith406() {
		Client client = clientList.get(0); //Existing client
		
		int rowsBeforeInsert = countRowsInTable(jdbcTemplate, "client");
		
		String requestUrl = "/client/register";
		ResponseEntity<LoggedInClient> response = 
				restTemplate.postForEntity(requestUrl, client, LoggedInClient.class);
		
		int rowsAfterInsert = countRowsInTable(jdbcTemplate, "client");
		// ASSERT
		assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());  
		assertTrue(rowsAfterInsert == rowsBeforeInsert); //No row has been added
	}
	@Test
	public void testClientRegistrationWithNullClientEmail_RespondsWith406() {
		Client client = clientList.get(2); //New client
		client.setEmail(null); //Null Client Email
		
		int rowsBeforeInsert = countRowsInTable(jdbcTemplate, "client");
		
		String requestUrl = "/client/register";
		ResponseEntity<LoggedInClient> response = 
				restTemplate.postForEntity(requestUrl, client, LoggedInClient.class);
		
		int rowsAfterInsert = countRowsInTable(jdbcTemplate, "client");
		// ASSERT
		assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());  
		assertTrue(rowsAfterInsert == rowsBeforeInsert); //No row has been added
	}
	
	/*Tests for Client Login*/
	@Test
	public void testSuccessfulClientLogin_RespondsWith200() {
		Client client = clientList.get(0);
		String email = client.getEmail();
		String password = client.getPassword();
		String requestUrl = "/client?email="+email+"&password="+password;
		ResponseEntity<LoggedInClient> response = 
			restTemplate.getForEntity(requestUrl, LoggedInClient.class);
		// ASSERT
		assertEquals(HttpStatus.OK, response.getStatusCode()); // verify the response HTTP status is OK
		LoggedInClient responseClient = response.getBody();
		assertEquals(responseClient.getClient(),client);  
	}
	@Test
	public void testClientLoginWithNonExistingEmail_RespondsWith406() {
		String email = clientList.get(1).getEmail(); //Non Existing email
		String password = clientList.get(1).getPassword();
		String requestUrl = "/client?email="+email+"&password="+password;
		ResponseEntity<LoggedInClient> response = 
			restTemplate.getForEntity(requestUrl, LoggedInClient.class);
		// ASSERT
		assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
	}
	@Test
	public void testClientLoginWithMismatchingCredenitals_RespondsWith406() {
		String email = clientList.get(0).getEmail();
		String password = clientList.get(1).getPassword(); //Mismatched password
		String requestUrl = "/client?email="+email+"&password="+password;
		ResponseEntity<LoggedInClient> response = 
			restTemplate.getForEntity(requestUrl, LoggedInClient.class);
		// ASSERT
		assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
	}

}
