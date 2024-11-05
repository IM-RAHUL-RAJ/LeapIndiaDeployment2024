package com.marshals.business;

import java.math.BigDecimal;
import java.util.Objects;

public class Holding {
     private String instrumentId;
     private int quantity;
     private BigDecimal avgPrice;

     // Constructors, getters, and setters
     public Holding() {}

     public Holding( String instrumentId, int quantity, BigDecimal avgPrice) {
         this.instrumentId = instrumentId;
         this.quantity = quantity;
         this.avgPrice = avgPrice;
     }

     public String getInstrumentId() {
         return instrumentId;
     }

     public void setInstrumentId(String instrumentId) {
         this.instrumentId = instrumentId;
     }

     public int getQuantity() {
         return quantity;
     }
    
     public void setQuantity(int quantity) {
         this.quantity = quantity;
     }

	public BigDecimal getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(BigDecimal avgPrice) {
		this.avgPrice = avgPrice;
	}

	@Override
	public int hashCode() {
		return Objects.hash(avgPrice, instrumentId, quantity);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Holding other = (Holding) obj;
		return Objects.equals(avgPrice, other.avgPrice) && Objects.equals(instrumentId, other.instrumentId)
				&& quantity == other.quantity;
	}

	@Override
	public String toString() {
		return "Holding [instrumentId=" + instrumentId + ", quantity=" + quantity + ", avgPrice=" + avgPrice + "]";
	}
	
}
