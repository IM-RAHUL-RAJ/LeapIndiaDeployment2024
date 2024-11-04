package com.marshals.business.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTableWhere;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import com.marshals.business.Price;
import com.marshals.business.Trade;
import com.marshals.business.TradeHistory;
import com.marshals.integration.DatabaseException;

@SpringBootTest
@Transactional
class TradeServiceIntegrationTest {

	@Autowired
	private TradeService service;
	
	@Autowired
	private FMTSService fmtsService;

	@Autowired
	private JdbcTemplate testJdbcTemplate;

	List<Holding> holdingsOf1425922638 = new ArrayList<Holding>(
			List.of(new Holding("C100", 100, new BigDecimal("95.67")), new Holding("Q456", 1, new BigDecimal("340"))));
	// Test client portfolios
	List<ClientPortfolio> clientPortfolios = new ArrayList<ClientPortfolio>(
			List.of(new ClientPortfolio("1425922638", new BigDecimal("10000"), holdingsOf1425922638), //Sufficient balance
					new ClientPortfolio("1425922638", new BigDecimal("20"), holdingsOf1425922638) // Insufficient balance
			));

	private List<Price> prices;

	@BeforeEach
	void setUp() throws Exception {
		prices = service.getPriceList();
	}

	@AfterEach
	void tearDown() throws Exception {
		service = null;
	}

	/* FMTS Get prices test */
	@Test
	public void testGetAllPricesShouldReturn13Prices() {
		assertEquals(13, prices.size(), "Should return 13 instrument prices");
	}

	@Test
	public void testGetAllPricesFirstPriceShouldBeAsExpected() {
		Price price = prices.get(0);
		assertEquals(new BigDecimal("104.75"), price.getAskPrice());
		assertEquals(new BigDecimal("104.25"), price.getBidPrice());
		assertEquals("21-AUG-19 10.00.01.042000000 AM GMT", price.getPriceTimestamp());
		assertEquals("N123456", price.getInstrument().getInstrumentId());
	}

	/* Get Trade History Tests */
	@Test
	public void testGetClientTradeHistory() {
		String clientId = "541107416";

		TradeHistory actualTradeHistory = service.getClientTradeHistory(clientId);
		assertTrue(actualTradeHistory.getTrades().size() >= 1);
	}

	@Test
	public void testGetClientTradeHistoryThrowsNullPointerException() {
		Exception e = assertThrows(NullPointerException.class, () -> service.getClientTradeHistory(null));
		assertEquals(e.getMessage(), "Client ID must not be null");
	}

	@Test
	public void testGetClientTradeHistoryThrowsExceptionForNonExistingClient() {
		String clientId = "nonexistentClient";
		Exception e = assertThrows(DatabaseException.class, () -> {
			service.getClientTradeHistory(clientId);
		});
		assertEquals(e.getMessage(), "Client has no trades");
	}

	@Test
	public void testGetTradesByClientIdShouldNotReturnMoreThan100Trades() {
		String clientId = "541107416";
		TradeHistory actualTradeHistory = service.getClientTradeHistory(clientId);
		assertTrue(actualTradeHistory.getTrades().size() <= 100);
	}

	/* ADD Trade Tests */
	@Test
	public void testAddTradesToInsertTradeAndOrderTable() {
		String clientId = "1654658069";
		Order order = new Order("NT123456", 10, new BigDecimal("100.00"), "B", clientId, "ORDER100", 123453);
		Trade trade = new Trade(order, new BigDecimal("110.00"), "ORDER100TR", new BigDecimal("1100.00"));
		service.addTrade(trade);
		assertEquals(1, countRowsInTableWhere(testJdbcTemplate, "CLIENT_ORDER", """
					 INSTRUMENT_ID = 'NT123456'
				 and ORDER_ID = 'ORDER100'
				"""));
		assertEquals(1, countRowsInTableWhere(testJdbcTemplate, "CLIENT_TRADE", """
					 TRADE_ID = 'ORDER100TR'
				 and ORDER_ID = 'ORDER100'
				"""));

	}

	@Test
	void testAddTradeThrowsExceptionForExistingOrderId() {
		Order newOrder = new Order("T67890", 10000, new BigDecimal("1.03375"), "S", "541107416", "ORDER002", 4);
		Trade newTrade = new Trade(newOrder, new BigDecimal("1.03375"), "TRADE002", new BigDecimal("10337.5"));
		assertThrows(DatabaseException.class, () -> {
			service.addTrade(newTrade);
		});
	}

	/* EXECUTE TRADE */
	@Test
	public void testExecuteSuccessfulBuyTradeUpdatesTradeTable() {
		String existingClientId = "541107416";
		UUID uuid = UUID.randomUUID();
		Integer token = fmtsService.verifyClient("himanshu@gmail.com", existingClientId).getToken();
		String orderId = "ORDER0020";
		Order order = new Order("N123456", 2, new BigDecimal("104.75"), "B", existingClientId, "ORDER0020", token);
		Trade trade = service.executeTrade(order);
		assertEquals(1, countRowsInTableWhere(testJdbcTemplate, "CLIENT_TRADE",
				"TRADE_ID = '" + trade.getTradeId() + "' and ORDER_ID = '" + orderId + "'"));
	}

	@Test
	public void testExecuteSuccessfulSellTradeUpdatesTradeTable() {
		String existingClientId = "541107416";
		UUID uuid = UUID.randomUUID();
		Integer token = fmtsService.verifyClient("himanshu@gmail.com", existingClientId).getToken();
		String orderId = "ORDER0021";
		Order order = new Order("C100", 10, new BigDecimal("95.92"), "S", existingClientId, "ORDER0021", token);
		Trade trade = service.executeTrade(order);
		assertEquals(1, countRowsInTableWhere(testJdbcTemplate, "CLIENT_TRADE",
				"TRADE_ID = '" + trade.getTradeId() + "' and ORDER_ID = '" + orderId + "'"));
	}

	@Test
	public void testExecuteSellTradeForNonExistentHoldingThrowsException() {
		String existingClientId = "1425922638";
		UUID uuid = UUID.randomUUID();
		String orderId = uuid.toString();
		Order order = new Order("C100", 10, new BigDecimal("104.75"), "S", existingClientId, orderId, 123);
		Exception e = assertThrows(IllegalArgumentException.class, () -> {
			service.executeTrade(order);
		});
		assertEquals(e.getMessage(), "Instrument not part of holdings! Cannot sell the instrument");

	}

}
