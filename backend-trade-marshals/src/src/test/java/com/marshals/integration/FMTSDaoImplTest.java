package com.marshals.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.marshals.business.FMTSValidatedClient;
import com.marshals.business.Order;
import com.marshals.business.Price;
import com.marshals.business.Trade;

@SpringBootTest
@Transactional
class FMTSDaoImplTest {
	//Mock client emails
	String newClientEmail = "sam@gmail.com";
	String existingClientEmail = "sowmya@gmail.com";
	String existingClientID = "1654658069";

	@Autowired
	private FMTSDaoImpl dao;
	
	@Test
	void testForDAOToHaveBeenIntialized() {
		assertNotNull(dao);
	}
	
	/*FMTS Client Validation at Registration*/
	@Test
	void testForValidatationOfNullClientEmailAtRegistrationThrowsException() {
		Exception e = assertThrows(FMTSException.class, () -> {
			dao.verifyClient(null);
		});
		assertEquals("FMTS couldnt validate new Client's Email",e.getMessage());
	}
	@Test
	void testForValidatationOfInvalidClientEmailAtRegistrationThrowsException() {
		Exception e = assertThrows(FMTSException.class, () -> {
			dao.verifyClient("invalid-email");
		});
		assertEquals("FMTS couldnt validate new Client's Email",e.getMessage());
	}
	@Test
	void testForSuccessfulValidatationOfClientEmailAtRegistration() {
		FMTSValidatedClient validatedClient = dao.verifyClient(newClientEmail);
		assertNotNull(validatedClient);
		assertEquals(validatedClient.getEmail(),newClientEmail);
	}
	
	/*FMTS Client Validation at Login*/
	@Test
	void testForValidatationOfNullClientEmailAtLoginThrowsException() {
		Exception e = assertThrows(FMTSException.class, () -> {
			dao.verifyClient(null,existingClientID);
		});
		assertEquals("Logging in Client's validation credentials dont match",e.getMessage());
	}
	@Test
	void testForValidatationOfInvalidClientEmailAtLoginThrowsException() {
		Exception e = assertThrows(FMTSException.class, () -> {
			dao.verifyClient("invalid-email",existingClientID);
		});
		assertEquals("Logging in Client's validation credentials dont match",e.getMessage());
	}
	@Test
	void testForValidatationOfInvalidClientCredentialsAtLoginThrowsException() {
		Exception e = assertThrows(FMTSException.class, () -> {
			dao.verifyClient(existingClientEmail,existingClientID+"1234");
		});
		assertEquals("Logging in Client's validation credentials dont match",e.getMessage());
	}
	@Test
	void testForSuccessfulValidatationOfClientCredentialsAtLogin() {
		FMTSValidatedClient validatedClient = dao.verifyClient(existingClientEmail,existingClientID);
		assertNotNull(validatedClient);
		assertTrue(validatedClient.getEmail().equals(existingClientEmail) && validatedClient.getClientId().equals(existingClientID));
	}
	
	/*FMTS Retrieval of Live Prices*/
	@Test
	void testForSuccessfulRetrievalOfLivePrices() {
		List<Price> prices = dao.getLivePrices();
		assertNotNull(prices);
		assertTrue(prices.size()>=13,"Atleast 13 Live Prices must be retrieved");
	}
	
	/*FMTS Execution of Trade given Order*/
	@Test
	void testForExecutionOfTradeWithExpiredTokenThrowsException() {
		Order order = new Order("N123456", 10, new BigDecimal("100"), "B", existingClientID, "ORDER001", 123);
		Exception e = assertThrows(FMTSException.class, () -> {
			dao.createTrade(order);
		});
		assertEquals("Token expired or is invalid",e.getMessage());
	}
	@Test
	void testForExecutionOfTradeWithInvalidOrderReturnsNullTrade() {
		//Set valid token but invalid instrument in order
		FMTSValidatedClient validatedClient = dao.verifyClient(existingClientEmail,existingClientID);
		Order order = new Order("INS124", 10, new BigDecimal("100"), "B", existingClientID, "ORDER001", validatedClient.getToken());
		Exception e = assertThrows(FMTSException.class, () -> {
			dao.createTrade(order);
		});
		assertEquals("Invalid order, trade returned null",e.getMessage());
	}
	@Test
	void testForExecutionOfTradeWithOutOfRangeTargetPriceThrowsException() {
		//Set valid token but invalid target price in order
		FMTSValidatedClient validatedClient = dao.verifyClient(existingClientEmail,existingClientID);
		Order order = new Order("N123456", 10, new BigDecimal("10000"), "B", existingClientID, "ORDER001", validatedClient.getToken());
		Exception e = assertThrows(FMTSException.class, () -> {
			dao.createTrade(order);
		});
		assertEquals("Target price is not in the expected range of execution price",e.getMessage());
	}
	@Test
	void testForSuccessfulExecutionOfTradeWithValidOrder() {
		//Set valid token but invalid instrument in order
		FMTSValidatedClient validatedClient = dao.verifyClient(existingClientEmail,existingClientID);
		Order order = new Order("N123456", 10, new BigDecimal("103.25"), "B", existingClientID, "ORDER001", validatedClient.getToken());
		Trade trade = dao.createTrade(order);
		trade.getOrder().setOrderId(order.getOrderId());
		assertNotNull(trade);
		System.out.println(trade.getOrder().toString());
		System.out.println(order.toString());
		assertTrue(trade.getOrder().equals(order));
		assertTrue(trade.getInstrumentId().equals(order.getInstrumentId()));
	}
}
