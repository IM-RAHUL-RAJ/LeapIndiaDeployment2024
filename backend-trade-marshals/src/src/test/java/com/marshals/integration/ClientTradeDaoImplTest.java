package com.marshals.integration;
 
import static org.junit.jupiter.api.Assertions.*;
 
 
import java.math.BigDecimal;
import java.sql.SQLException;

import com.marshals.business.Order;
import com.marshals.business.Trade;
import com.marshals.business.TradeHistory;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTable;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTableWhere;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
 
@SpringBootTest
@Transactional
class ClientTradeDaoImplTest {
	@Autowired
	private Logger logger;
	@Autowired
	@Qualifier("clientTradeDao")
	private ClientTradeDao dao;
	@Autowired
	private JdbcTemplate jdbcTemplate;
 
	@Test
	void verifyDaoNotNull() {
		assertNotNull(dao);
	}

	
	/*Get Client Trade History Tests*/
	@Test
	void testGetClientTradeHistoryNotNull() {
		String clientId = "541107416";
		TradeHistory tradeHistory = dao.getClientTradeHistory(clientId);
		assertNotNull(tradeHistory);
	}
	@Test
	void testGetClientTradeHistory() {
		String clientId = "541107416";
		TradeHistory tradeHistory = dao.getClientTradeHistory(clientId);
		assertTrue(tradeHistory.getTrades().size()>1);
	}
	@Test
	void testGetClientTradeHistoryFirstTrade() {
		String clientId = "541107416";
		TradeHistory tradeHistory = dao.getClientTradeHistory(clientId);
		assertNotNull(tradeHistory);
		for(Trade t: tradeHistory.getTrades()) {
			System.out.println("Trades -> " + t);
		}
		assertEquals(tradeHistory.getTrades().get(0).getInstrumentId(), "N123456");
	}
	@Test
	void testGetClientTradeHistoryThrowsExceptionForInvalidClientId() {
		String clientId = "nonExistingClientId";
		Exception e = assertThrows(DatabaseException.class, () -> {
			dao.getClientTradeHistory(clientId);
		});
		assertEquals("Client has no trades", e.getMessage());
	}
	
	/*Add Trade tests*/
	@Test
	void testAddTradeCheckIfBothOrderAndTradeTablesAreUpdated() throws SQLException {
		var rowCount = countRowsInTable(jdbcTemplate, "client_trade");
		// new order and trade to be inserted into the database
		Order newOrder = new Order("NT123456", 1000, new BigDecimal(45.678), "B", "739982664", "ORDER020", 20 );
		Trade newTrade = new Trade(newOrder, new BigDecimal(45.678), "TRADE020", new BigDecimal(45678));
		dao.addTrade(newTrade);
		// verify that the number of trade rows increased by 1
		assertEquals(rowCount + 1, countRowsInTable(jdbcTemplate, "client_trade"));
		// Verify that all Department properties were inserted correctly.
		assertEquals(1, countRowsInTableWhere(jdbcTemplate, "CLIENT_ORDER", """
								 INSTRUMENT_ID = 'NT123456'
							 and ORDER_ID = 'ORDER020'
						 """));
		assertEquals(1, countRowsInTableWhere(jdbcTemplate, "CLIENT_TRADE", """
				 TRADE_ID = 'TRADE020'
		 """));
	}
	@Test
	void testAddTradeThrowsExceptionForExistingOrderId() {
		Order newOrder = new Order("T67890", 10000, new BigDecimal("1.03375"), "S", "541107416", "ORDER002", 4 );
		Trade newTrade = new Trade(newOrder, new BigDecimal("1.03375"), "TRADE002", new BigDecimal("10337.5"));
		assertThrows(DatabaseException.class, ()->{
			dao.addTrade(newTrade);
		});
	}
	

	
	
}