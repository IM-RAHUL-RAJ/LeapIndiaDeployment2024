package com.marshals.integration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
 
import com.marshals.business.FMTSValidatedClient;
import com.marshals.business.Order;
import com.marshals.business.Price;
import com.marshals.business.Trade;
 
@Repository("fmtsDao")
public class FMTSDaoImpl implements FMTSDao {
	private ObjectMapper objectMapper = new ObjectMapper();
 
	// FMTS endpoint
	final private String url = "http://localhost:3000/fmts";
 
	/* FOR CLIENT VALIDATION */
	// For Registration
	@Override
	public FMTSValidatedClient verifyClient(String email) {
		String api = "/client";
		try {
			HttpResponse<String> response = null;
			// Creating the JSON body sent in POST req
			ObjectNode jsonNode = objectMapper.createObjectNode();
			jsonNode.put("clientId", "");
			jsonNode.put("email", email);
			String jsonBody = objectMapper.writeValueAsString(jsonNode);
 
			//Sending request and creating client to receive response
			HttpRequest request = HttpRequest.newBuilder().uri(new URI(url + api))
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(jsonBody))
					.build();
			HttpClient client = HttpClient.newBuilder().build();
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			//Check for response status code
			if(response.statusCode()==406) { //Error in validation
				throw new FMTSException("FMTS couldnt validate new Client's Email");
			}
			else if(response.statusCode()!=200) { //Any other status code
				throw new FMTSException("There was an unexpected error from FMTS while validating new client");
			}
			//If successful response
			FMTSValidatedClient validatedClient = objectMapper.readValue(response.body(), FMTSValidatedClient.class);
			return validatedClient;
		} catch(FMTSException e) {
			throw e;
		}
		catch (Exception e) { //Any other unexpected error
			throw new FMTSException("There was an unexpected error from FMTS while validating new client");
		}
	}
 
	// For login
	@Override
	public FMTSValidatedClient verifyClient(String email, String clientId) {
		String api = "/client";
		try {
			HttpResponse<String> response = null;
			// Creating the JSON body sent in POST req
			ObjectNode jsonNode = objectMapper.createObjectNode();
			jsonNode.put("clientId", clientId);
			jsonNode.put("email", email);
			String jsonBody = objectMapper.writeValueAsString(jsonNode);
 
			//Sending request and creating client to receive response
			HttpRequest request = HttpRequest.newBuilder().uri(new URI(url + api))
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(jsonBody))
					.build();
			HttpClient client = HttpClient.newBuilder().build();
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			//Check for response status code
			if(response.statusCode()==406) { //Error in validation
				throw new FMTSException("Logging in Client's validation credentials dont match");
			}
			else if(response.statusCode()!=200) { //Any other status code
				throw new FMTSException("There was an unexpected error from FMTS while validating logging in client");
			}
			//If successful response
			FMTSValidatedClient validatedClient = objectMapper.readValue(response.body(), FMTSValidatedClient.class);
			return validatedClient;
		} catch(FMTSException e) {
			throw e;
		}
		catch (Exception e) { //Any other unexpected error
			throw new FMTSException("There was an unexpected error from FMTS while validating logging in client");
		}
	}
 
	/* FOR TRADE EXECUTION */

	// Get live prices
	@Override
	public List<Price> getLivePrices() {
		String api = "/trades/prices";
		HttpResponse<String> response = null;
		Price[] prices = null;
		try {
			HttpRequest request = 
					HttpRequest.newBuilder()
						.uri(new URI(url+api))
						.GET()
						.build();
			
			HttpClient client = HttpClient.newBuilder().build();
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			prices = objectMapper.readValue(response.body(), Price[].class);
			return Arrays.asList(prices);	
		} catch (Exception e) {
			throw new FMTSException("Error in fetching live prices");
		}
	}

	// Execute trade
	@Override
	public Trade createTrade(Order order) {
		String api = "/trades/trade";
		HttpResponse<String> response = null;
		try {
			String jsonBody = prepareJsonBody(order);
			HttpRequest request = 
					HttpRequest.newBuilder()
						.uri(new URI(url+api))
						.POST(BodyPublishers.ofString(jsonBody))
						.header("Content-Type", "application/json")
						.build();
			
			HttpClient client = HttpClient.newBuilder().build();
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if(response.body() == null) {
				throw new NullPointerException("Trade returned null from fmts");
			}
			else if(response.statusCode()== 406) { //Error in validation
				throw new FMTSException("Token expired or is invalid");
			}
			else if(response.statusCode() == 409) {
				throw new FMTSException("Target price is not in the expected range of execution price");
			}
			else if(response.statusCode() == 400) {
				throw new FMTSException("Order invalid");
			}
			else if(response.statusCode()!=200) { //Any other status code
				throw new FMTSException("There was an unexpected error from FMTS while validating new client");
			}
			Trade processedTrade = objectMapper.readValue(response.body(), Trade.class);
			System.out.println(processedTrade);
			if(processedTrade == null) {
				throw new FMTSException("Invalid order, trade returned null");
			}
			return processedTrade;
		}
		catch(FMTSException e){
			throw e;
		} catch (Exception e) {
			throw new FMTSException("There was an unexpected error from FMTS while executing trade");
		} 
		
	}

	private String prepareJsonBody(Order order) throws JsonProcessingException {
		ObjectNode jsonNode = objectMapper.createObjectNode();
		jsonNode.put("orderId", order.getOrderId());
		jsonNode.put("quantity", order.getQuantity());
		jsonNode.put("targetPrice", order.getTargetPrice());
		jsonNode.put("direction", order.getDirection());
		jsonNode.put("clientId", order.getClientId());
		jsonNode.put("instrumentId", order.getInstrumentId());
		jsonNode.put("token", order.getToken());
		return objectMapper.writeValueAsString(order);
	}
 
}
