package com.marshals.restcontroller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.marshals.business.Client;
import com.marshals.business.ClientIdentification;
import com.marshals.business.ClientPreferences;
import com.marshals.business.LoggedInClient;
import com.marshals.integration.DatabaseException;

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"},
     executionPhase = ExecutionPhase.AFTER_TEST_METHOD) 
class ClientPreferencesControllerE2ETest {
	@Autowired
	private TestRestTemplate restTemplate; // for executing rest endpoints

	@Autowired
	private JdbcTemplate jdbcTemplate;  // for executing SQL queries
	
	//Mock Client preferences details
	List<ClientPreferences> clientPreferencesList = new ArrayList<ClientPreferences>(
			List.of(
					new ClientPreferences("1654658069",  "Education", "HIG", "Short", "Tier4", 2, false), //Existing client
					new ClientPreferences("1654658000", "Major Expense", "MIG", "Medium", "Tier1", 5, true), //Non existent client
					new ClientPreferences("767836496", "Retirement", "MIG", "Long", "Tier3", 3, true) //New client to be inserted
			)
		);
	
//	TESTS FOR GET CLIENT PREFERENCES BY ID
	
	//Get client preferences for valid id
	@Test
	void testForGetClientPreferencesValidClientId() {
		String id = "1654658069";
		String requestUrl = "/client-preferences/"+id;
		
		ResponseEntity<ClientPreferences> response = 
				restTemplate.getForEntity(requestUrl, ClientPreferences.class);
		
		// verify the response HTTP status is OK
		assertEquals(HttpStatus.OK, response.getStatusCode());
		
		ClientPreferences expected = response.getBody();
		assertTrue(expected.equals(clientPreferencesList.get(0)));
	}
	
	//Get client preferences for invalid id
	@Test
	void testForGetClientPreferenecsInvalidClientId() {
		String id = "invalid-id";
		String requestUrl = "/client-preferences/"+id;
		
		ResponseEntity<ClientPreferences> response = 
				restTemplate.getForEntity(requestUrl, ClientPreferences.class);
		
		assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
	}
	
	//Get client preferences for id that doesn't exist
	@Test
	void testForGetClientPreferenecsClientIdDoesntExist() {
		String id = "1654658111";
		String requestUrl = "/client-preferences/"+id;
		
		ResponseEntity<ClientPreferences> response = 
				restTemplate.getForEntity(requestUrl, ClientPreferences.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
//	TESTS FOR ADD CLIENT PREFERENCES
	
	//Add client preferences for valid details
	@Test
	void testForAddClientPreferencesForValidClientPreferences() {
		
		Client mockNewClient = new Client("sam@gmail.com", "" , "Password1234", "Sam", "12/11/2000", "USA",
				new ArrayList<>(List.of(new ClientIdentification("SSN", "1643846323"))), false);
		
		String requestUrlForClient = "/client/register";
		ResponseEntity<LoggedInClient> responseClient = 
				restTemplate.postForEntity(requestUrlForClient, mockNewClient, LoggedInClient.class);
		
		String requestUrl = "/client-preferences";
		ClientPreferences newClientPreferences = clientPreferencesList.get(2);
		
		
		ResponseEntity<ClientPreferences> response = 
				restTemplate.postForEntity(requestUrl, newClientPreferences , ClientPreferences.class);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		ClientPreferences responseDTO = response.getBody();
		assertTrue(responseDTO.equals(newClientPreferences));
	}
	
	//Add client preferences for invalid details
	@Test
	void testForAddClientPreferencesForInvalidClientPreferences() {
		String requestUrl = "/client-preferences";
		ClientPreferences invalidClientPreferences = new ClientPreferences("1231231234", "Retirement", "MIG", "Long", "Tier3", 3, true);
		
		ResponseEntity<ClientPreferences> response = restTemplate.postForEntity(requestUrl, invalidClientPreferences , ClientPreferences.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	//Add client preferences for null object
	@Test
	void testForAddClientPreferencesForNullClientPreferences() {
		String requestUrl = "/client-preferences";
		ClientPreferences nullClientPreferences = null;
		ResponseEntity<ClientPreferences> response = 
				restTemplate.postForEntity(requestUrl, nullClientPreferences , ClientPreferences.class);
		
		assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
	}
	
//	TESTS FOR UPDATE CLIENT PREFERENCES
	
	// Update for valid preferences details
	@Test
	void testForUpdateClientPreferencesForValidClientPreferences() throws Exception{
		String requestUrl = "/client-preferences";
		ClientPreferences newClientPreferences = new ClientPreferences("1654658069",  "Retirement", "HIG", "Short", "Tier4", 2, false);
		
		RequestEntity<ClientPreferences> requestEntity = 
				RequestEntity.put(new URI(requestUrl))
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON)
							.body(newClientPreferences);
							
		ResponseEntity<ClientPreferences> response = 
				restTemplate.exchange(requestEntity, ClientPreferences.class);
		
		assertEquals(HttpStatus.OK, response.getStatusCode());
		ClientPreferences responseDTO = response.getBody();
		assertTrue(responseDTO.equals(newClientPreferences));
	}
	
	// Update for invalid preferences details
	@Test
	void testForUpdateClientPreferencesForInvalidClientPreferences() throws Exception {
		String requestUrl = "/client-preferences";
		ClientPreferences invalidClientPreferences = new ClientPreferences("1231231234", "Retirement", "MIG", "Long", "Tier3", 3, true);
		RequestEntity<ClientPreferences> requestEntity = 
				RequestEntity.put(new URI(requestUrl))
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON)
							.body(invalidClientPreferences);
		ResponseEntity<ClientPreferences> response = 
				restTemplate.exchange(requestEntity, ClientPreferences.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	// Update for null preferences details
	@Test
	void testForUpdateClientPreferencesForNullClientPreferences() throws Exception{
		String requestUrl = "/client-preferences";
		ClientPreferences nullClientPreferences = null;

		RequestEntity<ClientPreferences> requestEntity = 
				RequestEntity.put(new URI(requestUrl))
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON)
							.body(nullClientPreferences);
							
		ResponseEntity<String> response = 
				restTemplate.exchange(requestEntity, String.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
}
