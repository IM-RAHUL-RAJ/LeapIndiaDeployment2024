package com.marshals.business;

import java.math.BigDecimal;
import java.util.Objects;

public class Price {
	private BigDecimal askPrice;
    private BigDecimal bidPrice;
    private String priceTimestamp;
    private Instrument instrument;
    
	public Price() {
	}

	public Price(BigDecimal askPrice, BigDecimal bidPrice, String priceTimestamp, Instrument instrument) {
		if (askPrice == null) {
	        throw new NullPointerException("askPrice cannot be null");
	    }
	    if (askPrice.compareTo(BigDecimal.ZERO) < 0) {
	        throw new IllegalArgumentException("askPrice cannot be negative");
	    }
	    if (bidPrice == null) {
	        throw new NullPointerException("bidPrice cannot be null");
	    }
	    if (bidPrice.compareTo(BigDecimal.ZERO) < 0) {
	        throw new IllegalArgumentException("bidPrice cannot be negative");
	    }
		this.askPrice = askPrice;
		this.bidPrice = bidPrice;
		this.priceTimestamp = priceTimestamp;
		this.instrument = instrument;
	}

	public BigDecimal getAskPrice() {
		return askPrice;
	}

	public BigDecimal getBidPrice() {
		return bidPrice;
	}

	public String getPriceTimestamp() {
		return priceTimestamp;
	}

	public Instrument getInstrument() {
		return instrument;
	}

	@Override
	public int hashCode() {
		return Objects.hash(askPrice, bidPrice, instrument, priceTimestamp);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Price other = (Price) obj;
		return Objects.equals(askPrice, other.askPrice) && Objects.equals(bidPrice, other.bidPrice)
				&& Objects.equals(instrument, other.instrument) && Objects.equals(priceTimestamp, other.priceTimestamp);
	}

	@Override
	public String toString() {
		return "Price [askPrice=" + askPrice + ", bidPrice=" + bidPrice + ", priceTimestamp=" + priceTimestamp
				+ ", instrument=" + instrument + "]";
	}
	
}
