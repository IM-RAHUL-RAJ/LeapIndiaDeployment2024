package com.marshals.business.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.marshals.business.Holding;
import com.marshals.integration.DatabaseException;

@SpringBootTest
@Transactional
class ActivityReportServiceIntegrationTest {
	
	@Autowired
	private Logger logger;
	
	@Autowired
	private ActivityReportService service;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void testGetClientHoldingsForClientWithHoldings() {
		String clientId = "541107416";
		List<Holding> holdings = service.generateHoldingsReport(clientId);
		assertTrue(holdings.size() >= 1);
	}

	@Test
	void testGetClientHoldingsForClientWithNoHoldings() {
		String clientId = "1654658069";
		Exception e = assertThrows(DatabaseException.class, () -> {
			service.generateHoldingsReport(clientId);
		});
		assertEquals(e.getMessage(), "client has no holdings");
	}

	@Test
	void testGetClientHoldingsThrowsExceptionForNonExistingClientID() {
		String clientId = "nonExistingClientId";
		Exception e = assertThrows(DatabaseException.class, () -> {
			service.generateHoldingsReport(clientId);
		});
	}

	@Test
	void testGetClientHoldingsThrowsNullPointerExceptionForNullClientId() {
		String clientId = null;
		Exception e = assertThrows(DatabaseException.class, () -> {
			service.generateHoldingsReport(clientId);
		});
	}
}
