package com.marshals.integration.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.marshals.business.Client;
import com.marshals.business.ClientIdentification;
import com.marshals.business.ClientPortfolio;

@Mapper
public interface ClientMapper {
	
	//Client
	String verifyClientEmail(String email); //Email Validation
	Client getClientAtLogin(String email); //For login
	//For registering
	List<ClientIdentification> getAllClientIdentificationDetails(); //Required for ID validation of new client
	int addNewClient(@Param("client") Client client, @Param("clientPortfolio") ClientPortfolio clientPortfolio); //Saving new client details (along with portfolio)
	int addNewClientIdentificationDetails(@Param("clientId") String clientId, @Param("identification") ClientIdentification identification); //Saving new client identification details
	
}