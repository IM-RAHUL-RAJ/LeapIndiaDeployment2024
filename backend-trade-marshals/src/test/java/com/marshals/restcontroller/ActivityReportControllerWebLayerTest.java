package com.marshals.restcontroller;

import com.marshals.business.Holding;
import com.marshals.business.Trade;
import com.marshals.business.TradeHistory;
import com.marshals.business.TradePL;
import com.marshals.business.services.ActivityReportService;
import com.marshals.integration.DatabaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ActivityReportControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ActivityReportController activityReportController;

    @MockBean
    private ActivityReportService activityReportService;

    @MockBean
    private Logger logger;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(activityReportController).build();
    }

    @Test
    void testGenerateHoldingsReport_Success() throws Exception {
        String clientId = "test-client";
        List<Holding> holdings = Collections.singletonList(new Holding());
        
        when(activityReportService.generateHoldingsReport(clientId)).thenReturn(holdings);
        
        ResponseEntity<?> response = activityReportController.generateHoldingsReport(clientId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(holdings, response.getBody());
    }

    @Test
    void testGenerateHoldingsReport_NoContent() throws Exception {
        String clientId = "test-client";
        
        when(activityReportService.generateHoldingsReport(clientId)).thenReturn(Collections.emptyList());
        assertThrows(ResponseStatusException.class, ()-> {
        	activityReportController.generateHoldingsReport(clientId);
        });
    }

    @Test
    void testGenerateHoldingsReport_DatabaseException() throws Exception {
        String clientId = "test-client";
        
        when(activityReportService.generateHoldingsReport(clientId)).thenThrow(new DatabaseException("Database error"));
        assertThrows(ResponseStatusException.class, ()-> {
        	activityReportController.generateHoldingsReport(clientId);
        });
    }

    @Test
    void testGenerateTradeReport_Success() throws Exception {
        String clientId = "test-client";
        TradeHistory tradeHistory = new TradeHistory();
        tradeHistory.setTrades(Collections.singletonList(new Trade()));
        
        when(activityReportService.generateTradeReport(clientId)).thenReturn(tradeHistory);
        
        ResponseEntity<?> response = activityReportController.generateTradeReport(clientId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tradeHistory, response.getBody());
    }

    @Test
    void testGenerateTradeReport_NoContent() throws Exception {
        String clientId = "test-client";
        TradeHistory tradeHistory = new TradeHistory();
        tradeHistory.setTrades(Collections.emptyList());
        
        when(activityReportService.generateTradeReport(clientId)).thenReturn(tradeHistory);
        
        assertThrows(ResponseStatusException.class, ()-> {
        	activityReportController.generateTradeReport(clientId);
        });
    }

    @Test
    void testGenerateTradeReport_DatabaseException() throws Exception {
        String clientId = "test-client";
        
        when(activityReportService.generateTradeReport(clientId)).thenThrow(new DatabaseException("Database error"));
        assertThrows(ResponseStatusException.class, ()-> {
        	activityReportController.generateTradeReport(clientId);
        });
    }

    @Test
    void testGeneratePLReport_Success() throws Exception {
        String clientId = "test-client";
        List<TradePL> profitLossMap = new ArrayList<TradePL>(List.of(
        			new TradePL("T67890", new BigDecimal("-10337.5"))
        		));
        
        when(activityReportService.generatePLReport(clientId)).thenReturn(profitLossMap);
        
        ResponseEntity<?> response = activityReportController.generatePLReport(clientId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(profitLossMap, response.getBody());
    }

    @Test
    void testGeneratePLReport_NoContent() throws Exception {
        String clientId = "test-client";
        
        when(activityReportService.generatePLReport(clientId)).thenReturn(new ArrayList<TradePL>());
       
        assertThrows(ResponseStatusException.class, ()-> {
        	activityReportController.generatePLReport(clientId);
        });
    }

    @Test
    void testGeneratePLReport_DatabaseException() throws Exception {
        String clientId = "test-client";
        
        when(activityReportService.generatePLReport(clientId)).thenThrow(new DatabaseException("Database error"));
        assertThrows(ResponseStatusException.class, ()-> {
        	activityReportController.generatePLReport(clientId);
        });
    }
}
