package com.marshals.business.services;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.marshals.business.ClientPortfolio;
import com.marshals.business.Holding;
import com.marshals.business.Order;
import com.marshals.business.Trade;
import com.marshals.integration.DatabaseException;

@SpringBootTest
@Transactional
class PortfolioServiceIntegrationTest {

	private List<ClientPortfolio> clientPortfolios;

	@Autowired
	private PortfolioService service;

	@Autowired
	private JdbcTemplate testJdbcTemplate;

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
						new ClientPortfolio("541107416", new BigDecimal("10000"), holdingsOf541107416)));

	}

	@AfterEach
	void tearDown() throws Exception {
		service = null;
		clientPortfolios = null;
	}

	@Test
	void testGetClientPortfolio() {
		String clientId = "541107416"; // Client with a portfolio
		ClientPortfolio clientPortfolio = service.getClientPortfolio(clientId);
		assertNotNull(clientPortfolio);
		assertEquals(clientPortfolio.getClientId(), clientId);
	}

	@Test
	void testGetClientPortfolioOfNonExistentClientShouldThrowException() {
		String clientId = "NonExistingClientId"; // Client with a portfolio
		String errorMsg = "Client ID does not exist";
		// Mock the behavior of Dao method to throw exception
		Exception e = assertThrows(DatabaseException.class, () -> {
			ClientPortfolio clientPortfolio = service.getClientPortfolio(clientId);
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
		// Order ('C100', 1000, 95.92, 'B', '541107416', 'ORDER001', 3)
		// Trade ('TRADE001', 'ORDER001', 95.92, 95920.00, to_date('2024-09-22
		// 21:31:04', 'yyyy-mm-dd hh24-mi-ss'))
		String existingClientId = "541107416";
		Order order = new Order("C100", 100, new BigDecimal("95.92"), "B", existingClientId, "ORDER001", 3);
		Trade trade = new Trade(order, new BigDecimal("95.92"), "TRADE001", new BigDecimal("95920.00"));
		
		ClientPortfolio clientPortfolio = service.getClientPortfolio(existingClientId);
		System.out.println(clientPortfolio.getHoldings().get(0).getQuantity());
		assertEquals(clientPortfolio.getHoldings().get(0).getQuantity(), 1000);
		service.updateClientPortfolio(trade);
		assertEquals(clientPortfolio.getHoldings().get(0).getQuantity(), 1100);
	}

	@Test
	void testUpdatePortfolioForBuyTradeOfNewInstrument() {
		String instrumentId = "Q678";
		BigDecimal execPrice = new BigDecimal("105");
		Integer quantity = 1;

		String existingClientId = "541107416";
		Order order = new Order(instrumentId, quantity, new BigDecimal("105"), "B", existingClientId, "order1",
				1425922638);
		Trade trade = new Trade(order, execPrice, "trade1", execPrice.multiply(new BigDecimal(quantity)));
		ClientPortfolio oldClientPortfolio = service.getClientPortfolio(existingClientId);
		int oldHoldingsSize = oldClientPortfolio.getHoldings().size();
		service.updateClientPortfolio(trade);
		assertEquals(oldHoldingsSize + 1, service.getClientPortfolio(existingClientId).getHoldings().size());

	}

	@Test
	void testUpdatePortfolioForSellTradeOfExistingHoldings() {
		String existingClientId = "541107416";
		Order order = new Order("C100", 100, new BigDecimal("95.92"), "S", existingClientId, "ORDER001", 3);
		Trade trade = new Trade(order, new BigDecimal("95.92"), "TRADE001", new BigDecimal("95920.00"));
		ClientPortfolio clientPortfolio = service.getClientPortfolio(existingClientId);

		System.out.println(clientPortfolio.getHoldings().get(0).getQuantity());
		assertEquals(clientPortfolio.getHoldings().get(0).getQuantity(), 1000);
		service.updateClientPortfolio(trade);
		assertEquals(clientPortfolio.getHoldings().get(0).getQuantity(), 900);

	}

}
