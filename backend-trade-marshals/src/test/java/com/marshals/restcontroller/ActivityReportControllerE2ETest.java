package com.marshals.restcontroller;

import com.marshals.business.Holding;
import com.marshals.business.TradeHistory;
import com.marshals.business.TradePL;

import org.junit.jupiter.api.Test;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"},
     executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class ActivityReportControllerE2ETest {

    @Autowired
    private TestRestTemplate restTemplate; // for executing rest endpoints

    @Autowired
    private JdbcTemplate jdbcTemplate;  // for executing SQL queries    

    @Test
    public void testRestTemplateToBeInstantiated() {
        assertNotNull(restTemplate);
    }

    /* Tests for Holdings Report */
    @Test
    public void testGenerateHoldingsReport_ShouldReturnHoldings() {
        String clientId = "541107416"; 
        ResponseEntity<List> response = 
            restTemplate.getForEntity("/activity-report/holdings/" + clientId, List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode()); // verify the response HTTP status is OK
        List<?> holdings = response.getBody();
        assertNotNull(holdings);
        assertTrue(holdings.size()>=1);
    }

    @Test
    public void testGenerateHoldingsReport_WithInvalidClientId_ShouldReturnBadRequest() {
        assertThrows(RestClientException.class, () -> {
        	restTemplate.getForEntity("/activity-report/holdings/invalidClientId", List.class);
        });
    }
    
//    @Test
//    public void testGenerateHoldingsReport_WithEmptyHoldingArray_ShouldReturntBadRequest() {
//    	String clientId = "541107416"; 
//    	deleteFromTables(jdbcTemplate, "holdings");
//    	ResponseEntity<List> response = 
//            restTemplate.getForEntity("/activity-report/holdings/" + clientId, List.class);
//    	System.out.println(response.getBody());
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }
    
    @Test
    public void testGenerateHoldingsReport_WithClientIdNull_ShouldReturnBadRequest() {
    	String clientId = null; 
    	assertThrows(RestClientException.class, () -> {
    		restTemplate.getForEntity("/activity-report/holdings/" + clientId, List.class);
        });
    }

    /* Tests for Trade Report */
    @Test
    public void testGenerateTradeReport_ShouldReturnTradeHistory() {
        String clientId = "541107416";
        ResponseEntity<TradeHistory> response = 
            restTemplate.getForEntity("/activity-report/trades/" + clientId, TradeHistory.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TradeHistory tradeHistory = response.getBody();
        assertNotNull(tradeHistory);
        assertEquals(tradeHistory.getClientId(), "541107416");
        assertTrue(tradeHistory.getTrades().size() >= 1);
    }

    @Test
    public void testGenerateTradeReport_WithInvalidClientId_ShouldReturnBadRequest() {
        ResponseEntity<TradeHistory> response = 
            restTemplate.getForEntity("/activity-report/trades/invalidClientId", TradeHistory.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void testGenerateTradeReport_WithNullClientId_ShouldReturnBadRequest() {
        String clientId = null;
    	ResponseEntity<TradeHistory> response = 
            restTemplate.getForEntity("/activity-report/trades/" + clientId, TradeHistory.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /* Tests for P&L Report */
    @Test
    public void testGeneratePLReport_ShouldReturnProfitLossMap() {
        String clientId = "541107416";
        ResponseEntity<TradePL[]> response = 
            restTemplate.getForEntity("/activity-report/pl/" + clientId, TradePL[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TradePL[] profitLossMap = response.getBody();
        assertNotNull(profitLossMap);
    }

    @Test
    public void testGeneratePLReport_WithInvalidClientId_ShouldReturnBadRequest() {
        ResponseEntity<Map> response = 
            restTemplate.getForEntity("/activity-report/pl/invalidClientId", Map.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void testGeneratePLReport_WithNullClientId_ShouldReturnBadRequest() {
        String clientId = null;
    	ResponseEntity<Map> response = 
            restTemplate.getForEntity("/activity-report/pl/" + clientId, Map.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
