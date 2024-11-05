package com.marshals.business.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.marshals.business.Client;
import com.marshals.business.ClientIdentification;
import com.marshals.business.ClientPortfolio;
import com.marshals.business.FMTSValidatedClient;
import com.marshals.business.Holding;
import com.marshals.business.LoggedInClient;
import com.marshals.integration.ClientDao;
import com.marshals.integration.DatabaseException;
import com.marshals.integration.FMTSException;

@Service("clientService")
public class ClientService {
	
	//Client Dao Object which interacts with DB
	private ClientDao clientDao;
	
	private FMTSService fmtsService;
	
	public ClientService() {}

	@Autowired
	public ClientService(@Qualifier("clientDao") ClientDao dao, @Qualifier("fmtsService") FMTSService fmtsService) {
		this.clientDao = dao; //Intializing the Dao Object
		this.fmtsService = fmtsService;
	}
	
	/*Methods related to Client - Email Validation, Login and Register*/
	
	//Verifying Email Address - Checking if given client already exists
	public Boolean verifyClientEmail(String email) {
		try {
			if(email==null) 
				throw new NullPointerException("Client Email cannot be null");
			Boolean isClientExists = clientDao.verifyClientEmail(email);
			return isClientExists;
		} catch(NullPointerException e) {
			throw e;
		} catch(IllegalArgumentException e) { //Email format is invalid
			throw e;
		} catch(DatabaseException e) { //Email not found
			return false;
		}
	}
	
	//Helper method - To verifying Client ID Details - If already exists or not - For registration
	private Boolean verifyClientIdentityDetails(ClientIdentification clientIdentification) {
		try {
			if(clientIdentification==null) 
				throw new NullPointerException("Client Identification Details cannot be null");
			 //Get the list of existing client identification details with dao
			 List<ClientIdentification> clientIdentifications = clientDao.getAllClientIdentificationDetails();
			for(ClientIdentification identification:clientIdentifications) {
				if(identification.equals(clientIdentification)) //If given clientIdentification exists
					return true;
			}
			return false;
		} catch(NullPointerException e) {
			throw e;
		} catch(DatabaseException e) {
			throw e;
		}
	}

	//Registering a new client
	public LoggedInClient registerNewClient(String email, String password, String name,
										String dateOfBirth, String country, List<ClientIdentification> identification) {
		try {
			LoggedInClient loggedInClient = null;
			Client client = null;
			//Verifying the email passed to check if client already exists or not
			if(verifyClientEmail(email))  //If email already registered
				throw new IllegalArgumentException("Client with given email is already registered");
			if(password==null || name==null || dateOfBirth==null || country==null)
				throw new NullPointerException("Client Details cannot be null");
			
			//Verifying the Client Identification information
			for(ClientIdentification id: identification) {
				if(verifyClientIdentityDetails(id)) //If identification details already present returns true
					throw new IllegalArgumentException("Client with given Identification Details is already registered with another email");
			}
			
			//Validating Client with FMTS
			FMTSValidatedClient validatedClient = fmtsService.verifyClient(email); 
			if(validatedClient == null)
				throw new NullPointerException("New Client Details couldnt be validated");
			
			//ON SUCCESSFUL VERIFICATION OF REGISTRATION - Saving the clients details
			client = new Client(email,validatedClient.getClientId(), password, name, dateOfBirth, country, identification, false);
			saveNewClientDetails(client);
			//Return logging in client info - which has token
			loggedInClient = new LoggedInClient(client,validatedClient.getToken());
			return loggedInClient;
		} catch(NullPointerException e) {
			throw e;
		} catch(IllegalArgumentException e) {
			throw e;
		} catch(FMTSException e) {
			System.out.println("Error: "+e.getMessage()+"/n"+e);
			throw e;
		} catch(DatabaseException e) {
			System.out.println("Error: "+e.getMessage()+"/n"+e);
			throw e;
		}
	}
	
	//Save new Client Details
	private void saveNewClientDetails(Client newClient) {
		//Setting Client Portfolio details with initial Balance of 10k Dollars and empty holdings
		ClientPortfolio newClientPortfolio = new ClientPortfolio(newClient.getClientId(),new BigDecimal("10000").setScale(4),new ArrayList<Holding>());
		//Calling dao to add the new client details
		clientDao.addNewClient(newClient, newClientPortfolio);
	}
	
	//Logging in an existing client
	public LoggedInClient loginExistingClient(String email, String password) {
		try {
			LoggedInClient loggedInClient = null;
			Client existingClient = null;
			
			//Verifying the email passed to check if client already exists or not
			if(!verifyClientEmail(email)) //If client not existing
				throw new IllegalArgumentException("Client with given email is not registered");
			if(password==null)
				throw new NullPointerException("Client Password cannot be null");
			
			//Calling dao to retrieve client details
			existingClient = clientDao.getClientAtLogin(email, password);
			
			//Validating Existing Client with FMTS
			FMTSValidatedClient validatedClient = fmtsService.verifyClient(email,existingClient.getClientId()); 
			if(validatedClient == null)
				throw new NullPointerException("Logging in Client Details couldnt be validated");
 
			//ON SUCCESSFUL LOGIN - Returning logging in client info - which has token
			loggedInClient = new LoggedInClient(existingClient,validatedClient.getToken());
			return loggedInClient;
		} catch(NullPointerException e) {
			throw e;
		} catch(IllegalArgumentException e) {
			throw e;
		} catch(FMTSException e) {
			throw e;
		} catch(DatabaseException e) {
			throw e;
		}
	}
		
}
