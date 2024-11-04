package com.marshals.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClientPortfolio {
	 private String clientId;
	 private BigDecimal currBalance;
	 private List<Holding> holdings;

	    // Constructors, getters, and setters
	 	public ClientPortfolio() {}
	 

	    public ClientPortfolio(String clientId, BigDecimal currBalance, List<Holding> holdings) {
	    	try {
	    		if(clientId==null || currBalance==null ) 
					throw new NullPointerException("Client Portfolio Details cannot be null");
	    		this.clientId = clientId;
	 	        this.currBalance = currBalance;
	 	        this.holdings = (holdings == null) ? new ArrayList<>() : holdings;
	    	} catch(NullPointerException e) {
	    		throw e;
	    	}
	        
	    }

	    public String getClientId() {
	        return clientId;
	    }

	    public BigDecimal getCurrBalance() {
	        return currBalance;
	    }

	    public void setCurrBalance(BigDecimal currBalance) {
	        this.currBalance = currBalance;
	    }

	    public List<Holding> getHoldings() {
	        return holdings;
	    }

	    public void setHoldings(List<Holding> holdings) {
	        this.holdings = holdings;
	    }

		@Override
		public int hashCode() {
			return Objects.hash(clientId, currBalance, holdings);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ClientPortfolio other = (ClientPortfolio) obj;
			return Objects.equals(clientId, other.clientId) && Objects.equals(currBalance, other.currBalance)
					&& Objects.equals(holdings, other.holdings);
		}

		@Override
		public String toString() {
			return "ClientPortfolio [clientId=" + clientId + ", currBalance=" + currBalance + ", holdings=" + holdings
					+ "]";
		}
	    
}
