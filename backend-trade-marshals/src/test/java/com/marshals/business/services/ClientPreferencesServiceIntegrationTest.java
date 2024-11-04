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
import com.marshals.business.ClientPreferences;
import com.marshals.business.LoggedInClient;
import com.marshals.integration.DatabaseException;
 
@SpringBootTest
@Transactional
class ClientPreferencesServiceIntegrationTest {
 
	@Autowired
	private ClientPreferencesService service;
 
	@Autowired
	private ClientService clientService;
 
	@Autowired
	private JdbcTemplate testJdbcTemplate;
 
	// Few test client
	Client client1654658069 = new Client("sowmya@gmail.com", "1654658069", "Marsh2024", "Sowmya", "11/12/2002", "India",
			new ArrayList<>(List.of(new ClientIdentification("Aadhar", "123456789102"))), true); // Existing client
	Client client1654658000 = new Client("nonexistent.client@gmail.com", "1654658000", "Password1234",
			"Non Existent client", "12/10/1994", "USA",
			new ArrayList<>(List.of(new ClientIdentification("SSN", "1953826343"))), false); // Non existent client
	Client client1654658070 = new Client("sam@gmail.com", "1654658070", "Password1234", "Sam", "12/11/2000", "USA",
			new ArrayList<>(List.of(new ClientIdentification("SSN", "1643846323"))), false); // New client to insert
 
	// Few test client preferences
	ClientPreferences clientPref1654658069 = new ClientPreferences("1654658069", "Education", "HIG", "Short", "Tier4",
			2, false); // Existing client
	ClientPreferences clientPref1654658070 = new ClientPreferences("1654658070", "Retirement", "LIG", "Short", "Tier4",
			2, false); // New client to insert
	ClientPreferences clientPref541107416 = new ClientPreferences("541107416", "Major Expense", "LIG", "Long", "Tier3",
			1, false); // Existing client with updated preferences
	ClientPreferences clientPref1654658000 = new ClientPreferences("1654658000", "Retirement", "LIG", "Short", "Tier4",
			2, false); // Non existent client
 
	@AfterEach
	void tearDown() throws Exception {
		service = null;
		clientService = null;
	}
 
	/* CLIENT PREFERENCES */
 
	/* TESTS FOR GETTING CLIENT PREFERENCES */
	@Test
	void testForGetClientPreferencesFromService() {
		String id = "1654658069";
		ClientPreferences expectedClientPreference = service.getClientPreferences(id);
		assertTrue(expectedClientPreference.equals(clientPref1654658069));
	}
 
	// Failure for non existent client preferences
	@Test
	void testForGetClientPreferencesForNonExistentClientFromService() {
		String id = "1654658000";
		Exception e = assertThrows(DatabaseException.class, () -> {
			service.getClientPreferences(id);
		});
		assertEquals(e.getMessage(), "Please enter your client preferences");
	}
 
	/* TESTS FOR ADDING CLIENT PREFERENCES */
	// Successful insertion must increase row count
	@Test
	void testForAddingClientPreferencesFromService() {
		// Must add new Client before adding new Client Preferences
		List<ClientIdentification> identificationList = new ArrayList<>(
				List.of(new ClientIdentification("SSN", "1643846323")));
		//Registering new client before adding new preferences
		LoggedInClient newClient = clientService.registerNewClient("sam@gmail.com", "Password1234", "Sam", "12/11/2000", "USA",
				identificationList);
		//Defining new client preferences
		ClientPreferences newClientPref = new ClientPreferences(newClient.getClient().getClientId(), "Retirement", "LIG", "Short",
				"Tier4", 2, false);
		int oldCount = countRowsInTable(testJdbcTemplate, "client_preferences");
		service.addClientPreferences(newClientPref);
		int newCount = countRowsInTable(testJdbcTemplate, "client_preferences");
		assertTrue(newCount == oldCount + 1, "Client Preferences count must increase by one");
	}
 
	// Failure - Insertion of client preferences for non existent client
	@Test
	void testForAddClientPreferencesMethodWhenClientDoesntExistFromService() {
		Exception e = assertThrows(DatabaseException.class, () -> {
			service.addClientPreferences(clientPref1654658000);
		});
		assertEquals(e.getMessage(), "Client doesn't exist with this Client ID");
	}
 
	// Failure - Addition of client preferences to client with existing preferences
	// - Can only update
	@Test
	void testForAddClientPreferencesMethodForExistingClientPreferencesFromService() {
		Exception e = assertThrows(DatabaseException.class, () -> {
			service.addClientPreferences(clientPref1654658069);
		});
		assertEquals(e.getMessage(), "Client already exists with this Client ID");
	}
 
	/* TESTS FOR UPDATING CLIENT PREFERENCES */
	// Successful updation of client preferences
	@Test
	void testForUpdatingClientPreferencesForValidClientFromService() {
		String existingClientId = "541107416";
		String whereCondition = "length_of_investment = 'Long' and percentage_of_spend = 'Tier3' and is_advisor_accepted = 'false' and client_id = "
				+ existingClientId;
		service.updateClientPreferences(clientPref541107416);
		int newCount = countRowsInTableWhere(testJdbcTemplate, "client_preferences", whereCondition);
		assertTrue(newCount == 1, "Updated preferences must exist");
	}
 
	// Failure - Updation of preferences for non existent client
	@Test
	void testForUpdatingClientPreferencesForNonExistentClientFromService() {
		Exception e = assertThrows(DatabaseException.class, () -> {
			service.updateClientPreferences(clientPref1654658000);
		});
		assertEquals(e.getMessage(), "Client doesn't exist with this Client ID");
	}
 
}