package com.marshals.business;

import java.math.BigDecimal;
import java.util.Objects;

public class Order {
    private String instrumentId;
    private Integer quantity;
    private BigDecimal targetPrice;
    private String direction;
    private String clientId;
    private String orderId;
    private Integer token;
    
    public Order() {}

    public Order(String instrumentId, Integer quantity, BigDecimal targetPrice, String direction, String clientId,
                 String orderId, Integer token) {
        if (instrumentId == null || instrumentId.isEmpty()) {
            throw new IllegalArgumentException("instrumentId cannot be null or empty");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than 0");
        }
        if (targetPrice == null || targetPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("targetPrice must be greater than 0");
        }
        if (direction == null || direction.isEmpty()) {
            throw new IllegalArgumentException("direction cannot be null or empty");
        }
        if (clientId == null || clientId.isEmpty()) {
            throw new IllegalArgumentException("clientId cannot be null or empty");
        }
        if (orderId == null || orderId.isEmpty()) {
            throw new IllegalArgumentException("orderId cannot be null or empty");
        }

        this.instrumentId = instrumentId;
        this.quantity = quantity;
        this.targetPrice = targetPrice;
        this.direction = direction;
        this.clientId = clientId;
        this.orderId = orderId;
        this.token = token;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public String getDirection() {
        return direction;
    }

    public String getClientId() {
        return clientId;
    }

    public String getOrderId() {
        return orderId;
    }

	public Integer getToken() {
		return token;
	}

	
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(clientId, direction, instrumentId, orderId, quantity, targetPrice, token);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		return Objects.equals(clientId, other.clientId) && Objects.equals(direction, other.direction)
				&& Objects.equals(instrumentId, other.instrumentId) && Objects.equals(orderId, other.orderId)
				&& Objects.equals(quantity, other.quantity) && Objects.equals(targetPrice, other.targetPrice)
				&& Objects.equals(token, other.token);
	}

	@Override
	public String toString() {
		return "Order [instrumentId=" + instrumentId + ", quantity=" + quantity + ", targetPrice=" + targetPrice
				+ ", direction=" + direction + ", clientId=" + clientId + ", orderId=" + orderId + ", token=" + token
				+ "]";
	}

}

