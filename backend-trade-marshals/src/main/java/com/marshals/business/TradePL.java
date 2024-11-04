package com.marshals.business;

import java.math.BigDecimal;
import java.util.Objects;

public class TradePL {
	
	private String instrumentId;
	private BigDecimal profitLossValue;
	
	public TradePL() {
	}

	public TradePL(String instrumentId, BigDecimal profitLossValue) {
		this.instrumentId = instrumentId;
		this.profitLossValue = profitLossValue;
	}

	public String getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(String instrumentId) {
		this.instrumentId = instrumentId;
	}

	public BigDecimal getProfitLossValue() {
		return profitLossValue;
	}

	public void setProfitLossValue(BigDecimal profitLossValue) {
		this.profitLossValue = profitLossValue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(instrumentId, profitLossValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TradePL other = (TradePL) obj;
		return Objects.equals(instrumentId, other.instrumentId)
				&& Objects.equals(profitLossValue, other.profitLossValue);
	}

	@Override
	public String toString() {
		return "TradePL [instrumentId=" + instrumentId + ", profitLossValue=" + profitLossValue + "]";
	}
	
}
