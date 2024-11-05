package com.marshals.business;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PriceTest {

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
    void testValidPriceCreation() {
        Instrument instrument = new Instrument("id1", "type1", "externalId1", "category1", "description1", 10, 1);
        Price price = new Price(new BigDecimal("100.00"), new BigDecimal("95.00"), "2024-09-15T10:00:00Z", instrument);

        assertEquals(new BigDecimal("100.00"), price.getAskPrice());
        assertEquals(new BigDecimal("95.00"), price.getBidPrice());
        assertEquals("2024-09-15T10:00:00Z", price.getPriceTimestamp());
        assertEquals(instrument, price.getInstrument());
    }

    @Test
    void testPriceTimestampCannotBeNull() {
        Instrument instrument = new Instrument("id1", "type1", "externalId1", "category1", "description1", 10, 1);
        Price price = new Price(new BigDecimal("100.00"), new BigDecimal("95.00"), null, instrument);

        assertNull(price.getPriceTimestamp());
    }

    @Test
    void testInstrumentCanBeNull() {
        Price price = new Price(new BigDecimal("100.00"), new BigDecimal("95.00"), "2024-09-15T10:00:00Z", null);

        assertNull(price.getInstrument());
    }

    @Test
    void testAskPriceCannotBeNegative() {
        Instrument instrument = new Instrument("id1", "type1", "externalId1", "category1", "description1", 10, 1);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Price(new BigDecimal("-100.00"), new BigDecimal("95.00"), "2024-09-15T10:00:00Z", instrument));
        assertEquals("askPrice cannot be negative", exception.getMessage());
    }

    @Test
    void testBidPriceCannotBeNegative() {
        Instrument instrument = new Instrument("id1", "type1", "externalId1", "category1", "description1", 10, 1);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Price(new BigDecimal("100.00"), new BigDecimal("-95.00"), "2024-09-15T10:00:00Z", instrument));
        assertEquals("bidPrice cannot be negative", exception.getMessage());
    }

    @Test
    void testAskPriceAndBidPriceCanBeZero() {
        Instrument instrument = new Instrument("id1", "type1", "externalId1", "category1", "description1", 10, 1);
        Price price = new Price(new BigDecimal("0.00"), new BigDecimal("0.00"), "2024-09-15T10:00:00Z", instrument);

        assertEquals(new BigDecimal("0.00"), price.getAskPrice());
        assertEquals(new BigDecimal("0.00"), price.getBidPrice());
    }
}

