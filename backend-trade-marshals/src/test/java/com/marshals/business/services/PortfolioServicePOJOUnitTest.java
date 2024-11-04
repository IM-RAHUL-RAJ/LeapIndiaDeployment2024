package com.marshals.business.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.marshals.business.ClientPortfolio;
import com.marshals.business.Holding;
import com.marshals.business.Order;
import com.marshals.business.Trade;
import com.marshals.integration.ClientPortfolioDao;
import com.marshals.integration.DatabaseException;

class PortfolioServicePOJOUnitTest {
	@Mock
	ClientPortfolioDao mockDao;
	@InjectMocks
	PortfolioService service;

	private List<ClientPortfolio> clientPortfolios;

	@BeforeEach
	void setUp() throws Exception {

		List<Holding> holdingsOf1654658069 = new ArrayList<Holding>(
				List.of(new Holding("Q123", 2, new BigDecimal("105")), new Holding("Q456", 1, new BigDecimal("340"))));
		List<Holding> holdingsOf541107416 = new ArrayList<Holding>(
				List.of(new Holding("C100", 10000, new BigDecimal("95.67")),
						new Holding("T67890", 10, new BigDecimal("1.033828125"))));
		// Test client portfolios
		clientPortfolios = new ArrayList<ClientPortfolio>(
				List.of(new ClientPortfolio("1654658069", new BigDecimal("10000"), holdingsOf1654658069),
						new ClientPortfolio("541107416", new BigDecimal("20000"), holdingsOf541107416)));

		// Initializing the Portfolio Service with a Mock Dao
		MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() throws Exception {
		service = null;
		clientPortfolios = null;
	}

	@Test
	void testGetClientPortfolio() {
		String clientId = "541107416"; // Client with a portfolio
		ClientPortfolio expected = clientPortfolios.get(1);
		// Mocking the dao method
		Mockito.when(mockDao.getClientPortfolio(clientId)).thenReturn(expected);

		ClientPortfolio clientPortfolio = service.getClientPortfolio(clientId);
		assertEquals(clientPortfolio, expected, "Must return client portfolio");
	}

	@Test
	void testGetClientPortfolioOfNonExistentClientShouldThrowException() {
		String clientId = "541107416"; // Client with a portfolio
		String errorMsg = "Client ID does not exist";
		// Mock the behavior of Dao method to throw exception
		Mockito.doThrow(new DatabaseException(errorMsg)).when(mockDao).getClientPortfolio(clientId);
		Exception e = assertThrows(DatabaseException.class, () -> {
			service.getClientPortfolio(clientId);
		});
		assertEquals(e.getMessage(), errorMsg);
	}

	@Test
	void shouldNotGetForNullObject() {
		assertThrows(NullPointerException.class, () -> {
			service.getClientPortfolio(null);
		});
	}

	@Test
	void shouldNotUpdateForNullObject() {
		assertThrows(NullPointerException.class, () -> {
			service.updateClientPortfolio(null);
		});
	}

	@Test
	void testUpdatePortfolioForBuyTradeOfExistingHoldings() {
		String instrumentId = "C100";
		BigDecimal execPrice = new BigDecimal("100");
		Integer quantity = 2;

		String existingClientId = "541107416";

		// Create Order object ie passed
		Order order = new Order(instrumentId, quantity, new BigDecimal("99.2"), "B", existingClientId, "order1",
				1425922638);
		Trade trade = new Trade(order, execPrice, "trade1", execPrice.multiply(new BigDecimal(quantity)));

		// Get old clientportfolio
		Mockito.when(mockDao.getClientPortfolio(existingClientId)).thenReturn(clientPortfolios.get(1));
		ClientPortfolio oldClientPortfolio = service.getClientPortfolio(existingClientId);
		BigDecimal oldCurrBalance = oldClientPortfolio.getCurrBalance();
		// New balance and holdings
		BigDecimal newCurrBalance = oldCurrBalance.subtract(execPrice.multiply(new BigDecimal(quantity)));
		Holding updatedHolding = new Holding("C100", 10002, new BigDecimal("95.67"));
		service.updateClientPortfolio(trade);
		Mockito.verify(mockDao).updateClientBalance(existingClientId, newCurrBalance);
		Mockito.verify(mockDao).updateClientHoldings(existingClientId, updatedHolding);

	}

	@Test
	void testUpdatePortfolioForBuyTradeOfNewInstrument() {
		String instrumentId = "Q678";
		BigDecimal execPrice = new BigDecimal("105");
		Integer quantity = 1;

		String existingClientId = "541107416";

		// Create Order object if needed

		Order order = new Order(instrumentId, quantity, new BigDecimal("105"), "B", existingClientId, "order1",
				1425922638);
		Trade trade = new Trade(order, execPrice, "trade1", execPrice.multiply(new BigDecimal(quantity)));

		// Get old clientportfolio
		Mockito.when(mockDao.getClientPortfolio(existingClientId)).thenReturn(clientPortfolios.get(1));
		ClientPortfolio oldClientPortfolio = service.getClientPortfolio(existingClientId);
		BigDecimal oldCurrBalance = oldClientPortfolio.getCurrBalance();
		// New balance and holdings
		BigDecimal newCurrBalance = oldCurrBalance.subtract(execPrice.multiply(new BigDecimal(quantity)));
		Holding updatedHolding = new Holding(instrumentId, quantity, execPrice);
		service.updateClientPortfolio(trade);
		Mockito.verify(mockDao).updateClientBalance(existingClientId, newCurrBalance);
		// Must call add client Portfolio
		Mockito.verify(mockDao).addClientHoldings(existingClientId, updatedHolding);
	}

	@Test
	void testUpdatePortfolioForSellTradeOfExistingHoldings() {
		String instrumentId = "C100";
		BigDecimal execPrice = new BigDecimal("100");
		Integer quantity = 100;

		String existingClientId = "541107416";

		// Create Order object ie passed
		Order order = new Order(instrumentId, quantity, new BigDecimal("99.2"), "S", existingClientId, "order1",
				1425922638);
		Trade trade = new Trade(order, execPrice, "trade1", execPrice.multiply(new BigDecimal(quantity)));

		// Get old clientportfolio
		Mockito.when(mockDao.getClientPortfolio(existingClientId)).thenReturn(clientPortfolios.get(1));
		ClientPortfolio oldClientPortfolio = service.getClientPortfolio(existingClientId);
		BigDecimal oldCurrBalance = oldClientPortfolio.getCurrBalance();
		// New balance and holdings
		BigDecimal newCurrBalance = oldCurrBalance.add(execPrice.multiply(new BigDecimal(quantity)));
		Holding updatedHolding = new Holding(instrumentId, 9900, new BigDecimal("93.73"));
		service.updateClientPortfolio(trade);
		Mockito.verify(mockDao).updateClientBalance(existingClientId, newCurrBalance);
		Mockito.verify(mockDao).updateClientHoldings(existingClientId, updatedHolding);

	}

}
