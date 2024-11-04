package com.marshals.integration;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import com.marshals.business.ClientPreferences;
import com.marshals.integration.mapper.ClientPreferencesMapper;

@Repository("clientPreferencesDao")
public class ClientPreferencesDaoImpl implements ClientPreferencesDao{

	@Autowired 
	private ClientPreferencesMapper clientMapper;
	
	@Autowired
	private Logger logger;

	@Override
	public ClientPreferences getClientPreferences(String clientId) {
		ClientPreferences preferences = clientMapper.getClientPreferences(clientId);
		if(preferences == null) {
			throw new DatabaseException("Please enter your client preferences");
		}
		return preferences;
	}

	@Override
	public boolean addClientPreferences(ClientPreferences clientPreferences) {
		int rowsAffected = 0;
		try {
			rowsAffected = clientMapper.addClientPreferences(clientPreferences);
		}
		catch(DuplicateKeyException e) {
			logger.error("Client already exists with this Client ID", e);
			throw new DatabaseException("Client already exists with this Client ID");
		}
		catch(DataIntegrityViolationException e) {
			logger.error("Error in adding client preferences details: ",e);
			throw new DatabaseException("Client doesn't exist with this Client ID");
		}
		return rowsAffected > 0;
	}

	@Override
	public boolean updateClientPreferences(ClientPreferences clientPreferences) {
		int rowsAffected = 0;
		try {
			rowsAffected = clientMapper.updateClientPreferences(clientPreferences);
			if(rowsAffected == 0) {
				throw new DatabaseException("Client doesn't exist with this Client ID");
			}
		}
		catch(DatabaseException e) {
			logger.error("Error in updating client preferences details: "+ e);
			throw e;
		}
		return rowsAffected > 0;
	}
}
