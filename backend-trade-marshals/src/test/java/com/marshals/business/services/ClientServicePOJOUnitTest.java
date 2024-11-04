package com.marshals.business.services;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.marshals.business.Client;
import com.marshals.business.ClientIdentification;
import com.marshals.business.ClientPortfolio;
import com.marshals.business.FMTSValidatedClient;
import com.marshals.business.LoggedInClient;
import com.marshals.integration.ClientDao;
import com.marshals.integration.DatabaseException;
import com.marshals.integration.FMTSException;

class ClientServicePOJOUnitTest {
	@Mock ClientDao mockDao;
	@Mock FMTSService mockFMTSService;
	
	@Autowired
	@InjectMocks
	private ClientService service;
	
	private List<Client> clientList;

	@BeforeEach
	void setUp() throws Exception {
		
		//Test Client details
		clientList = new ArrayList<Client>(List.of(
				 new Client("sowmya@gmail.com","1654658069", "Marsh2024", "Sowmya", "11/12/2002", "India", 
							new ArrayList<>(List.of(new ClientIdentification("Aadhar","123456789102"))), true), //Existing client
				 new Client("nonexistent.client@gmail.com","1654658000", "Password1234", "Non Existent client", "12/10/1994", "USA", 
							new ArrayList<>(List.of(new ClientIdentification("SSN","1953826343"))), false), //Non Existent client
				 new Client("sam@gmail.com","767836496", "Password1234", "Sam", "12/11/2000", "USA", 
							new ArrayList<>(List.of(new ClientIdentification("SSN","1643846323"))), false) //New client to be inserted
				));
		
		//Initializing the Client Service with a Mock Dao and mock fmts service
		MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() throws Exception {
		service = null;
		clientList = null;
	}

	@Test
	void shouldCreateObject() {
		assertNotNull(service);
	}
	
	/*Testing related to Client - Email Validation, Login and Register*/
	
	//Email Validation
	@Test
	void shouldHandleNullEmailValidation() {
		Exception e = assertThrows(NullPointerException.class, () -> {
			 service.verifyClientEmail(null);
		});
		assertEquals("Client Email cannot be null",e.getMessage());
	}
	
	@Test
	void testNonExistentEmailValidation() {
		String nonExistentEmail = clientList.get(1).getEmail();
		String errorMsg = "Client with given email doesnt exist";
		//Mock the behavior of Dao method to throw exception
		Mockito.doThrow(new DatabaseException(errorMsg)).when(mockDao).verifyClientEmail(nonExistentEmail);
		Boolean isClientExists = service.verifyClientEmail(nonExistentEmail);
		Mockito.verify(mockDao).verifyClientEmail(nonExistentEmail); //Verifying that the corresponding mockDao method was called
		assertFalse(isClientExists,"Should return false for non existent client email");
	}
	
	@Test
	void testExistingEmailValidation() {
		String validEmail = clientList.get(0).getEmail();
		Mockito.when(mockDao.verifyClientEmail(validEmail)).thenReturn(true); // Mock behavior
		Boolean isClientExists = service.verifyClientEmail(validEmail);
		Mockito.verify(mockDao).verifyClientEmail(validEmail); //Verifying that the corresponding mockDao method was called
		assertTrue(isClientExists,"Should return true for existent client email");
	}
	
	@Test
	void testForInvalidEmailValidationThrowsException() {
		String invalidEmail = "sample_invalid";
		String errorMsg = "Client Email Format is invalid";
		//Mock the behavior of Dao method to throw exception
		Mockito.doThrow(new IllegalArgumentException(errorMsg)).when(mockDao).verifyClientEmail(invalidEmail);
		Exception e = assertThrows(IllegalArgumentException.class, () -> {
			service.verifyClientEmail(invalidEmail);
			Mockito.verify(mockDao).verifyClientEmail(invalidEmail); //Verifying that the corresponding mockDao method was called
		});
		assertEquals(errorMsg,e.getMessage());
	}
	
	
	//Registering a new client
	@Test
	void shouldHandleRegisterationOfClientWithNullEmail() {
		Exception e = assertThrows(NullPointerException.class, () -> {
			service.registerNewClient(null,"Password12", "John Doe", "08/11/2003", 
								"India", new ArrayList<>(List.of(new ClientIdentification("Aadhar","123467541244"))));
		});
		assertEquals("Client Email cannot be null",e.getMessage());
	}
	
	@Test
	void shouldHandleRegisterationOfExistingClient() {
		String existingEmail = clientList.get(0).getEmail();
		Mockito.when(mockDao.verifyClientEmail(existingEmail)).thenReturn(true); // Mock behavior
		Exception e = assertThrows(IllegalArgumentException.class, () -> {
			service.registerNewClient(existingEmail, clientList.get(0).getPassword(), clientList.get(0).getName(),
					"12/11/2000", clientList.get(0).getCountry(), clientList.get(0).getIdentification());
			Mockito.verify(mockDao).verifyClientEmail(existingEmail); //Verifying that the corresponding mockDao method was called
		});
		assertEquals("Client with given email is already registered",e.getMessage());
	}
	
	@Test
	void shouldHandleRegisterationOfClientWithNullDetails() {
		String nonExistentEmail = clientList.get(2).getEmail();
		//Mock the behavior of Dao method to throw exception - For email validation to return false
		Mockito.doThrow(new DatabaseException()).when(mockDao).verifyClientEmail(nonExistentEmail);
		Exception e = assertThrows(NullPointerException.class, () -> {
			service.registerNewClient(nonExistentEmail,"Password12", null, "08/11/2003", 
								"India", new ArrayList<>(List.of(new ClientIdentification("Aadhar","123467541244"))));
		});
		assertEquals("Client Details cannot be null",e.getMessage());
	}
	
	@Test
	void shouldHandleRegisterationOfClientWithIDDetailsOfExistingClient() {
		//Mock the behavior of Dao method to throw exception - For email validation to return false
		Mockito.doThrow(new DatabaseException()).when(mockDao).verifyClientEmail(clientList.get(2).getEmail());
		// Mock behavior of Dao method that retrieves all client ID Details
		Mockito.when(mockDao.getAllClientIdentificationDetails()).thenReturn(clientList.get(0).getIdentification());
		Exception e = assertThrows(IllegalArgumentException.class, () -> {
			service.registerNewClient(clientList.get(2).getEmail(), clientList.get(2).getPassword(), clientList.get(2).getName(),
					"12/11/2000", clientList.get(2).getCountry(), clientList.get(0).getIdentification());
			//Verifying that the corresponding mockDao method was called
			Mockito.verify(mockDao).verifyClientEmail(clientList.get(2).getEmail()); 
			Mockito.verify(mockDao).getAllClientIdentificationDetails(); 
		});
		assertEquals("Client with given Identification Details is already registered with another email",e.getMessage());
	}
	
	@Test
	void shouldHandleFMTSInvalidationAtRegistration() {
		String newEmail = clientList.get(2).getEmail();
		//Mock the behavior of Dao method to throw exception - For email validation to return false
		Mockito.doThrow(new DatabaseException()).when(mockDao).verifyClientEmail(newEmail);
		// Mock behavior of Dao method that retrieves all client ID Details
		Mockito.when(mockDao.getAllClientIdentificationDetails()).thenReturn(clientList.get(0).getIdentification());
		//Mocking fmtsService to throw FMTSException - Invalidate client
		Mockito.doThrow(new FMTSException()).when(mockFMTSService).verifyClient(newEmail);
		
		assertThrows(FMTSException.class, () -> {
			service.registerNewClient(newEmail, clientList.get(2).getPassword(), clientList.get(2).getName(),
					"12/11/2000", clientList.get(2).getCountry(), clientList.get(2).getIdentification());
			//Verifying that the corresponding mockDao method was called
			Mockito.verify(mockDao).verifyClientEmail(newEmail); 
			Mockito.verify(mockDao).getAllClientIdentificationDetails(); 
			//Verifying that mock fmts serivce was called
			Mockito.verify(mockFMTSService).verifyClient(newEmail);
		});
		
	}
	
	@Test
	void testSuccesfulRegistrationOfClientWithValidDetails() {
		String newEmail = clientList.get(2).getEmail();
		//Mock the behavior of Dao method to throw exception - For email validation to return false
		Mockito.doThrow(new DatabaseException()).when(mockDao).verifyClientEmail(newEmail);
		// Mock behavior of Dao method that retrieves all client ID Details
		Mockito.when(mockDao.getAllClientIdentificationDetails()).thenReturn(clientList.get(0).getIdentification());
		//Mocking fmtsService to also successfully validate the client
		Mockito.when(mockFMTSService.verifyClient(newEmail)).thenReturn(
				new FMTSValidatedClient(clientList.get(2).getClientId(),newEmail,767836496));
		//New Client Details
		LoggedInClient newClient = service.registerNewClient(newEmail, clientList.get(2).getPassword(), clientList.get(2).getName(),
				"12/11/2000", clientList.get(2).getCountry(), clientList.get(2).getIdentification());
		//Verifying that the corresponding mockDao method was called
		Mockito.verify(mockDao).verifyClientEmail(newEmail); 
		Mockito.verify(mockDao).getAllClientIdentificationDetails(); 
		//Verifying that mock fmts serivce was called
		Mockito.verify(mockFMTSService).verifyClient(newEmail);
		Mockito.verify(mockDao).addNewClient(newClient.getClient(), new ClientPortfolio(newClient.getClient().getClientId(),new BigDecimal("10000").setScale(4),new ArrayList<>())); 
		assertEquals(newClient.getClient(),clientList.get(2),"Should successfully register new client");
	}
	
	//Logging in an existing client
	@Test
	void shouldHandleLoginOfClientWithNullEmail() {
		Exception e = assertThrows(NullPointerException.class, () -> {
			service.loginExistingClient(null,"Password12");
		});
		assertEquals("Client Email cannot be null",e.getMessage());
	}
	
	@Test
	void shouldHandleLoginOfNonExistentClient() {
		String nonExistentEmail = clientList.get(1).getEmail();
		//Mock the behavior of Dao method to throw exception
		Mockito.doThrow(new DatabaseException()).when(mockDao).verifyClientEmail(nonExistentEmail);
		Exception e = assertThrows(IllegalArgumentException.class, () -> {
			service.loginExistingClient(nonExistentEmail,clientList.get(1).getPassword());
			Mockito.verify(mockDao).verifyClientEmail(nonExistentEmail); //Verifying that the corresponding mockDao method was called
		});
		assertEquals("Client with given email is not registered",e.getMessage());
	}
	
	@Test
	void shouldHandleLoginOfClientWithNullPassword() {
		String existingEmail = clientList.get(0).getEmail();
		//Mock the behavior of Dao method to return true - Successful email validation
		Mockito.when(mockDao.verifyClientEmail(existingEmail)).thenReturn(true); 
		Exception e = assertThrows(NullPointerException.class, () -> {
			service.loginExistingClient(existingEmail,null);
		});
		assertEquals("Client Password cannot be null",e.getMessage());
	}
	
	@Test
	void shouldHandleLoginOfExistingClientWithInvalidCredentials() {
		String existingEmail = clientList.get(0).getEmail();
		String invalidPassword = "Password123";
		String errorMsg = "Password does not match logging in Client's credentials";
		//Mock the behavior of Dao method to return true - Successful email validation
		Mockito.when(mockDao.verifyClientEmail(existingEmail)).thenReturn(true); 
		//Mock the behavior of Dao method to throw exception
		Mockito.doThrow(new IllegalArgumentException(errorMsg)).when(mockDao).getClientAtLogin(existingEmail,invalidPassword);
		Exception e = assertThrows(IllegalArgumentException.class, () -> {
			service.loginExistingClient(existingEmail,invalidPassword);
			//Verifying that the corresponding mockDao methods were called
			Mockito.verify(mockDao).verifyClientEmail(existingEmail); 
			Mockito.verify(mockDao).getClientAtLogin(existingEmail,invalidPassword); 
		});
		assertEquals(errorMsg,e.getMessage());
	}
	
	@Test
	void shouldHandleFMTSInvalidationAtLogin() {
		String existingEmail = clientList.get(0).getEmail();
		String validPassword = clientList.get(0).getPassword();
		//Mock the behavior of Dao method to return true - Successful email validation
		Mockito.when(mockDao.verifyClientEmail(existingEmail)).thenReturn(true);
		Mockito.when(mockDao.getClientAtLogin(existingEmail,validPassword)).thenReturn(clientList.get(0));
		//Mocking fmtsService to throw FMTSException - Invalidate client
		Mockito.doThrow(new FMTSException()).when(mockFMTSService).verifyClient(existingEmail, clientList.get(0).getClientId());
		
		assertThrows(FMTSException.class, () -> {
			service.loginExistingClient(existingEmail,validPassword);
			//Verifying that the corresponding mockDao methods were called
			Mockito.verify(mockDao).verifyClientEmail(existingEmail); 
			Mockito.verify(mockDao).getClientAtLogin(existingEmail,validPassword); 
			//Verifying that the corresponding mockDao method was called
			Mockito.verify(mockDao).verifyClientEmail(existingEmail); 
		});
	}
	
	@Test
	void testSuccesfulLoginOfExistingValidClient() {
		String existingEmail = clientList.get(0).getEmail();
		String validPassword = clientList.get(0).getPassword();
		//Mock the behavior of Dao method to return true - Successful email validation
		Mockito.when(mockDao.verifyClientEmail(existingEmail)).thenReturn(true);
		Mockito.when(mockDao.getClientAtLogin(existingEmail,validPassword)).thenReturn(clientList.get(0));
		//Mocking fmtsService to also successfully validate the client
		Mockito.when(mockFMTSService.verifyClient(existingEmail, clientList.get(0).getClientId())).
				thenReturn(new FMTSValidatedClient(clientList.get(0).getClientId(), existingEmail, 1654658069));
		
		LoggedInClient existingClient = service.loginExistingClient(existingEmail,validPassword);
		//Verifying that the corresponding mockDao methods were called
		Mockito.verify(mockDao).verifyClientEmail(existingEmail); 
		Mockito.verify(mockDao).getClientAtLogin(existingEmail,validPassword); 
		//Verifying that the corresponding mockDao method was called
		Mockito.verify(mockDao).verifyClientEmail(existingEmail); 
		assertEquals(existingClient.getClient(),clientList.get(0),"Should successfully login existing client");	
	}

}
