package com.marshals.integration.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.marshals.business.ClientPreferences;

@Mapper
public interface ClientPreferencesMapper {

	// Client Preferences
	ClientPreferences getClientPreferences(String clientId);
	int addClientPreferences(ClientPreferences clientPreferences); // returns number of rows affected
	int updateClientPreferences(ClientPreferences clientPreferences);// returns number of rows updated
}
