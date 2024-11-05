package com.marshals.business;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		//fail("Not yet implemented");
	}


    @Test
    void testValidOrderCreation() {
        Order order = new Order("instrument1", 10, new BigDecimal("100.00"), "B", "client1", "order1", 123);
        assertEquals(order,new Order("instrument1", 10, new BigDecimal("100.00"), "B", "client1", "order1", 123));
    }

    @Test
    void testInstrumentIdCannotBeNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Order(null, 10, new BigDecimal("100.00"), "B", "client1", "order1", 123));
        assertEquals("instrumentId cannot be null or empty", exception.getMessage());
    }

    @Test
    void testQuantityMustBePositive() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Order("instrument1", 0, new BigDecimal("100.00"), "B", "client1", "order1", 123));
        assertEquals("quantity must be greater than 0", exception.getMessage());
    }

    @Test
    void testTargetPriceMustBePositive() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Order("instrument1", 10, new BigDecimal("-1.00"), "B", "client1", "order1", 123));
        assertEquals("targetPrice must be greater than 0", exception.getMessage());
    }

    @Test
    void testDirectionCannotBeNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Order("instrument1", 10, new BigDecimal("100.00"), null, "client1", "order1", 123));
        assertEquals("direction cannot be null or empty", exception.getMessage());
    }

    @Test
    void testClientIdCannotBeEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Order("instrument1", 10, new BigDecimal("100.00"), "B", "", "order1", 123));
        assertEquals("clientId cannot be null or empty", exception.getMessage());
    }

    @Test
    void testOrderIdCannotBeEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Order("instrument1", 10, new BigDecimal("100.00"), "B", "client1", "", 123));
        assertEquals("orderId cannot be null or empty", exception.getMessage());
    }
}
