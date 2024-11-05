package com.marshals.restcontroller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marshals.business.ClientPreferences;
import com.marshals.business.Instrument;
import com.marshals.business.Order;
import com.marshals.business.Price;
import com.marshals.business.Trade;
import com.marshals.business.TradeHistory;
import com.marshals.business.services.FMTSService;
import com.marshals.business.services.TradeService;
import com.marshals.integration.DatabaseException;

@WebMvcTest(controllers = { TradeController.class })
class TradeControllerWebLayerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TradeService mockTradeService;

	@MockBean
	private FMTSService mockFMTSService;

//	Test client trade-history details
	List<Trade> tradeObjectList = List.of(
			new Trade(new Order("N123456", 20, new BigDecimal("1.03375"), "B", "541107416", "ORDER004", 4),
					new BigDecimal("1.035"), "TRADE004", new BigDecimal("20.7")),
			new Trade(new Order("T67894", 50, new BigDecimal("95.92"), "B", "541107416", "ORDER003", 3),
					new BigDecimal("96"), "TRADE003", new BigDecimal("4800")),
			new Trade(new Order("T67890", 10000, new BigDecimal("1.03375"), "S", "541107416", "ORDER002", 4),
					new BigDecimal("1.03375"), "TRADE002", new BigDecimal("10337.5")));

	TradeHistory clientTradeHistoryList = new TradeHistory("541107416", tradeObjectList);

	// Test Live Prices
	List<Price> priceList = new ArrayList<>(List.of(
			new Price(new BigDecimal("104.75"), new BigDecimal("104.25"), "21-AUG-19 10.00.01.042000000 AM GMT",
					new Instrument("N123456", "CUSIP", "46625H100", "STOCK", "JPMorgan Chase & Co. Capital Stock", 1000,
							1)),
			new Price(new BigDecimal("312500"), new BigDecimal("312000"), "21-AUG-19 05.00.00.040000000 AM -05:00",
					new Instrument("N123789", "ISIN", "US0846707026", "STOCK", "Berkshire Hathaway Inc. Class A", 10,
							1)),
			new Price(new BigDecimal("95.92"), new BigDecimal("95.42"), "21-AUG-19 10.00.02.042000000 AM GMT",
					new Instrument("C100", "CUSIP", "48123Y5A0", "CD",
							"JPMorgan Chase Bank, National Association 01/19", 1000, 100)),
			new Price(new BigDecimal("1.03375"), new BigDecimal("1.03390625"), "21-AUG-19 10.00.02.000000000 AM GMT",
					new Instrument("T67890", "CUSIP", "9128285M8", "GOVT", "USA, Note 3.125 15nov2028 10Y", 10000,
							100)),
			new Price(new BigDecimal("0.998125"), new BigDecimal("0.99828125"), "21-AUG-19 10.00.02.002000000 AM GMT",
					new Instrument("T67894", "CUSIP", "9128285Z9", "GOVT", "USA, Note 2.5 31jan2024 5Y", 10000, 100))));
	
	//Test Client Preferences - For Robo Advisor
	ClientPreferences testClientPreferences = new ClientPreferences("1654658069",  "Education", "HIG", "Short", "Tier4", 2, true);

	// Smoke Test for Mock MVC object
	@Test
	void testForMockMvcInstantiationForTradeController() {
		assertNotNull(mockMvc);
	}

