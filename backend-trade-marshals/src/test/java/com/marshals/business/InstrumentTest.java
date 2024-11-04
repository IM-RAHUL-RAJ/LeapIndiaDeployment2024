package com.marshals.business;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InstrumentTest {

    @Test
    void testValidInstrumentCreation() {
        Instrument instrument = new Instrument("id1", "type1", "externalId1", "category1", "description1", 10, 1);

        assertEquals("id1", instrument.getInstrumentId());
        assertEquals("type1", instrument.getExternalIdType());
        assertEquals("externalId1", instrument.getExternalId());
        assertEquals("category1", instrument.getCategoryId());
        assertEquals("description1", instrument.getInstrumentDescription());
        assertEquals(10, instrument.getMaxQuantity());
        assertEquals(1, instrument.getMinQuantity());
    }

    @Test
    void testInstrumentIdCannotBeNull() {
        Exception exception = assertThrows(NullPointerException.class, () ->
                new Instrument(null, "type", "externalId", "category", "description", 10, 1));
        assertEquals("instrumentId cannot be null", exception.getMessage());
    }

    @Test
    void testInstrumentIdCannotBeEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Instrument("", "type", "externalId", "category", "description", 10, 1));
        assertEquals("instrumentId cannot be empty", exception.getMessage());
    }

    @Test
    void testExternalIdTypeCannotBeNull() {
        Exception exception = assertThrows(NullPointerException.class, () ->
                new Instrument("id", null, "externalId", "category", "description", 10, 1));
        assertEquals("externalIdType cannot be null", exception.getMessage());
    }

    @Test
    void testExternalIdTypeCannotBeEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Instrument("id", "", "externalId", "category", "description", 10, 1));
        assertEquals("externalIdType cannot be empty", exception.getMessage());
    }

    @Test
    void testExternalIdCannotBeNull() {
        Exception exception = assertThrows(NullPointerException.class, () ->
                new Instrument("id", "type", null, "category", "description", 10, 1));
        assertEquals("externalId cannot be null", exception.getMessage());
    }

    @Test
    void testExternalIdCannotBeEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Instrument("id", "type", "", "category", "description", 10, 1));
        assertEquals("externalId cannot be empty", exception.getMessage());
    }

    @Test
    void testCategoryIdCannotBeNull() {
        Exception exception = assertThrows(NullPointerException.class, () ->
                new Instrument("id", "type", "externalId", null, "description", 10, 1));
        assertEquals("categoryId cannot be null", exception.getMessage());
    }

    @Test
    void testCategoryIdCannotBeEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Instrument("id", "type", "externalId", "", "description", 10, 1));
        assertEquals("categoryId cannot be empty", exception.getMessage());
    }

    @Test
    void testInstrumentDescriptionCannotBeNull() {
        Exception exception = assertThrows(NullPointerException.class, () ->
                new Instrument("id", "type", "externalId", "category", null, 10, 1));
        assertEquals("instrumentDescription cannot be null", exception.getMessage());
    }

    @Test
    void testInstrumentDescriptionCannotBeEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Instrument("id", "type", "externalId", "category", "", 10, 1));
        assertEquals("instrumentDescription cannot be empty", exception.getMessage());
    }

    @Test
    void testMaxQuantityCannotBeNull() {
        Exception exception = assertThrows(NullPointerException.class, () ->
                new Instrument("id", "type", "externalId", "category", "description", null, 1));
        assertEquals("maxQuantity cannot be null", exception.getMessage());
    }

    @Test
    void testMaxQuantityCannotBeNegative() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Instrument("id", "type", "externalId", "category", "description", -1, 1));
        assertEquals("maxQuantity cannot be negative", exception.getMessage());
    }

    @Test
    void testMinQuantityCannotBeNull() {
        Exception exception = assertThrows(NullPointerException.class, () ->
                new Instrument("id", "type", "externalId", "category", "description", 10, null));
        assertEquals("minQuantity cannot be null", exception.getMessage());
    }

    @Test
    void testMinQuantityCannotBeNegative() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Instrument("id", "type", "externalId", "category", "description", 10, -1));
        assertEquals("minQuantity cannot be negative", exception.getMessage());
    }

}

