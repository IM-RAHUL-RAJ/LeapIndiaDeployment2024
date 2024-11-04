package com.marshals.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.marshals.business.Holding;

@SpringBootTest
@Transactional
class ClientActivityReportDaoImplTest {	
	@Autowired
	private Logger logger;
	@Autowired
	@Qualifier("clientActivityReportDao")
	private ClientActivityReportDao dao;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
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
	void testGetClientHoldingsForClientWithHoldings() {
		String clientId = "541107416";
		List<Holding> holdings = dao.getClientHoldings(clientId);
		assertTrue(holdings.size() >= 1);
	}
	
	@Test
	void testGetClientHoldingsForClientWithNoHoldings() {
		String clientId = "1654658069";
		Exception e = assertThrows(DatabaseException.class, () -> {
			dao.getClientHoldings(clientId);
		});
		assertEquals(e.getMessage(), "client has no holdings");
	}
	
	@Test
	void testGetClientHoldingsThrowsExceptionForNonExistingClientID() {
		String clientId = "nonExistingClientId";
		Exception e = assertThrows(DatabaseException.class, () -> {
			dao.getClientHoldings(clientId);
		});
	}

}
