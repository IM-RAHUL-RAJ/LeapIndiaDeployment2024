package com.marshals.restcontroller;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.marshals.business.Client;
import com.marshals.business.LoggedInClient;
import com.marshals.business.services.ClientService;
import com.marshals.integration.DatabaseException;
import com.marshals.integration.FMTSException;

@RestController("clientController")
@RequestMapping("/client")
public class ClientController {

	@Autowired
	private Logger logger;

	@Autowired
	private ClientService clientService;

	@GetMapping(value = "/ping")
	public String ping() {
		return "Client web service is alive at " + LocalDateTime.now();
	}

	// Verify client email
	@GetMapping(value = "/verify-email/{email}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<VerificationRequestResult> verifyClientEmail(@PathVariable String email) {
		ResponseEntity<VerificationRequestResult> response = null;
		try {
			boolean isEmailVerified = clientService.verifyClientEmail(email);
			response = ResponseEntity.ok(new VerificationRequestResult(isEmailVerified)); // 200 - Successfully
																							// validated client
			return response;
		}
//		catch(NullPointerException e) { //406
//			logger.error("Error in request for validating client email", e);
//			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getLocalizedMessage());
//		}
		catch (IllegalArgumentException e) { // 406
			logger.error("Error in request for validating client email", e);
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getLocalizedMessage());
		} catch (RuntimeException e) { // Unexpected error - 500
			logger.error("Problem occured from server", e);
			response = ResponseEntity.internalServerError().build();
			return response;
		}
	}

	// Register new client
	@PostMapping(value = "/register", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<LoggedInClient> registerNewClient(@RequestBody Client client) { // Get a client object with
																							// null clientId
		ResponseEntity<LoggedInClient> response = null;
		try {
//			if(client == null) {
//				throw new NullPointerException("Client Request Body is null");
//			}
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy"); // doB Format stored in client model
			if (client.getDateOfBirth() == null)
				throw new NullPointerException("Client details cannot be null");
			String dateOfBirth = dateFormat.format(client.getDateOfBirth());
			LoggedInClient newClient = clientService.registerNewClient(client.getEmail(), client.getPassword(),
					client.getName(), dateOfBirth, client.getCountry(), client.getIdentification());
			response = ResponseEntity.ok(newClient); // 200
			return response;
		} catch (NullPointerException e) { // 406
			logger.error("Error in request for registering new client", e);
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getLocalizedMessage());
		} catch (IllegalArgumentException e) { // 406
			logger.error("Error in request for registering new client", e);
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getLocalizedMessage());
		} catch (DatabaseException e) { // 404
			logger.error("Error in request for registering new client", e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
		} catch (FMTSException e) { // 404
			logger.error("Error in request for registering new client", e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
		} catch (RuntimeException e) { // Unexpected error - 500
			logger.error("Problem occured from server", e);
			response = ResponseEntity.internalServerError().build();
			return response;
		}
	}

	// Login existing client - Send email and password as Query Params
	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<LoggedInClient> loginExistingClient(@RequestParam String[] email, @RequestParam String[] password) {
		String actualEmail = (email.length > 0) ? email[0] : ""; // Safely get the first email
		String actualPassword = (password.length > 0) ? password[0] : ""; // Safely get the first password
		System.out.println("Email: " + actualEmail + "\nPassword: " + actualPassword);
		ResponseEntity<LoggedInClient> response = null;
		try {
//			if(email == null || password == null) {
//				throw new NullPointerException("Client login credentials are null");
//			}
			LoggedInClient newClient = clientService.loginExistingClient(actualEmail, actualPassword);
			response = ResponseEntity.ok(newClient); // 200
			return response;
		} catch (IllegalArgumentException e) { // 406
			logger.error("Error in request for registering new client", e);
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getLocalizedMessage());
		} catch (DatabaseException e) { // 404
			logger.error("Error in request for registering new client", e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
		} catch (FMTSException e) { // 404
			logger.error("Error in request for registering new client", e);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
		} catch (RuntimeException e) { // Unexpected error - 500
			logger.error("Problem occured from server", e);
			response = ResponseEntity.internalServerError().build();
			return response;
		}
	}
}
