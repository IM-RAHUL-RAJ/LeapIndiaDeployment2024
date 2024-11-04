package com.marshals.integration;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.marshals.business.ClientPortfolio;
import com.marshals.business.Holding;
import com.marshals.integration.mapper.ClientPortfolioMapper;

@Repository("clientPortfolioDao")
public class ClientPortfolioDaoImpl implements ClientPortfolioDao{
	
	@Autowired
	private ClientPortfolioMapper clientPortfolioMapper;

	@Override
	public ClientPortfolio getClientPortfolio(String clientId) {
		 ClientPortfolio clientPortfolio = clientPortfolioMapper.getClientPortfolio(clientId);
        if (clientPortfolio == null) {
            throw new DatabaseException("Client ID does not exist");
        }
        return clientPortfolio;
	}
 
	@Override
	public void updateClientBalance(String clientId, BigDecimal currBalance) {
		int rowcount=0; 
		try {
	            rowcount = clientPortfolioMapper.updateClientBalance(clientId, currBalance);
	            if(rowcount ==0) {
	            	 throw new DatabaseException("Client does not exist to update balance");
	            }
	        } catch (Exception e) {
	            throw e;
	        }
	}
	
	@Override
	public int updateClientHoldings(String clientId, Holding holding) {
		int rowcount =0;
		try {
	            rowcount = clientPortfolioMapper.updateClientHoldings(clientId, holding);
 
		 	if(rowcount ==0) {
           	 throw new DatabaseException("Client does not exist to update holdings");
           }
		 	return rowcount;
       }  catch (DatabaseException e) {
           throw e;
       } catch (DataIntegrityViolationException e) {
    	   throw new DatabaseException("Holdings for given client does not exist");
       }
	}
	
	@Override
	public int addClientHoldings(String clientId, Holding holding) {
		int rowcount = 0;
		try {
            rowcount = clientPortfolioMapper.addClientHoldings(clientId, holding);
            if(rowcount == 0) {
           	 throw new DatabaseException("Client does not exist to add holding");
           }
            return rowcount;
       } catch (DatabaseException e) {
           throw e;
       } catch (DataIntegrityViolationException e) {
    	   throw new DatabaseException("Holdings for given client already exists");
       }
	}

	@Override
	public int deleteClientHoldings(String clientId, Holding holding) {
		int rowcount = 0;
		try {
            rowcount = clientPortfolioMapper.deleteClientHoldings(clientId, holding);
            if(rowcount == 0) {
           	 throw new DatabaseException("Client does not exist to delete holding");
           }
            return rowcount;
       } catch (DatabaseException e) {
           throw e;
       } catch (DataIntegrityViolationException e) {
    	   throw new DatabaseException("Client does not exist to delete holding");
       }
	}

}
