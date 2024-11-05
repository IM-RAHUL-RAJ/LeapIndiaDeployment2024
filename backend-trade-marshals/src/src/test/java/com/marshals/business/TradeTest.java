package com.marshals.business;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TradeTest {
	
	Trade trade;
	Order order;

	@BeforeEach
	void setUp() throws Exception {
		order = new Order("instrument1", 10, new BigDecimal("100.00"), "B", "client1", "order1", 123);
		trade = new Trade(order,new BigDecimal("100.00"),"trade1", new BigDecimal("1000.00"));
	}

	@AfterEach
	void tearDown() throws Exception {
	}

    @Test
    void testTradeEquals() {
        assertEquals(trade,new Trade(order,new BigDecimal("100.00"),"trade1", new BigDecimal("1000.00")));   
    }
    
    @Test
    void testTradeNotEquals() {
       assertNotEquals(trade,new Trade(order,new BigDecimal("100.00"),"t1", new BigDecimal("1000.00"))) ;
    }

 


    @Test
    void testExecutionPriceMustBePositive() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
        	new Trade(order,new BigDecimal("-2.00"),"trade1", new BigDecimal("1000.00")));
        assertEquals("executionPrice must be greater than 0", exception.getMessage());
    }


    @Test
    void testTradeIdCannotBeEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
        	new Trade(order,new BigDecimal("100.00"),"", new BigDecimal("1000.00")));
        assertEquals("tradeId cannot be null or empty", exception.getMessage());
    }

    @Test
    void testCashValueCannotBeNegative() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
    	new Trade(order,new BigDecimal("100.00"),"trade1", new BigDecimal("-1000.00")));
        assertEquals("cashValue cannot be null and must be non-negative", exception.getMessage());
    }
}
