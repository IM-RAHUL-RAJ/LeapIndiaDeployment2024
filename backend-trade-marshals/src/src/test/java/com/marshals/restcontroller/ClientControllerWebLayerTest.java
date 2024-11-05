package com.marshals.restcontroller;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marshals.business.Client;
import com.marshals.business.ClientIdentification;
import com.marshals.business.ClientPreferences;
import com.marshals.business.LoggedInClient;
import com.marshals.business.services.ClientService;
import com.marshals.integration.DatabaseException;
import com.marshals.integration.FMTSException;

@WebMvcTest(controllers = { ClientController.class })
class ClientControllerWebLayerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ClientService mockService;

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
	void testForMockMvcInstantiationForClientController() {
		assertNotNull(mockMvc);
	}

	// Email Validation tests
	@Test
	void testForSuccessfulEmailVerification_RespondsWith200() throws Exception {
		String email = clientList.get(0).getEmail(); // Existing email
		when(mockService.verifyClientEmail(email)).thenReturn(true); // Mocking service

		mockMvc.perform(get("/client/verify-email/" + email)).andExpect(status().isOk())
				.andExpect(jsonPath("$.isVerified").value(true));
	}

	@Test
	void testForNonExistingEmailVerification_RespondsWith200AndFalseResponse() throws Exception {
		String email = clientList.get(1).getEmail(); // Non Existing email
		when(mockService.verifyClientEmail(email)).thenReturn(false); // Mocking service

		mockMvc.perform(get("/client/verify-email/" + email)).andExpect(status().isOk())
				.andExpect(jsonPath("$.isVerified").value(false));
	}

	@Test
	void testForVerificationOfEmailWithInvalidFormat_RespondsWith406() throws Exception {
		String email = "invalid-email";
		when(mockService.verifyClientEmail(email)).thenThrow(new IllegalArgumentException()); // Mocking service
		mockMvc.perform(get("/client/verify-email/" + email)).andExpect(status().isNotAcceptable())
				.andExpect(content().string(is(emptyOrNullString())));
		;
	}

	@Test
	void testForUnexpectedErrorAtVerificationOfEmail_RespondsWith500() throws Exception {
		String email = "invalid-email";
		when(mockService.verifyClientEmail(email)).thenThrow(new RuntimeException()); // Mocking service
		mockMvc.perform(get("/client/verify-email/" + email)).andExpect(status().is5xxServerError())
				.andExpect(content().string(is(emptyOrNullString())));
		;
	}

	
	// Register New Client Tests
	@Test
	void testForSuccessfulRegistrationOfNewClient_RespondsWith200() throws Exception {
		Client client = clientList.get(2);
		LoggedInClient newClient = new LoggedInClient(client,1235345);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy"); //doB Format stored in client model
		String dateOfBirth = dateFormat.format(client.getDateOfBirth());
		// Mocking service to return a LoggedInClient
		when(mockService.registerNewClient(client.getEmail(), client.getPassword(), client.getName(), 
				dateOfBirth, client.getCountry(), client.getIdentification())).thenReturn(newClient); 
		
		// Creating the ObjectMapper object
		ObjectMapper mapper = new ObjectMapper();		
		String jsonString = mapper.writeValueAsString(client); // Converting the Object to JSONString
//		System.out.println("JSON: "+jsonString);
		
		mockMvc.perform(post("/client/register")
							.contentType(MediaType.APPLICATION_JSON)
							.content(jsonString))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.client.email").value(client.getEmail()))
				.andExpect(jsonPath("$.client.clientId").value(newClient.getClient().getClientId()))
				.andExpect(jsonPath("$.client.name").value(client.getName()))
				.andExpect(jsonPath("$.client.password").value(client.getPassword()))
				.andExpect(jsonPath("$.client.country").value(client.getCountry()));
	}
	
	@Test
	void testForRegistrationWithNullClientDetails_RespondsWith406() throws Exception {
		Client client = clientList.get(2);
		client.setDateOfBirth(null); //Null Client details
		
		// Creating the ObjectMapper object
		ObjectMapper mapper = new ObjectMapper();		
		String jsonString = mapper.writeValueAsString(client); // Converting the Object to JSONString
		
		mockMvc.perform(post("/client/register")
							.contentType(MediaType.APPLICATION_JSON)
							.content(jsonString))
				.andExpect(status().isNotAcceptable())
				.andExpect(content().string(is(emptyOrNullString())));
	}
	
	@Test
	void testForRegistrationWithNullClientEmail_RespondsWith406() throws Exception {
		Client client = clientList.get(2);
		client.setEmail(null); //Null Client email
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy"); //doB Format stored in client model
		String dateOfBirth = dateFormat.format(client.getDateOfBirth());
		
		// Mocking service to throw a NullPointerException
		when(mockService.registerNewClient(client.getEmail(), client.getPassword(), client.getName(), 
				dateOfBirth, client.getCountry(), client.getIdentification())).thenThrow(new NullPointerException()); 
		
		// Creating the ObjectMapper object
		ObjectMapper mapper = new ObjectMapper();		
		String jsonString = mapper.writeValueAsString(client); // Converting the Object to JSONString
		
		mockMvc.perform(post("/client/register")
							.contentType(MediaType.APPLICATION_JSON)
							.content(jsonString))
				.andExpect(status().isNotAcceptable())
				.andExpect(content().string(is(emptyOrNullString())));
	}
	
	@Test
	void testForRegistrationWithExistingClientDetails_RespondsWith406() throws Exception {
		Client client = clientList.get(2);
		client.setIdentification(clientList.get(0).getIdentification()); //Existing Client ID Details
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy"); //doB Format stored in client model
		String dateOfBirth = dateFormat.format(client.getDateOfBirth());
		
		// Mocking service to throw a IllegalArgumentException
		when(mockService.registerNewClient(client.getEmail(), client.getPassword(), client.getName(), 
				dateOfBirth, client.getCountry(), client.getIdentification())).thenThrow(new IllegalArgumentException()); 
		
		// Creating the ObjectMapper object
		ObjectMapper mapper = new ObjectMapper();		
		String jsonString = mapper.writeValueAsString(client); // Converting the Object to JSONString
		
		mockMvc.perform(post("/client/register")
							.contentType(MediaType.APPLICATION_JSON)
							.content(jsonString))
				.andExpect(status().isNotAcceptable())
				.andExpect(content().string(is(emptyOrNullString())));
	}
	
	@Test
	void testWhenServiceThrowsDatabaseExceptionAtRegistration_RespondsWith400() throws Exception {
		Client client = clientList.get(0);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy"); //doB Format stored in client model
		String dateOfBirth = dateFormat.format(client.getDateOfBirth());
		
		// Mocking service to throw a DatabaseException
		when(mockService.registerNewClient(client.getEmail(), client.getPassword(), client.getName(), 
				dateOfBirth, client.getCountry(), client.getIdentification())).thenThrow(new DatabaseException()); 
		
		// Creating the ObjectMapper object
		ObjectMapper mapper = new ObjectMapper();		
		String jsonString = mapper.writeValueAsString(client); // Converting the Object to JSONString
		
		mockMvc.perform(post("/client/register")
							.contentType(MediaType.APPLICATION_JSON)
							.content(jsonString))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(is(emptyOrNullString())));
	}
	
	@Test
	void testWhenServiceThrowsFMTSExceptionAtRegistration_RespondsWith400() throws Exception {
		Client client = clientList.get(0);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy"); //doB Format stored in client model
		String dateOfBirth = dateFormat.format(client.getDateOfBirth());
		
		// Mocking service to throw a FMTSException
		when(mockService.registerNewClient(client.getEmail(), client.getPassword(), client.getName(), 
				dateOfBirth, client.getCountry(), client.getIdentification())).thenThrow(new FMTSException()); 
		
		// Creating the ObjectMapper object
		ObjectMapper mapper = new ObjectMapper();		
		String jsonString = mapper.writeValueAsString(client); // Converting the Object to JSONString
		
		mockMvc.perform(post("/client/register")
							.contentType(MediaType.APPLICATION_JSON)
							.content(jsonString))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(is(emptyOrNullString())));
	}
	
	@Test
	void testForUnexpectedErrorAtRegistration_RespondsWith500() throws Exception {
		Client client = clientList.get(2);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy"); //doB Format stored in client model
		String dateOfBirth = dateFormat.format(client.getDateOfBirth());
		
		// Mocking service to throw a RuntimeException
		when(mockService.registerNewClient(client.getEmail(), client.getPassword(), client.getName(), 
				dateOfBirth, client.getCountry(), client.getIdentification())).thenThrow(new RuntimeException()); 
		
		// Creating the ObjectMapper object
		ObjectMapper mapper = new ObjectMapper();		
		String jsonString = mapper.writeValueAsString(client); // Converting the Object to JSONString
		
		mockMvc.perform(post("/client/register")
							.contentType(MediaType.APPLICATION_JSON)
							.content(jsonString))
				.andExpect(status().is5xxServerError())
				.andExpect(content().string(is(emptyOrNullString())));
	}

	// Login Existing Client Tests
	@Test
	void testForSuccessfulClientLogin_RespondsWith200() throws Exception {
		Client client = clientList.get(0);
		LoggedInClient existingClient = new LoggedInClient(client,1235345);
		when(mockService.loginExistingClient(client.getEmail(), client.getPassword())).thenReturn(existingClient); // Mocking service

		mockMvc.perform(get("/client?email={email}&password={password}",client.getEmail(),client.getPassword()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.client.email").value(client.getEmail()))
			.andExpect(jsonPath("$.client.clientId").value(existingClient.getClient().getClientId()))
			.andExpect(jsonPath("$.client.name").value(client.getName()))
			.andExpect(jsonPath("$.client.password").value(client.getPassword()))
			.andExpect(jsonPath("$.client.country").value(client.getCountry()));
	}
	@Test
	void testForClientLoginWithNonExistingClient_RespondsWith406() throws Exception {
		String email = clientList.get(1).getEmail();
		String password = clientList.get(1).getPassword();
		// Mocking service to throw IllegalArgumentException
		when(mockService.loginExistingClient(email, password)).thenThrow(new IllegalArgumentException("Client is not registered")); 

		mockMvc.perform(get("/client?email={email}&password={password}",email,password))
			.andExpect(status().isNotAcceptable())
			.andExpect(content().string(is(emptyOrNullString())));
	}
	
	@Test
	void testForClientLoginWithMismatchingCredentials_RespondsWith406() throws Exception {
		String email = clientList.get(0).getEmail();
		String password = clientList.get(1).getPassword(); //Mismatched Password
		// Mocking service to throw IllegalArgumentException
		when(mockService.loginExistingClient(email, password)).thenThrow(new IllegalArgumentException("Password does not match")); 

		mockMvc.perform(get("/client?email={email}&password={password}",email,password))
			.andExpect(status().isNotAcceptable())
			.andExpect(content().string(is(emptyOrNullString())));
	}
	
	@Test
	void testWhenServiceThrowsDatabaseExceptionAtLogin_RespondsWith400() throws Exception {
		String email = clientList.get(2).getEmail();
		String password = clientList.get(2).getPassword(); 
		// Mocking service to throw DatabaseException
		when(mockService.loginExistingClient(email, password)).thenThrow(new DatabaseException("Client doesnt exist")); 
		
		mockMvc.perform(get("/client?email={email}&password={password}",email,password))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(is(emptyOrNullString())));
	}
	
	@Test
	void testWhenServiceThrowsFMTSExceptionAtLogin_RespondsWith400() throws Exception {
		String email = clientList.get(1).getEmail();
		String password = clientList.get(1).getPassword(); 
		// Mocking service to throw FMTSException
		when(mockService.loginExistingClient(email, password)).thenThrow(new FMTSException("Couldnt validate client")); 
		
		mockMvc.perform(get("/client?email={email}&password={password}",email,password))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(is(emptyOrNullString())));
	}
	
	@Test
	void testForUnexpectedErrorAtLogin_RespondsWith500() throws Exception {
		String email = clientList.get(0).getEmail();
		String password = clientList.get(0).getPassword(); 
		// Mocking service to throw RuntimeException
		when(mockService.loginExistingClient(email, password)).thenThrow(new RuntimeException()); 
		
		mockMvc.perform(get("/client?email={email}&password={password}",email,password))
			.andExpect(status().is5xxServerError())
			.andExpect(content().string(is(emptyOrNullString())));
	}
}
