package com.marshals.business;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FMTSValidatedClientTest {
	
	private FMTSValidatedClient validatedClient;

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
		validatedClient = null;
	}

	@Test
	void testForEqualsValidatedClientObject() {
		validatedClient = new FMTSValidatedClient("john.doe@gmail.com","1234567890",207156590);
		assertEquals(validatedClient, new FMTSValidatedClient("john.doe@gmail.com","1234567890",207156590) , 
				"Validated Client details should be equal");
	}
	
	@Test
	void testForNotEqualsOfValidatedClientObjectWithDiffTokens() {
		validatedClient = new FMTSValidatedClient("john.doe@gmail.com","1234567890",207156590);
		assertNotEquals(validatedClient, new FMTSValidatedClient("john.doe@gmail.com","1234567890",123456789) , 
				"Validated Client details should not be equal");
	}
	
	//NullPointerException
	@Test
	void testNullClientIDInitialization() {
		Exception e = assertThrows(NullPointerException.class, () -> {
			validatedClient = new FMTSValidatedClient("john.doe@gmail.com",null,207156590);
		});
		assertEquals("Validated Client Details cannot be null",e.getMessage());
	}
	

}
