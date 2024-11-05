package com.marshals.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.marshals.business.ClientPreferences;

class PriceScorerTest {
	
	PriceScorer scorer;
	
	ClientPreferences prefs = new ClientPreferences(
	        "1",
	        "Retirement",
	        "VHIG",
	        "Long",
	        "Tier3",
	         2, 
	         true
	 );

	@BeforeEach
	void setUp() throws Exception {
		scorer = new PriceScorer(prefs);
	}

	@AfterEach
	void tearDown() throws Exception {
		scorer = null;
	}

	@Test
    void testPriceScorerToCalculateScorer() {
    	assertEquals(scorer.calculateScore(), 15);
    }

}
