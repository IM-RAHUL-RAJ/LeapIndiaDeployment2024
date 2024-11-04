package com.marshals.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTableWhere;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.marshals.business.ClientPortfolio;
import com.marshals.business.Holding;

@SpringBootTest
@Transactional
public class ClientPortfolioDaoImplTest {

	@Autowired
	private Logger logger;
	@Autowired
	@Qualifier("clientPortfolioDao")
	private ClientPortfolioDao dao;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/* GET CLIENT PORTFOLIO */
	@Test
	void testGetClientPortfolioNotNull() {
		String clientId = "541107416";
		ClientPortfolio clientPortfolio = dao.getClientPortfolio(clientId);
		assertNotNull(clientPortfolio);
	}

	@Test
	void testGetClientPortfolioHoldingsForClientWithoutHoldings() {
		String clientId = "1425922638";
		ClientPortfolio clientPortfolio = dao.getClientPortfolio(clientId);
		assertTrue(clientPortfolio.getHoldings().size() == 0);
	}

	@Test
	void testGetClientPortfolioHoldingsForClientWithHoldings() {
		String clientId = "541107416";
		ClientPortfolio clientPortfolio = dao.getClientPortfolio(clientId);
		assertTrue(clientPortfolio.getHoldings().size() >= 1);
	}

	@Test
	void testGetClientPortfolioThrowsExceptionForInvalidClientId() {
		String clientId = "nonExistingClientId";
		Exception e = assertThrows(DatabaseException.class, () -> {
			dao.getClientPortfolio(clientId);
		});
		assertEquals("Client ID does not exist", e.getMessage());
	}

	/* UPDATE CLIENT PORTFOLIO */

	// Updating current balance
	@Test
	void testSuccessfulUpdationOfClientBalance() throws SQLException {
		String clientId = "541107416"; // Existing client
		BigDecimal currBalance = new BigDecimal("10453").setScale(4);
		String whereCondition = "client_id = '541107416' and curr_balance = " + currBalance;
		dao.updateClientBalance(clientId, currBalance);
		int newSize = countRowsInTableWhere(jdbcTemplate, "client", whereCondition);
		assertTrue(newSize == 1);
	}

	// Adding holdings
	@Test
	void testSuccessfulAddClientHoldingOfClientWithHolding() throws SQLException {
		String clientId = "541107416"; // Has holdings
		Holding holding = new Holding("N123789", 1, new BigDecimal("31100"));
		int oldSize = countRowsInTableWhere(jdbcTemplate, "holdings", "client_id = " + clientId);
		dao.addClientHoldings(clientId, holding);
		int newSize = countRowsInTableWhere(jdbcTemplate, "holdings", "client_id = " + clientId);
		assertTrue(newSize == oldSize + 1);
	}
	@Test
	void testSuccessfulAddClientHoldingOfClientWithNoHoldings() throws SQLException {
		String clientId = "1654658069"; // Has no holdings
		Holding holding = new Holding("N123456", 1, new BigDecimal("104.50"));
		dao.addClientHoldings(clientId, holding);
		int newSize = countRowsInTableWhere(jdbcTemplate, "holdings", """
				client_id = '1654658069'
				""");
		assertTrue(newSize == 1);
	}
	@Test
	void testAddClientHoldingOfNonExistentClientThrowsException() throws SQLException {
		String clientId = "nonexistent";
		Holding holding = new Holding("N123456", 1, new BigDecimal("104.50"));
		assertThrows(DatabaseException.class, () -> {
			dao.addClientHoldings(clientId, holding);
		});
	}

	// Updating existing holdings
	@Test
	void testSuccessfulUpdationOfClientHoldings() throws SQLException {
		String clientId = "541107416"; // Has holdings
		Holding holding = new Holding("C100", 10, new BigDecimal("104.50"));

		dao.updateClientHoldings(clientId, holding);
		int newSize = countRowsInTableWhere(jdbcTemplate, "holdings", """
					 instrument_id = 'C100'
					 and quantity = '10'
				""");
		assertTrue(newSize == 1);
	}
	@Test
	void testUpdationOfNonExistentClientHoldingsThrowsException() throws SQLException {
		String clientId = "541107416"; // Has holdings
		Holding holding = new Holding("nonexistent", 10, new BigDecimal("104.50"));
		assertThrows(DatabaseException.class, () -> {
			dao.updateClientHoldings(clientId, holding);
		});
	}
	@Test
	void testUpdationOfClientHoldingsOfNonExistentClientThrowsException() throws SQLException {
		String clientId = "nonexistent";
		Holding holding = new Holding("N123456", 10, new BigDecimal("104.50"));
		assertThrows(DatabaseException.class, () -> {
			dao.updateClientHoldings(clientId, holding);
		});
	}
}
