package com.marshals.restcontroller;


import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marshals.business.Client;
import com.marshals.business.ClientIdentification;
import com.marshals.business.ClientPreferences;
import com.marshals.business.services.ClientPreferencesService;
import com.marshals.integration.DatabaseException;

@WebMvcTest(controllers = {ClientPreferencesController.class})
class ClientPreferencesControllerWebLayerTest {
	
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private ClientPreferencesService mockClientPreferencesService;
	
	@MockBean
	private Logger mockLogger;
	
	//Test Client details
	List<Client> clientList = new ArrayList<Client>(List.of(
		 new Client("sowmya@gmail.com","1654658069", "Marsh2024", "Sowmya", "11/12/2002", "India", 
					new ArrayList<>(List.of(new ClientIdentification("Aadhar","123456789102"))), true), //Existing client
		 new Client("nonexistent.client@gmail.com","1654658000", "Password1234", "Non Existent client", "12/10/1994", "USA", 
					new ArrayList<>(List.of(new ClientIdentification("SSN","1953826343"))), false), //Non Existent client
		 new Client("sam@gmail.com","767836496", "Password1234", "Sam", "12/11/2000", "USA", 
					new ArrayList<>(List.of(new ClientIdentification("SSN","1643846323"))), false) //New client to be inserted
		));
	
	//Test Client preferences details
	List<ClientPreferences> clientPreferencesList = new ArrayList<ClientPreferences>(
			List.of(
					new ClientPreferences("1654658069",  "Education", "HIG", "Short", "Tier4", 2, false), //Existing client
					new ClientPreferences("1654658000", "Major Expense", "MIG", "Medium", "Tier1", 5, true), //Non existent client
					new ClientPreferences("767836496", "Retirement", "MIG", "Long", "Tier3", 3, true) //New client to be inserted
			)
		);

//	Smoke Test for Mock MVC object
	@Test
	void testForMockMvcInstantiationForClientPreference() {
		assertNotNull(mockMvc);
	}

//	Test for GET Mapping for client-preferences
	@Test
	void testForGetClientPreferencesRespond200() throws Exception {
		String id = "1654658069";
		ClientPreferences clientPreference = clientPreferencesList.get(0);
		when(mockClientPreferencesService.getClientPreferences(id)).thenReturn(clientPreference);
		
		mockMvc.perform(get("/client-preferences/" + id))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.clientId").value(id))
			.andExpect(jsonPath("$.investmentPurpose").value(clientPreference.getInvestmentPurpose()))
			.andExpect(jsonPath("$.incomeCategory").value(clientPreference.getIncomeCategory()))
			.andExpect(jsonPath("$.lengthOfInvestment").value(clientPreference.getLengthOfInvestment()))
			.andExpect(jsonPath("$.percentageOfSpend").value(clientPreference.getPercentageOfSpend()))
			.andExpect(jsonPath("$.riskTolerance").value(clientPreference.getRiskTolerance()))
			.andExpect(jsonPath("$.acceptAdvisor").value(clientPreference.getAcceptAdvisor()));
	}
	
	@Test
	void testForGetClientPreferencesRespond204() throws Exception {
		String id = "12345678";
		when(mockClientPreferencesService.getClientPreferences(id)).thenThrow(DatabaseException.class);
		
		mockMvc.perform(get("/client-preferences/" + id))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(is(emptyOrNullString())));;
	}
	
	@Test
	void testForGetClientPreferencesRespond400ForRandomString() throws Exception {
		String id = "invalid-string";
		when(mockClientPreferencesService.getClientPreferences(id)).thenThrow(IllegalArgumentException.class);
		
		mockMvc.perform(get("/client-preferences/" + id))
			.andExpect(status().isNotAcceptable())
			.andExpect(content().string(is(emptyOrNullString())));;
	}
	
	@Test
	void testForGetClientPreferencesRespond406ForInvalidClientId() throws Exception {
		String id = "-10000";
		when(mockClientPreferencesService.getClientPreferences(id)).thenThrow(IllegalArgumentException.class);
		
		mockMvc.perform(get("/client-preferences/" + id))
			.andExpect(status().isNotAcceptable())
			.andExpect(content().string(is(emptyOrNullString())));;
	}

	@Test
	void testForGetClientPreferencesRespond500() throws Exception {
		String id = "1234567";
		when(mockClientPreferencesService.getClientPreferences(id)).thenThrow(RuntimeException.class);
		
		mockMvc.perform(get("/client-preferences/" + id))
			.andExpect(status().isInternalServerError())
			.andExpect(content().string(is(emptyOrNullString())));;
	}
	
