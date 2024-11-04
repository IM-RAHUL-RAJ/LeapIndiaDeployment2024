package com.marshals.restcontroller;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marshals.business.ClientPortfolio;
import com.marshals.business.Holding;
import com.marshals.business.Order;
import com.marshals.business.Trade;
import com.marshals.business.services.PortfolioService;


@WebMvcTest(controllers = {PortfolioController.class})
public class PortfolioControllerWebLayerTest {

	@Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortfolioService mockService;
    
    @MockBean
    private Logger mockLogger;
    
    List<Holding> holdingsOf1654658069 = new ArrayList<Holding>(
			List.of(new Holding("Q123", 2, new BigDecimal("105")), new Holding("Q456", 1, new BigDecimal("340"))));
	List<Holding> holdingsOf541107416 = new ArrayList<Holding>(
			List.of(new Holding("C100", 10000, new BigDecimal("95.67")),
					new Holding("T67890", 10, new BigDecimal("1.033828125"))));
	// Test client portfolios
	private List<ClientPortfolio> clientPortfolios = new ArrayList<ClientPortfolio>(
			List.of(new ClientPortfolio("1654658069", new BigDecimal("10000"), holdingsOf1654658069),
					new ClientPortfolio("541107416", new BigDecimal("20000"), holdingsOf541107416)));

        @Test
        public void testQueryForPortfolioById() throws Exception {
            String id = "541107416";
            ClientPortfolio firstPortfolio = new ClientPortfolio("541107416", new BigDecimal("20000"), holdingsOf541107416);

            
            when(mockService.getClientPortfolio(id))
                .thenReturn(firstPortfolio);
           
            mockMvc.perform(get("/portfolio/client/541107416"))
                   .andDo(print())
                   .andExpect(status().isOk());
        }

        @Test
        void testGetPortfolioById_RuntimeException() {
            String clientId = "541107486";

            ResponseStatusException exception = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
            when(mockService.getClientPortfolio(clientId)).thenThrow(exception);

            ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> {
            	mockService.getClientPortfolio(clientId);
            });

            assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
            assertEquals("Bad request", thrown.getReason());
          }
        
        @Test
        void testGetPortfolioById_NotFound() throws Exception {
            String clientId = "541107417";

            // Mocking the service to return null, simulating a not found scenario
            when(mockService.getClientPortfolio(clientId)).thenReturn(null);
       
	        mockMvc.perform(get("/portfolio/client/541107417"))
	               .andDo(print())
	               .andExpect(status().isNotFound());
           
        }
       
}
