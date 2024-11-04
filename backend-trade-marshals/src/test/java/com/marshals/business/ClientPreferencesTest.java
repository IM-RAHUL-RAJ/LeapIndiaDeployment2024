package com.marshals.business;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientPreferencesTest {
	
	private ClientPreferences clientPreferences;

	@BeforeEach
	void setUp() throws Exception {

	}

	@AfterEach
	void tearDown() throws Exception {
		clientPreferences = null;
	}

//	@Test
//	void shouldCreateClientPreferenceObject() {
//		assertNotNull(new ClientPreferences("1425922638", "Education", "MIG", "Short", "Tier1", 5, true));
//	}
	
	@Test
	void shouldNotCreateInvalidClientPreferenceObject() {
		assertThrows(IllegalArgumentException.class, () -> {
			new ClientPreferences("1425922638", "Education", "MIG", "Short", "Tier1", 10, true);
		});
	}
	
	@Test
	void testForEqualsClientPreferencesObject() {
		clientPreferences = new ClientPreferences("1425922638", "Education", "MIG", "Short", "Tier1", 5, true);
		assertTrue(clientPreferences.equals(new ClientPreferences("1425922638", "Education", "MIG", "Short", "Tier1", 5, true)));
	}
	
	@Test
	void testForNotEqualsClientPreferencesObject() {
		clientPreferences = new ClientPreferences("1425922638", "Education", "MIG", "Short", "Tier1", 5, true);
		assertFalse(clientPreferences.equals(new ClientPreferences("1425922638", "Education", "LIG", "Short", "Tier3", 2, true)));
	}
	
	@Test
	void testForHashCodeClientPreferencesObject() {
		clientPreferences = new ClientPreferences("1425922638", "Education", "MIG", "Short", "Tier1", 5, true);
		ClientPreferences expected = new ClientPreferences("1425922638", "Education", "MIG", "Short", "Tier1", 5, true);
		assertEquals(clientPreferences.hashCode(), expected.hashCode());
	}

}
