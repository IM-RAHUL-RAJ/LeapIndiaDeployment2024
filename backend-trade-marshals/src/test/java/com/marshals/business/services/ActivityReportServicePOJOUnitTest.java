package com.marshals.business.services;
 
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.marshals.business.Holding;
import com.marshals.business.Order;
import com.marshals.business.Trade;
import com.marshals.business.TradeHistory;
import com.marshals.business.TradePL;
import com.marshals.integration.ClientActivityReportDao;
import com.marshals.integration.DatabaseException;
 
class ActivityReportServicePOJOUnitTest {
	
	@Mock ClientActivityReportDao mockDao;
	@Mock TradeService mockTradeService;
	
	@Autowired
	@InjectMocks
	ActivityReportService service;

	Holding holding1 = new Holding("C100", 1000, new BigDecimal("95.67"));
	Holding holding2 = new Holding("T67890", 10, new BigDecimal("1.0338"));
	List<Holding> mockHoldings = new ArrayList<Holding>(
				List.of(
					holding1,
					holding2
				)
			);


	String clientId = "541107416";
    List<Trade> mockTradeList = new ArrayList<>();
    Order order1 = new Order("instrument1", 10, new BigDecimal("100.00"), "B", clientId, "ORDER001", 123);
	Order order2 = new Order("instrument2", 15, new BigDecimal("120.00"), "B", clientId, "ORDER002", 123);
	Order order3 = new Order("instrument3", 5, new BigDecimal("120.00"), "S", clientId, "ORDER003", 123);
    Trade trade1 = new Trade(order1,new BigDecimal("100.00"),"TRADE001", new BigDecimal("1000.00"));
	Trade trade2 = new Trade(order2,new BigDecimal("110.00"),"TRADE002", new BigDecimal("1000.00"));
	Trade trade3 = new Trade(order3,new BigDecimal("110.00"),"TRADE003", new BigDecimal("5000.00"));
	
	TradeHistory mockTradeHistory = new TradeHistory(clientId, mockTradeList);
 
	@BeforeEach
	void setUp() throws Exception {
//		 mockDao = mock(ClientActivityReportDao.class);
//	     mockTradeService = mock(TradeHistoryService.class);
//	     service = new ActivityReportService(mockDao,mockTradeService);
		
		
		 MockitoAnnotations.openMocks(this);
	     mockTradeList.add(trade1);
	 	 mockTradeList.add(trade2);
	 	 mockTradeList.add(trade3);
	}
 
	@AfterEach
	void tearDown() throws Exception {
		mockDao = null;
		mockTradeService = null;
		service = null;
		mockTradeList = null;
		mockTradeHistory = null;
	}
	
	@Test
	void testGenerateClientHoldingsForValidClient() {
		String clientId = "541107416";
		Mockito.when(mockDao.getClientHoldings(clientId))
			.thenReturn(mockHoldings);
		service.generateHoldingsReport(clientId);
		Mockito.verify(mockDao).getClientHoldings(clientId);
	}
	
	@Test
	void testGenerateClientHoldingsShouldHandleNullClientId() {
		assertThrows(NullPointerException.class, () -> {
			service.generateHoldingsReport(null);
		});	}
 
	@Test
	void testGenerateClientHoldingShouldHandleForNonExistentClientId() {
		String clientId = "541107416";
		Mockito.doThrow(new DatabaseException("Client has no holdings")).when(mockDao).getClientHoldings(clientId);
		assertThrows(NullPointerException.class, () -> {
			service.generateHoldingsReport(clientId);
			//Verifying that the corresponding mockDao methods were called
			Mockito.verify(mockDao).getClientHoldings(clientId);
		});
	}
	
	@Test
	void testGenerateTradeReportForValidClient() {
		String clientId = "541107416";
		Mockito.when(mockTradeService.getClientTradeHistory(clientId))
			.thenReturn(mockTradeHistory);
		TradeHistory expected = service.generateTradeReport(clientId);
		Mockito.verify(mockTradeService).getClientTradeHistory(clientId);
		assertEquals(expected.getClientId(), clientId);
	}
	
	@Test
	void testGenerateTradeReportForNullClientId() {
		Exception e = assertThrows(NullPointerException.class, () -> {
			service.generateTradeReport(null);
		});
		assertEquals(e.getMessage(),"Client Id should not be null for Trade History");
	}
	
	@Test
	void testGenerateTradeReportShouldHandleForNonExistentClientId() {
		String clientId = "541107400";
		Mockito.doThrow(new DatabaseException("Client has no Trade Report")).when(mockTradeService).getClientTradeHistory(clientId);
		Exception e = assertThrows(DatabaseException.class, () -> {
			service.generateTradeReport(clientId);
			//Verifying that the corresponding mockDao methods were called
			Mockito.verify(mockTradeService).getClientTradeHistory(clientId);
		});
		assertEquals(e.getMessage(), "Client has no Trade Report");
	}
	
	@Test
	void testGeneratePLReportForValidClient() {
		String clientId = "541107416";
		Mockito.when(mockTradeService.getClientTradeHistory(clientId))
		.thenReturn(mockTradeHistory);
		List<TradePL> expected = service.generatePLReport(clientId);
		Mockito.verify(mockTradeService).getClientTradeHistory(clientId);
		assertNotNull(expected);
	}
	
	@Test
	void testGeneratePLReportForNullClient() {
		Exception e = assertThrows(NullPointerException.class, () -> {
			service.generatePLReport(null);
		});
		assertEquals(e.getMessage(),"Client Id should not be null to calculate Profit Loss");
	}
	
	@Test
	void testGeneratePLReportForInvalidClient() {
		String clientId = "541107400";
		Mockito.doThrow(new DatabaseException("Client has no Trade History")).when(mockTradeService).getClientTradeHistory(clientId);
		Exception e = assertThrows(DatabaseException.class, () -> {
			service.generatePLReport(clientId);
			//Verifying that the corresponding mockDao methods were called
			Mockito.verify(mockTradeService).getClientTradeHistory(clientId);
		});
		assertEquals(e.getMessage(), "Client has no Trade History");
	}
}