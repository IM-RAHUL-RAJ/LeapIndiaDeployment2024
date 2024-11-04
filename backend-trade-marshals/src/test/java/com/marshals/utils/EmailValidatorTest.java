package com.marshals.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EmailValidatorTest {

	@Test
	void testValidEmail() {
		String validEmail = "sample_user@gmail.com";
		assertTrue(EmailValidator.isValidEmail(validEmail));
	}
	
	@Test
	void testinvalidEmail() {
		String invalidEmail = "invalid-email";
		assertFalse(EmailValidator.isValidEmail(invalidEmail));
	}

}
