package com.marshals.business.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.marshals.business.FMTSValidatedClient;
import com.marshals.business.Order;
import com.marshals.business.Price;
import com.marshals.business.Trade;
import com.marshals.integration.FMTSException;

@SpringBootTest
@Transactional
class FMTSServiceIntegrationTest {

	@Autowired
	private FMTSService service;

	// Mock client emails
	String newClientEmail = "sam@gmail.com";
	String existingClientEmail = "sowmya@gmail.com";
	String existingClientID = "1654658069";
	
	@AfterEach
	void tearDown() throws Exception {
		service = null;
	}
	
	/*FMTS Client Validation at Registration*/
	@Test
	void testForValidatationOfInvalidClientEmailAtRegistrationThrowsException() {
		Exception e = assertThrows(FMTSException.class, () -> {
			service.verifyClient("invalid-email");
		});
		assertEquals("FMTS couldnt validate new Client's Email",e.getMessage());
	}
	@Test
	void testForSuccessfulValidatationOfClientEmailAtRegistration() {
		FMTSValidatedClient validatedClient = service.verifyClient(newClientEmail);
		assertNotNull(validatedClient);
		assertEquals(validatedClient.getEmail(),newClientEmail);
	}
	
	/*FMTS Client Validation at Login*/
	@Test
	void testForValidatationOfInvalidClientEmailAtLoginThrowsException() {
		Exception e = assertThrows(FMTSException.class, () -> {
			service.verifyClient("invalid-email",existingClientID);
		});
		assertEquals("Logging in Client's validation credentials dont match",e.getMessage());
	}
	@Test
	void testForValidatationOfInvalidClientCredentialsAtLoginThrowsException() {
		Exception e = assertThrows(FMTSException.class, () -> {
			service.verifyClient(existingClientEmail,existingClientID+"1234");
		});
		assertEquals("Logging in Client's validation credentials dont match",e.getMessage());
	}
	@Test
	void testForSuccessfulValidatationOfClientCredentialsAtLogin() {
		FMTSValidatedClient validatedClient = service.verifyClient(existingClientEmail,existingClientID);
		assertNotNull(validatedClient);
		assertTrue(validatedClient.getEmail().equals(existingClientEmail) && validatedClient.getClientId().equals(existingClientID));
	}
	
	/*FMTS Retrieval of Live Prices*/
	@Test
	void testForSuccessfulRetrievalOfLivePrices() {
		List<Price> prices = service.getLivePrices();
		assertNotNull(prices);
		assertTrue(prices.size()>=13,"Atleast 13 Live Prices must be retrieved");
	}
	
	/*FMTS Execution of Trade given Order*/
	@Test
	void testForExecutionOfTradeWithExpiredTokenThrowsException() {
		Order order = new Order("N123456", 10, new BigDecimal("100"), "B", existingClientID, "ORDER001", 123);
		Exception e = assertThrows(FMTSException.class, () -> {
			service.createTrade(order);
		});
		assertEquals("Token expired or is invalid",e.getMessage());
	}
	@Test
	void testForExecutionOfTradeWithInvalidOrderThrowsException() {
		//Set valid token but invalid instrument in order
		FMTSValidatedClient validatedClient = service.verifyClient(existingClientEmail,existingClientID);
		Order order = new Order("INS124", 10, new BigDecimal("100"), "B", existingClientID, "ORDER001", validatedClient.getToken());
		Exception e = assertThrows(FMTSException.class, () -> {
			service.createTrade(order);
		});
		assertEquals("Invalid order, trade returned null",e.getMessage());
	}
	@Test
	void testForExecutionOfTradeWithOutOfRangeTargetPriceThrowsException() {
		//Set valid token but invalid target price in order
		FMTSValidatedClient validatedClient = service.verifyClient(existingClientEmail,existingClientID);
		Order order = new Order("N123456", 10, new BigDecimal("10"), "B", existingClientID, "ORDER001", validatedClient.getToken());
		Exception e = assertThrows(FMTSException.class, () -> {
			service.createTrade(order);
		});
		assertEquals("Target price is not in the expected range of execution price",e.getMessage());
	}
	@Test
	void testForSuccessfulExecutionOfTradeWithValidOrder() {
		//Set valid token but invalid instrument in order
		FMTSValidatedClient validatedClient = service.verifyClient(existingClientEmail,existingClientID);
		Order order = new Order("N123456", 10, new BigDecimal("103.25"), "B", existingClientID, "ORDER001", validatedClient.getToken());
		Trade trade = service.createTrade(order);
		assertNotNull(trade);
		assertTrue(trade.getOrder().equals(order) && trade.getInstrumentId().equals(order.getInstrumentId()));
	}
}