//		Test for GET mapping for client trade-history
	@Test
	void testForGetClientTradeHistoryRespond200() throws Exception {
		String id = "541107416";
		when(mockTradeService.getClientTradeHistory(id)).thenReturn(clientTradeHistoryList);

		String expectedFirstTradeJson = """
					   	{
					"instrumentId": "N123456",
					"quantity": 20,
					"executionPrice": 1.035,
					"direction": "B",
					"clientId": "541107416",
					"order": {
						"instrumentId": "N123456",
						"quantity": 20,
						"targetPrice": 1.03375,
						"direction": "B",
						"clientId": "541107416",
						"orderId": "ORDER004",
						"token": 4
					},
					"tradeId": "TRADE004",
					"cashValue": 20.7
				}
					    """;

		mockMvc.perform(get("/trade/trade-history/" + id)).andExpect(status().isOk())
				.andExpect(jsonPath("$.clientId").value(id)).andExpect(jsonPath("$.trades").isArray())
				.andExpect(jsonPath("$.trades.length()").value(3)) // Change to the expected number of trades
				.andExpect(jsonPath("$.trades[0].instrumentId").value("N123456"))
				.andExpect(jsonPath("$.trades[1].executionPrice").value(96))
				.andExpect(jsonPath("$.trades[2].direction").value("S"))
				.andExpect(jsonPath("$.trades[2].cashValue").value(10337.5));
//				.andExpect(content().json(expectedFirstTradeJson));

	}

	@Test
	void testForGetClientTradeHistoryRespond204() throws Exception {
		String id = "12345678";

		when(mockTradeService.getClientTradeHistory(id)).thenThrow(DatabaseException.class);

		mockMvc.perform(get("/trade/trade-history/" + id)).andExpect(status().isBadRequest())
				.andExpect(content().string(is(emptyOrNullString())));
	}

	@Test
	void testForGetClientTradeHistoryRespond400ForRandomClientIdString() throws Exception {
		String id = "invalid-cliend-id";

		when(mockTradeService.getClientTradeHistory(id)).thenThrow(IllegalArgumentException.class);

		mockMvc.perform(get("/trade/trade-history/" + id)).andExpect(status().isNotAcceptable())
				.andExpect(content().string(is(emptyOrNullString())));
		;
	}

	@Test
	void testForGetClientTradeHistoryRespond406ForInvalidClientId() throws Exception {
		String id = "-10000";
		when(mockTradeService.getClientTradeHistory(id)).thenThrow(IllegalArgumentException.class);

		mockMvc.perform(get("/trade/trade-history/" + id)).andExpect(status().isNotAcceptable())
				.andExpect(content().string(is(emptyOrNullString())));
		;
	}

	@Test
	void testForGetClientTradeHistoryRespond500() throws Exception {
		String id = "541107416";
		when(mockTradeService.getClientTradeHistory(id)).thenThrow(RuntimeException.class);

		mockMvc.perform(get("/trade/trade-history/" + id)).andExpect(status().isInternalServerError())
				.andExpect(content().string(is(emptyOrNullString())));
		;
	}

	// Test for Execute Trade Success
	@Test
	void testExecuteTradeSuccess() throws Exception {
		// Arrange
		Order order = new Order("ORDER9876", 10, new BigDecimal("104.75"), "B", "541107416", "N123456", 540983960);
		Trade expectedTrade = new Trade(order, new BigDecimal("104.75"), // executionPrice
				"f5ffmsn2rep-etjqfvr7an4-2g1137sdpvf", // tradeId
				new BigDecimal("1057.975") // cashValue
		);

		when(mockTradeService.executeTrade(order)).thenReturn(expectedTrade);

		// Act & Assert
		mockMvc.perform(post("/trade/execute-trade").contentType(MediaType.APPLICATION_JSON).content("""
				    {
				        "orderId": "ORDER9876",
				        "quantity": 10,
				        "targetPrice": 104.75,
				        "direction": "B",
				        "clientId": "541107416",
				        "instrumentId": "N123456",
				        "token": 540983960
				    }
				""")).andExpect(status().isOk()).andDo(print());
	}

	// Test for GET Live prices
	@Test
	void testforSuccessfulRetrievalOfLivePrices_ReturnsWith200() throws Exception {
		when(mockFMTSService.getLivePrices()).thenReturn(priceList);
		mockMvc.perform(get("/trade/live-prices")).andExpect(status().isOk()).andDo(print())
				.andExpect(jsonPath("$.length()").value(5));
	}

	@Test
	void testforEmptyRetrievalOfLivePrices_ReturnsWith204() throws Exception {
		when(mockFMTSService.getLivePrices()).thenReturn(new ArrayList<>());
		mockMvc.perform(get("/trade/live-prices")).andExpect(status().isNoContent())
				.andExpect(content().string(is(emptyOrNullString())));
	}

	// Test for Robo Advisor
	@Test
	void testforSuccessfulRetrievalOfBuySuggestions_ReturnsWith200() throws Exception {
		when(mockTradeService.recommendTopBuyInstruments(testClientPreferences)).thenReturn(priceList);
		
		ObjectMapper mapper = new ObjectMapper();		
		String jsonString = mapper.writeValueAsString(testClientPreferences); 
		
		mockMvc.perform(post("/trade/suggest-buy")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonString))
				.andExpect(status().isOk());
	}
	@Test
	void testforEmptyRetrievalOfBuySuggestions_ReturnsWith204() throws Exception {
		when(mockTradeService.recommendTopBuyInstruments(testClientPreferences)).thenReturn(null);
		
		ObjectMapper mapper = new ObjectMapper();		
		String jsonString = mapper.writeValueAsString(testClientPreferences); 
		
		mockMvc.perform(post("/trade/suggest-buy")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonString))
				.andExpect(status().isNoContent())
				.andExpect(content().string(is(emptyOrNullString())));
	}
	@Test
	void testforSuccessfulRetrievalOfSellSuggestions_ReturnsWith200() throws Exception {
		when(mockTradeService.recommendTopSellInstruments(testClientPreferences)).thenReturn(priceList);
		
		ObjectMapper mapper = new ObjectMapper();		
		String jsonString = mapper.writeValueAsString(testClientPreferences); 
		
		mockMvc.perform(post("/trade/suggest-sell")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonString))
				.andExpect(status().isOk());
	}
	@Test
	void testforEmptyRetrievalOfSellSuggestions_ReturnsWith204() throws Exception {
		when(mockTradeService.recommendTopSellInstruments(testClientPreferences)).thenReturn(null);
		
		ObjectMapper mapper = new ObjectMapper();		
		String jsonString = mapper.writeValueAsString(testClientPreferences); 
		
		mockMvc.perform(post("/trade/suggest-sell")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonString))
				.andExpect(status().isNoContent())
				.andExpect(content().string(is(emptyOrNullString())));
	}

}