package com.marshals.integration;

import com.marshals.business.ClientPreferences;

//Has functions related to Client preferences related data access
public interface ClientPreferencesDao {

	ClientPreferences getClientPreferences(String clientId);

	boolean addClientPreferences(ClientPreferences clientPreferences);

	boolean updateClientPreferences(ClientPreferences clientPreferences);
}
