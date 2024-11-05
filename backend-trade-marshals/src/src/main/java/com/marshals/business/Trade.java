package com.marshals.business;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Trade {
    private String instrumentId;
    private Integer quantity;
    private BigDecimal executionPrice;
    private String direction;
    private String clientId;
    private Order order;
    private String tradeId;
    private BigDecimal cashValue;
    private LocalDateTime executedAt;
    
    public Trade() {}

    public Trade(Order order, BigDecimal executionPrice, String tradeId, 
    		BigDecimal cashValue) {
        if (executionPrice == null || executionPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("executionPrice must be greater than 0");
        }
        if (tradeId == null || tradeId.isEmpty()) {
            throw new IllegalArgumentException("tradeId cannot be null or empty");
        }
        if (cashValue == null || cashValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("cashValue cannot be null and must be non-negative");
        }
        
        this.instrumentId = order.getInstrumentId();
        this.quantity = order.getQuantity();
        this.executionPrice = executionPrice;
        this.direction = order.getDirection();
        this.clientId = order.getClientId();
        this.order = order;
        this.tradeId = tradeId;
        this.cashValue = cashValue;
    }
    
    public Trade(Order order, BigDecimal executionPrice, String tradeId, 
    		BigDecimal cashValue, LocalDateTime executedAt) {
        if (executionPrice == null || executionPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("executionPrice must be greater than 0");
        }
        if (tradeId == null || tradeId.isEmpty()) {
            throw new IllegalArgumentException("tradeId cannot be null or empty");
        }
        if (cashValue == null || cashValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("cashValue cannot be null and must be non-negative");
        }
        
        this.instrumentId = order.getInstrumentId();
        this.quantity = order.getQuantity();
        this.executionPrice = executionPrice;
        this.direction = order.getDirection();
        this.clientId = order.getClientId();
        this.order = order;
        this.tradeId = tradeId;
        this.cashValue = cashValue;
        this.executedAt = executedAt;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getExecutionPrice() {
        return executionPrice;
    }

    public String getDirection() {
        return direction;
    }

    public String getClientId() {
        return clientId;
    }

    public Order getOrder() {
        return order;
    }

    public String getTradeId() {
        return tradeId;
    }

    public BigDecimal getCashValue() {
        return cashValue;
    }
    
    public LocalDateTime getExecutedAt() {
    	return executedAt;
    }
    
	public void setInstrumentId(String instrumentId) {
		this.instrumentId = instrumentId;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public void setExecutionPrice(BigDecimal executionPrice) {
		this.executionPrice = executionPrice;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public void setCashValue(BigDecimal cashValue) {
		this.cashValue = cashValue;
	}
	
	public void setExecutedAt(LocalDateTime executedAt) {
		this.executedAt = executedAt;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cashValue, clientId, direction, executedAt, executionPrice, instrumentId, order, quantity,
				tradeId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Trade other = (Trade) obj;
		return Objects.equals(cashValue, other.cashValue) && Objects.equals(clientId, other.clientId)
				&& Objects.equals(direction, other.direction) && Objects.equals(executedAt, other.executedAt)
				&& Objects.equals(executionPrice, other.executionPrice)
				&& Objects.equals(instrumentId, other.instrumentId) && Objects.equals(order, other.order)
				&& Objects.equals(quantity, other.quantity) && Objects.equals(tradeId, other.tradeId);
	}

	@Override
	public String toString() {
		return "Trade [instrumentId=" + instrumentId + ", quantity=" + quantity + ", executionPrice=" + executionPrice
				+ ", direction=" + direction + ", clientId=" + clientId + ", order=" + order + ", tradeId=" + tradeId
				+ ", cashValue=" + cashValue + ", executedAt=" + executedAt + "]";
	}

	
    
}
