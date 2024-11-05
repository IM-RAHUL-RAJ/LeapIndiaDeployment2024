package com.marshals.restcontroller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.transaction.annotation.Transactional;

import com.marshals.business.Order;
import com.marshals.business.Price;
import com.marshals.business.Trade;
import com.marshals.business.TradeHistory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = { "classpath:schema.sql", "classpath:data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class TradeControllerE2ETest {
	@Autowired
	private TestRestTemplate restTemplate; // for executing rest endpoints

	@Autowired
	private JdbcTemplate jdbcTemplate; // for executing SQL queries

	@Autowired
	private Logger logger;

//	Test client trade-history details
	List<Trade> tradeObjectList = List.of(
			new Trade(new Order("N123456", 20, new BigDecimal("1.03375"), "B", "541107416", "ORDER004", 4),
					new BigDecimal("1.035"), "TRADE004", new BigDecimal("20.7"), LocalDateTime.parse("2024-10-16T12:12:44")),
			new Trade(new Order("T67894", 50, new BigDecimal("95.92"), "B", "541107416", "ORDER003", 3),
					new BigDecimal("96"), "TRADE003", new BigDecimal("4800")),
			new Trade(new Order("T67890", 10000, new BigDecimal("1.03375"), "S", "541107416", "ORDER002", 4),
					new BigDecimal("1.03375"), "TRADE002", new BigDecimal("10337.5")));

	TradeHistory clientTradeHistoryList = new TradeHistory("541107416", tradeObjectList);

//    TESTS FOR GET TRADE HISTORY

//	 Get client trade-history for valid id
	@Test
	void testForGetClientTradeHistoryValidClientId() {
		String id = "541107416";
		String requestUrl = "/trade/trade-history/" + id;

		ResponseEntity<TradeHistory> response = restTemplate.getForEntity(requestUrl, TradeHistory.class);

//		 verify the response HTTP status is OK
		assertEquals(HttpStatus.OK, response.getStatusCode());

//			verify the client ID fetched
		TradeHistory expected = response.getBody();
		assertTrue(expected.getClientId().equals(clientTradeHistoryList.getClientId()));

//		verify the first trade out of fetched trades
		assertEquals(expected.getTrades().get(0), clientTradeHistoryList.getTrades().get(0));
		assertTrue(expected.getTrades().get(0).equals(clientTradeHistoryList.getTrades().get(0)));
	}
	
//	Get client trade history for invalid id
	@Test
	void testForGetClientTradeHistoryInvalidClientId() {
		String id = "invalid-id";
		String requestUrl = "/trade/trade-history/"+id;
		
		ResponseEntity<TradeHistory> response = 
				restTemplate.getForEntity(requestUrl, TradeHistory.class);
		
		assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
	}
	
//	Get client trade history for id that doesn't exist
		@Test
		void testForGetClientTradeHistoryClientIdDoesntExist() {
			String id = "1654658111";
			String requestUrl = "/trade/trade-history/"+id;
			
			ResponseEntity<TradeHistory> response = 
					restTemplate.getForEntity(requestUrl, TradeHistory.class);
			
			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		}
		

//    TESTS FOR EXECUTE TRADE

	@Test
	void testExecuteTrade() {
		Order order = new Order("N123456", 10, new BigDecimal("104.75"), "B", "541107416", "ABC123", 540983960);

		ResponseEntity<Trade> response = restTemplate.postForEntity("/trade/execute-trade", order, Trade.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		Trade trade = response.getBody();
		assertNotNull(trade);
		assertEquals(order.getQuantity(), trade.getQuantity());
		assertEquals(order.getInstrumentId(), trade.getInstrumentId());
	}

	@Test
	void testExecuteTrade_NullOrder() {
		ResponseEntity<Trade> response = restTemplate.postForEntity("/trade/execute-trade", null, Trade.class);
		assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
	}

	@Test
	void testExecuteTrade_InvalidDirection() {
		String invalidDirection = "X";
		Order order = new Order("N123456", 10, new BigDecimal("104.75"), invalidDirection, "541107416", "ABC123",
				540983960);

		ResponseEntity<Trade> response = restTemplate.postForEntity("/trade/execute-trade", order, Trade.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void testExecuteTrade_InsufficientBalance() {
		Order order = new Order("N123456", 1000, new BigDecimal("1040000.75"), "B", "541107416", "ABC123", 540983960);

		ResponseEntity<Trade> response = restTemplate.postForEntity("/trade/execute-trade", order, Trade.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void testExecuteTrade_InsufficientQuantityToSell() {
		Order order = new Order("N123456", 1000, new BigDecimal("104.75"), "S", "541107416", "ABC123", 540983960);

		ResponseEntity<Trade> response = restTemplate.postForEntity("/trade/execute-trade", order, Trade.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void testExecuteTrade_InstrumentNotInHoldings() {
		Order order = new Order("NonExistingInstrument", 10, new BigDecimal("104.75"), "S", "541107416", "ABC123",
				540983960);

		ResponseEntity<Trade> response = restTemplate.postForEntity("/trade/execute-trade", order, Trade.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void testExecuteTrade_NullTradeReturnedFromFMTS() {
		Order order = new Order("N123456", 10, new BigDecimal("1000.75"), "B", "541107416", "ABC123", 540983960);

		ResponseEntity<Trade> response = restTemplate.postForEntity("/trade/execute-trade", order, Trade.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
}
