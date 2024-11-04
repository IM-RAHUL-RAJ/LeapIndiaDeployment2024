package com.marshals.business.services;

import static org.junit.jupiter.api.Assertions.*;

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
import com.marshals.business.ClientPreferences;
import com.marshals.integration.ClientPreferencesDao;
import com.marshals.integration.DatabaseException;

class ClientPreferencesServicePOJOUnitTest {

	@Mock ClientPreferencesDao mockDao; 
	
	@Autowired
	@InjectMocks
	private ClientPreferencesService service;
	
	private List<Client> clientList;
	private List<ClientPreferences> clientPreferencesList;
	
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
		//Test Client preferences details
		clientPreferencesList = new ArrayList<ClientPreferences>(
				List.of(
						new ClientPreferences("1654658069",  "Education", "HIG", "Short", "Tier4", 2, false), //Existing client
						new ClientPreferences("1654658000", "Major Expense", "MIG", "Medium", "Tier1", 5, true), //Non existent client
						new ClientPreferences("767836496", "Retirement", "MIG", "Long", "Tier3", 3, true) //New client to be inserted
				)
			);
		
		//Initializing the Client Service with a Mock Dao and mock fmts service
		MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() throws Exception {
		service = null;
		clientList = null;
		clientPreferencesList = null;
	}
	
	@Test
	void shouldCreateClientPreferenceService() {
		assertNotNull(service);
	}

	/*Testing related to Adding and Updating of Client Preferences*/
	
	//Adding Client Preferences
	@Test
	void shouldAddClientPreference() {
		ClientPreferences newClientPref = clientPreferencesList.get(2);
		service.addClientPreferences(newClientPref);
		//Verifying that the corresponding mockDao methods were called
		Mockito.verify(mockDao).addClientPreferences(newClientPref); 
	}
	@Test
	void shouldHandleDatabaseExceptionWhileAddingAlreadyExistingClient() {
		ClientPreferences clientPreference = clientPreferencesList.get(0);
		Mockito.doThrow(new DatabaseException()).when(mockDao).addClientPreferences(clientPreference);
		assertThrows(DatabaseException.class, () -> {
			service.addClientPreferences(clientPreference);
			//Verifying that the corresponding mockDao methods were called
			Mockito.verify(mockDao).addClientPreferences(clientPreference);
		});
	}
	@Test
	void shouldHandleDatabaseExceptionWhileGettingNonExistentClientPreference() {
		String clientPreferenceId = clientPreferencesList.get(2).getClientId();
		Mockito.doThrow(new DatabaseException()).when(mockDao).getClientPreferences(clientPreferenceId);
		assertThrows(DatabaseException.class, () -> {
			service.getClientPreferences(clientPreferenceId);
		});
	}
	
	@Test
	void shouldHandleNullObjectForAddClientPreference() {
		assertThrows(NullPointerException.class, () -> {
			service.addClientPreferences(null);
		});
	}
	
	//Getting Client Preferences
	@Test
	void shouldGetClientPreference() {
		String existingClientid = clientPreferencesList.get(0).getClientId();
		ClientPreferences expected = clientPreferencesList.get(0);
		Mockito.when(mockDao.getClientPreferences(existingClientid))
			.thenReturn(expected);
		ClientPreferences clientPreference =  service.getClientPreferences(existingClientid);
		//Verifying that the corresponding mockDao methods were called
		Mockito.verify(mockDao).getClientPreferences(existingClientid); 
		assertEquals(clientPreference.equals(expected), true);
	}
	
	@Test
	void shouldHandleNullObjectForGetClientPreference() {
		assertThrows(NullPointerException.class, () -> {
			service.getClientPreferences(null);
		});
	}
	
	//Updating Client Preferences
	@Test
	void shouldUpdateClientPreference() {
		ClientPreferences clientPreference = clientPreferencesList.get(0);
		clientPreference.setIncomeCategory("HIG"); //Updating existing client preference object
		service.updateClientPreferences(clientPreference);
		Mockito.verify(mockDao).updateClientPreferences(clientPreference);
		assertNotEquals(clientPreference.getIncomeCategory(), "MIG"); //Checking if the details have been updated
	}
	
	@Test
	void shouldNotUpdateForNullObject() {
		assertThrows(NullPointerException.class, () -> {
			service.updateClientPreferences(null);
		});
	}
	
	@Test
	void testUpdationOfNonExistentClientPreferencesThrowsDatabaseException() {
		Mockito.doThrow(new DatabaseException()).when(mockDao).updateClientPreferences(clientPreferencesList.get(1));
		assertThrows(DatabaseException.class, () -> 
			service.updateClientPreferences(clientPreferencesList.get(1))
		);
	}

}
