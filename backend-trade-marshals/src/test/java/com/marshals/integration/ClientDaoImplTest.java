package com.marshals.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTable;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTableWhere;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.marshals.business.Client;
import com.marshals.business.ClientIdentification;
import com.marshals.business.ClientPortfolio;

@SpringBootTest
@Transactional
class ClientDaoImplTest {
	
	@Autowired
	private ClientDaoImpl dao;
	
	@Autowired
	private JdbcTemplate testJdbcTemplate;
	
	//Few test client
	Client client1654658069 = new Client("sowmya@gmail.com","1654658069", "Marsh2024", "Sowmya", "11/12/2002", "India", 
			new ArrayList<>(List.of(new ClientIdentification("Aadhar","123456789102"))), true); //Existing client
	Client client1654658000 = new Client("nonexistent.client@gmail.com","1654658000", "Password1234", "Non Existent client", "12/10/1994", "USA", 
			new ArrayList<>(List.of(new ClientIdentification("SSN","1953826343"))), false); //Non existent client
	Client client1654658070 = new Client("sam@gmail.com","1654658070", "Password1234", "Sam", "12/11/2000", "USA", 
			new ArrayList<>(List.of(new ClientIdentification("SSN","1643846323"))), false); //New client to insert
	
	
	@Test
	void testForDAOToHaveBeenIntialized() {
		assertNotNull(dao);
	}
	
	/*CLIENT*/
	 
	/*TESTS FOR CLIENT EMAIL VERIFICATION*/
	//Existing email to be found
	@Test
	void testForClientWithExistingEmailToBeFound() {
		String existingEmail = "sowmya@gmail.com";
		Boolean clientExists = dao.verifyClientEmail(existingEmail);
		assertTrue(clientExists, "Client must be found");
	}
	//Non existing email to not be found
	@Test
	void testForClientWithNonExistingEmailToNotBeFound() {
		String nonExistingEmail = "client_nonexist@gmail.com";
		Exception e = assertThrows(DatabaseException.class, () -> {
			dao.verifyClientEmail(nonExistingEmail);
		});
		assertEquals(e.getMessage(),"Client with given email doesnt exist");
	}
	//Invalid email to throw exception
	@Test
	void testForClientWithInvalidEmailToThrowException() {
		String invalidEmail = "client_invalid";
		Exception e = assertThrows(IllegalArgumentException.class, () -> {
			dao.verifyClientEmail(invalidEmail);
		});
		assertEquals(e.getMessage(),"Client Email Format is invalid");
	}
	
	/*TESTS FOR CLIENT LOGIN*/
	//Successful login of client
	@Test
	void testForSuccessfulRetrievalOfClientDetailsWithValidCredentials() {
		String validClientEmail = "sowmya@gmail.com";
		String validClientPassword = "Marsh2024";
		Client client = dao.getClientAtLogin(validClientEmail, validClientPassword);
		assertEquals(client1654658069, client, "Successful login must return Client details");
	}
	//Login of client with non existent email
	@Test
	void testForLoginOfNonExistentClientThrowsException() {
		Exception e = assertThrows(DatabaseException.class, () -> {
			dao.getClientAtLogin(client1654658000.getEmail(), client1654658000.getPassword());
		}); 
		assertEquals(e.getMessage(), "Logging in Client doesnt exist");
	}
	//Login of existing client with invalid credentials - password mismatch
	@Test
	void testForLoginOfExistingClientWithInvalidCredentailsThrowsException() {
		String existingClientEmail = "sowmya@gmail.com";
		String invalidClientPassword = "Password123";
		Exception e = assertThrows(IllegalArgumentException.class, () -> {
			dao.getClientAtLogin(existingClientEmail, invalidClientPassword);
		}); 
		assertEquals(e.getMessage(), "Password does not match logging in Client's credentials");
	}
	
//	/*TESTS FOR CLIENT REGISTRATION*/
	//Testing successful retrieval of all client identification details
	@Test
	void testForRetrievalOfAllClientIdentificationDetails() {
		List<ClientIdentification> identificationDetails = dao.getAllClientIdentificationDetails();
		assertTrue(identificationDetails.size()>=6,"Must retrieve atleast 6 client identification details");
	}
	//Successful insetion must increase row count in client table
	@Test
	void testForSuccessfulAdditionOfNewClientDetailsInClientTable() {
		int oldCount = countRowsInTable(testJdbcTemplate, "client");
		dao.addNewClient(client1654658070, new ClientPortfolio(client1654658070.getClientId(),new BigDecimal("10000"),new ArrayList<>()));
		int newCount = countRowsInTable(testJdbcTemplate, "client");
		assertTrue(newCount == oldCount+1, "Client Table count must increase by one");
	}
	//Successful insertion must add correct data in client identification table
	@Test
	void testForSuccessfulAdditionOfNewClientDetailsInClientIdentificationTable() {
		String newClientId = "1654658070";
		String whereCondition = "client_id = "+ newClientId;
		dao.addNewClient(client1654658070, new ClientPortfolio(client1654658070.getClientId(),new BigDecimal("10000"),new ArrayList<>()));
		int newCount = countRowsInTableWhere(testJdbcTemplate, "client_identification", whereCondition);
		assertTrue(newCount >= 1, "One or more client identification details added");
	}
	//Exisitng client registration throws error
	@Test
	void testForAdditionOfExistingClientDetailsShouldThrowException(){
		String existingClientId = "1654658069";
		Exception e = assertThrows(DatabaseException.class, () -> {
			dao.addNewClient(client1654658069, 
					new ClientPortfolio(existingClientId,new BigDecimal("10000"),new ArrayList<>()));
		});
		assertEquals(e.getMessage(),"Cannot insert client with ID "+existingClientId);
	}

}
