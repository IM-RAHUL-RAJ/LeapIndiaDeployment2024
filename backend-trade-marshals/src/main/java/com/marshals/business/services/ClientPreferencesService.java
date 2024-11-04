package com.marshals.business.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.marshals.business.ClientPreferences;
import com.marshals.integration.ClientPreferencesDao;
import com.marshals.integration.DatabaseException;

@Service("clientPreferencesService")
public class ClientPreferencesService {

	// Client Dao Object which interacts with DB
	private ClientPreferencesDao clientPreferencesDao;

	@Autowired
	public ClientPreferencesService(@Qualifier("clientPreferencesDao") ClientPreferencesDao dao) {
		this.clientPreferencesDao = dao; //Intializing the Dao Object
	}
	
	/* Methods Related to Adding and Updating of Client Preferences */
	public boolean addClientPreferences(ClientPreferences preferences) {
		try {
			if (preferences == null) {
				throw new NullPointerException("Preferences cannot be null");
			}
			return clientPreferencesDao.addClientPreferences(preferences);
		} catch (NullPointerException e) {
			throw e;
		}
	}

	public ClientPreferences getClientPreferences(String clientId) {
		ClientPreferences clientPreferences = null;
		try {
			if (clientId == null) {
				throw new NullPointerException("Id should not be null");
			}
			clientPreferences = clientPreferencesDao.getClientPreferences(clientId);
		} catch (NullPointerException e) {
			throw e;
		}
		return clientPreferences;
	}

	public boolean updateClientPreferences(ClientPreferences preferences) {
		try {
			if (preferences == null) {
				throw new NullPointerException("Preferences should not be null");
			}
			return clientPreferencesDao.updateClientPreferences(preferences);
		} catch (NullPointerException e) {
			throw e;
		}
	}
}
