package com.marshals.integration.mapper;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.marshals.business.ClientPortfolio;
import com.marshals.business.Holding;

@Mapper
public interface ClientPortfolioMapper {
	
	//Get Client Portoflio
	ClientPortfolio getClientPortfolio(String clientId); 
	//Updation of Client Portfolio has 3 parts - UpdateClientBalanc, addHoldings and updateHoldings
	int updateClientBalance(@Param("clientId") String clientId,@Param("currBalance") BigDecimal currBalance);
	int addClientHoldings(@Param("clientId") String clientId, @Param("holding")Holding holding);
	int updateClientHoldings(@Param("clientId") String clientId, @Param("holding") Holding holding); 
	int deleteClientHoldings(@Param("clientId") String clientId, @Param("holding")Holding holding);
}
