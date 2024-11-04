package com.marshals.restcontroller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTable;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTableWhere;
import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import com.marshals.business.ClientPortfolio;
import com.marshals.business.Holding;

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@Sql(scripts={"classpath:schema.sql", "classpath:data.sql"}, // SQL files are in src/main/resources
     executionPhase=Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PortfolioControllerE2ETest {
	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	List<Holding> holdingsOf541107416 = new ArrayList<Holding>(
			List.of(new Holding("C100", 1000, new BigDecimal("95.67")),
					new Holding("T67890", 10, new BigDecimal("1.0338"))));
	@Test
	public void testQueryForPortfolioById() {
		ClientPortfolio expectedPortfolio = new ClientPortfolio("541107416", new BigDecimal("10000"), holdingsOf541107416);
		String requestUrl = "/portfolio/client/541107416";

		ResponseEntity<ClientPortfolio> response = 
			restTemplate.getForEntity(requestUrl, ClientPortfolio.class);
	
		assertEquals(HttpStatus.OK, response.getStatusCode());
		
		ClientPortfolio responseBody = response.getBody();
	}
	
	@Test
	public void testQueryForPortfolioById_NotPresent() {
		String requestUrl = "/portfolio/client/541107419";

		ResponseEntity<ClientPortfolio> response = 
			restTemplate.getForEntity(requestUrl, ClientPortfolio.class);
				
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR ,response.getStatusCode());	
		}
	
	@Test
	public void testQueryForPortfolioById_NopShipInDb() {
		// delete all rows from the president table
		deleteFromTables(jdbcTemplate, "holdings");
		
		String requestUrl = "/ship";

		ResponseEntity<ClientPortfolio> response = 
			restTemplate.getForEntity(requestUrl, ClientPortfolio.class);
		
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
	
}