// 	Test for PUT Mapping for client-preferences
	@Test
	void testForAddClientPreferencesRespond202() throws Exception {
		ClientPreferences clientPreference = clientPreferencesList.get(0);
		when(mockClientPreferencesService.addClientPreferences(clientPreference)).thenReturn(true);
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(clientPreference);
		
		mockMvc.perform(post("/client-preferences")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonString) )
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.clientId").value(clientPreference.getClientId()))
			.andExpect(jsonPath("$.investmentPurpose").value(clientPreference.getInvestmentPurpose()))
			.andExpect(jsonPath("$.incomeCategory").value(clientPreference.getIncomeCategory()))
			.andExpect(jsonPath("$.lengthOfInvestment").value(clientPreference.getLengthOfInvestment()))
			.andExpect(jsonPath("$.percentageOfSpend").value(clientPreference.getPercentageOfSpend()))
			.andExpect(jsonPath("$.riskTolerance").value(clientPreference.getRiskTolerance()))
			.andExpect(jsonPath("$.acceptAdvisor").value(clientPreference.getAcceptAdvisor()));
	}
	
	@Test
	void testForAddClientPreferencesRespond400ForNullPreferenceObject() throws Exception {
		mockMvc.perform(post("/client-preferences")
						.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(is(emptyOrNullString())));
	}
	
	@Test
	void testForAddClientPreferencesRespond400ForExistingClientPreferences() throws Exception {
		ClientPreferences existingClientPreferences = clientPreferencesList.get(0);
		when(mockClientPreferencesService.addClientPreferences(existingClientPreferences)).thenThrow(DatabaseException.class);
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(existingClientPreferences);
		
		mockMvc.perform(post("/client-preferences/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonString))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(is(emptyOrNullString())));
	}
	
	@Test
	void testForAddClientPreferencesRespond400ForNonExistingClientPreferences() throws Exception {
		ClientPreferences existingClientPreferences = clientPreferencesList.get(2);
		when(mockClientPreferencesService.addClientPreferences(existingClientPreferences)).thenThrow(DatabaseException.class);
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(existingClientPreferences);
		
		mockMvc.perform(post("/client-preferences/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonString))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(is(emptyOrNullString())));
	}
	
	@Test
	void testForAddClientPreferences500() throws Exception {
		ClientPreferences existingClientPreferences = clientPreferencesList.get(2);
		when(mockClientPreferencesService.addClientPreferences(existingClientPreferences)).thenThrow(RuntimeException.class);
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(existingClientPreferences);
		
		mockMvc.perform(post("/client-preferences/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonString))
			.andExpect(content().string(is(emptyOrNullString())))
			.andExpect(status().isInternalServerError());
	}
	
// 	Test for PUT Mapping for client-preferences
	@Test
	void testForUpdateClientPreferencesRespond202() throws Exception {
		ClientPreferences clientPreference = clientPreferencesList.get(0);
		when(mockClientPreferencesService.updateClientPreferences(clientPreference)).thenReturn(true);
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(clientPreference);
		
		mockMvc.perform(put("/client-preferences")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonString) )
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.clientId").value(clientPreference.getClientId()))
			.andExpect(jsonPath("$.investmentPurpose").value(clientPreference.getInvestmentPurpose()))
			.andExpect(jsonPath("$.incomeCategory").value(clientPreference.getIncomeCategory()))
			.andExpect(jsonPath("$.lengthOfInvestment").value(clientPreference.getLengthOfInvestment()))
			.andExpect(jsonPath("$.percentageOfSpend").value(clientPreference.getPercentageOfSpend()))
			.andExpect(jsonPath("$.riskTolerance").value(clientPreference.getRiskTolerance()))
			.andExpect(jsonPath("$.acceptAdvisor").value(clientPreference.getAcceptAdvisor()));
	}
	
	@Test
	void testForUpdateClientPreferencesRespond400ForNullPreferenceObject() throws Exception {
		mockMvc.perform(post("/client-preferences")
						.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(is(emptyOrNullString())));
	}
	
	@Test
	void testForUpdateClientPreferencesRespond400ForExistingClientPreferences() throws Exception {
		ClientPreferences existingClientPreferences = clientPreferencesList.get(0);
		when(mockClientPreferencesService.updateClientPreferences(existingClientPreferences)).thenThrow(DatabaseException.class);
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(existingClientPreferences);
		
		mockMvc.perform(put("/client-preferences/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonString))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(is(emptyOrNullString())));;
			
	}
	
	@Test
	void testForUpdateClientPreferencesRespond400ForNonExistingClientPreferences() throws Exception {
		ClientPreferences existingClientPreferences = clientPreferencesList.get(2);
		when(mockClientPreferencesService.updateClientPreferences(existingClientPreferences)).thenThrow(DatabaseException.class);
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(existingClientPreferences);
		
		mockMvc.perform(put("/client-preferences/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonString))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(is(emptyOrNullString())));;
	}
	
	@Test
	void testForUpdateClientPreferences500() throws Exception {
		ClientPreferences existingClientPreferences = clientPreferencesList.get(2);
		when(mockClientPreferencesService.updateClientPreferences(existingClientPreferences)).thenThrow(RuntimeException.class);
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(existingClientPreferences);
		
		mockMvc.perform(put("/client-preferences/")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonString))
			.andExpect(content().string(is(emptyOrNullString())))
			.andExpect(status().isInternalServerError());
	}
}
