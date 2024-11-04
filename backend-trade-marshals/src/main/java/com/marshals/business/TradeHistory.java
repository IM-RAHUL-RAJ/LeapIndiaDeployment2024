package com.marshals.business;
 
import java.util.List;
import java.util.Objects;
 
public class TradeHistory {
	 private String clientId;
	 private List<Trade> trades;
	 
	 public TradeHistory() {}
	 
	public TradeHistory(String clientId, List<Trade> trades) {
		try {
    		if(clientId==null || trades==null) 
				throw new NullPointerException("Client Portfolio Details cannot be null");
			this.clientId = clientId;
			this.trades = trades;
		}catch(NullPointerException e) {
    		throw e;
    	}
	}
	
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientId() {
        return clientId;
    }
	public List<Trade> getTrades(){
		return trades;
	}
	public void setTrades(List<Trade> trades)
	{
		this.trades = trades;
	}
 
	@Override
	public int hashCode() {
		return Objects.hash(clientId, trades);
	}
 
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TradeHistory other = (TradeHistory) obj;
		return Objects.equals(clientId, other.clientId) && Objects.equals(trades, other.trades);
	}

	@Override
	public String toString() {
		return "TradeHistory [clientId=" + clientId + ", trades=" + trades + "]";
	}
	
	
}