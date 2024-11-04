package com.marshals.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTable;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTableWhere;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.marshals.business.Client;
import com.marshals.business.ClientIdentification;
import com.marshals.business.ClientPortfolio;
import com.marshals.business.ClientPreferences;

@SpringBootTest
@Transactional
class ClientPreferencesDaoImplTest {

	@Autowired
	private ClientPreferencesDaoImpl dao;

	@Autowired
	private ClientDaoImpl clientDao;

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

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testForClientPreferencesDAOToHaveBeenIntialized() {
		assertNotNull(dao);
	}

	/* CLIENT PREFERENCES */

	/* TESTS FOR GETTING CLIENT PREFERENCES */
	// Successful retrieval
	@Test
	void testForGetClientPreferences() {
		String id = "1654658069";
		ClientPreferences expectedClientPreference = dao.getClientPreferences(id);
		assertTrue(expectedClientPreference.equals(clientPref1654658069));
	}

	// Failure for non existent client preferences
	@Test
	void testForGetClientPreferencesForNonExistentClient() {
		String id = "1654658000";
		Exception e = assertThrows(DatabaseException.class, () -> {
			dao.getClientPreferences(id);
		});
		assertEquals(e.getMessage(), "Please enter your client preferences");
	}

	/* TESTS FOR ADDING CLIENT PREFERENCES */
	// Successful insertion must increase row count
	@Test
	void testForAddingClientPreferences() {
		// Must add new Client before adding new Client Preferences
		clientDao.addNewClient(client1654658070,
				new ClientPortfolio(client1654658070.getClientId(), new BigDecimal("10000"), new ArrayList<>()));
		int oldCount = countRowsInTable(testJdbcTemplate, "client_preferences");
		dao.addClientPreferences(clientPref1654658070);
		int newCount = countRowsInTable(testJdbcTemplate, "client_preferences");
		assertTrue(newCount == oldCount + 1, "Client Preferences count must increase by one");
	}

	// Successful insertion must add correct data
	@Test
	void testForCheckingCorrectAddOfClientPreferences() {
		String newClientId = "1654658070";
		// Must add new Client before adding new Client Preferences
		clientDao.addNewClient(client1654658070,
				new ClientPortfolio(newClientId, new BigDecimal("10000"), new ArrayList<>()));
		String whereCondition = "client_id = " + newClientId;
		dao.addClientPreferences(clientPref1654658070);
		int newCount = countRowsInTableWhere(testJdbcTemplate, "client_preferences", whereCondition);
		assertTrue(newCount == 1, "Exactly one client preferences details added");
	}

	// Failure - Insertion of client preferences for non existent client
	@Test
	void testForAddClientPreferencesMethodWhenClientDoesntExist() {
		Exception e = assertThrows(DatabaseException.class, () -> {
			dao.addClientPreferences(clientPref1654658000);
		});
		assertEquals(e.getMessage(), "Client doesn't exist with this Client ID");
	}

	// Failure - Addition of client preferences to client with existing preferences
	// - Can only update
	@Test
	void testForAddClientPreferencesMethodForExistingClientPreferences() {
		Exception e = assertThrows(DatabaseException.class, () -> {
			dao.addClientPreferences(clientPref1654658069);
		});
		assertEquals(e.getMessage(), "Client already exists with this Client ID");
	}

	/* TESTS FOR UPDATING CLIENT PREFERENCES */
	// Successful updation of client preferences
	@Test
	void testForUpdatingClientPreferencesForValidClient() {
		String existingClientId = "541107416";
		String whereCondition = "length_of_investment = 'Long' and percentage_of_spend = 'Tier3' and is_advisor_accepted = 'false' and client_id = "
				+ existingClientId;
		dao.updateClientPreferences(clientPref541107416);
		int newCount = countRowsInTableWhere(testJdbcTemplate, "client_preferences", whereCondition);
		assertTrue(newCount == 1, "Updated preferences must exist");
	}

	// Failure - Updation of preferences for non existent client
	@Test
	void testForUpdatingClientPreferencesForNonExistentClient() {
		Exception e = assertThrows(DatabaseException.class, () -> {
			dao.updateClientPreferences(clientPref1654658000);
		});
		assertEquals(e.getMessage(), "Client doesn't exist with this Client ID");
	}

}
