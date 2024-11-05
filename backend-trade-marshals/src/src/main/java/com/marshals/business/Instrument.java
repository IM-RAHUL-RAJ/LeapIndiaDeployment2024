package com.marshals.business;

import java.util.Objects;

public class Instrument {
	private String instrumentId;
	private String externalIdType;
    private String externalId;
    private String categoryId;
    private String instrumentDescription;
    private Integer maxQuantity;
    private Integer minQuantity;
    
    public Instrument() {
    }
    
	public Instrument(String instrumentId, String externalIdType, String externalId, String categoryId,
			String instrumentDescription, Integer maxQuantity, Integer minQuantity) {
		if(instrumentId == null) {
			throw new NullPointerException("instrumentId cannot be null");
		}
		if(instrumentId.isEmpty()) {
			throw new IllegalArgumentException("instrumentId cannot be empty");
		}
		if(externalIdType == null) {
			throw new NullPointerException("externalIdType cannot be null");
		}
		if(externalIdType.isEmpty()) {
			throw new IllegalArgumentException("externalIdType cannot be empty");
		}
		if(externalId == null) {
			throw new NullPointerException("externalId cannot be null");
		}
		if(externalId.isEmpty()) {
			throw new IllegalArgumentException("externalId cannot be empty");
		}
		if(categoryId == null) {
			throw new NullPointerException("categoryId cannot be null");
		}
		if(categoryId.isEmpty()) {
			throw new IllegalArgumentException("categoryId cannot be empty");
		}
		if(instrumentDescription == null) {
			throw new NullPointerException("instrumentDescription cannot be null");
		}
		if(instrumentDescription.isEmpty()) {
			throw new IllegalArgumentException("instrumentDescription cannot be empty");
		}
		if(maxQuantity == null) {
			throw new NullPointerException("maxQuantity cannot be null");
		}
		if(maxQuantity < 0) {
			throw new IllegalArgumentException("maxQuantity cannot be negative");
		}
		if(minQuantity == null) {
			throw new NullPointerException("minQuantity cannot be null");
		}
		if(minQuantity < 0) {
			throw new IllegalArgumentException("minQuantity cannot be negative");
		}
		this.instrumentId = instrumentId;
		this.externalIdType = externalIdType;
		this.externalId = externalId;
		this.categoryId = categoryId;
		this.instrumentDescription = instrumentDescription;
		this.maxQuantity = maxQuantity;
		this.minQuantity = minQuantity;
	}

	public String getInstrumentId() {
		return instrumentId;
	}

	public String getExternalIdType() {
		return externalIdType;
	}

	public String getExternalId() {
		return externalId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public String getInstrumentDescription() {
		return instrumentDescription;
	}
	
	public Integer getMaxQuantity() {
		return maxQuantity;
	}

	public Integer getMinQuantity() {
		return minQuantity;
	}

	@Override
	public int hashCode() {
		return Objects.hash(categoryId, externalId, externalIdType, instrumentDescription, instrumentId, maxQuantity,
				minQuantity);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Instrument other = (Instrument) obj;
		return Objects.equals(categoryId, other.categoryId) && Objects.equals(externalId, other.externalId)
				&& Objects.equals(externalIdType, other.externalIdType)
				&& Objects.equals(instrumentDescription, other.instrumentDescription)
				&& Objects.equals(instrumentId, other.instrumentId) && Objects.equals(maxQuantity, other.maxQuantity)
				&& Objects.equals(minQuantity, other.minQuantity);
	}

	@Override
	public String toString() {
		return "Instrument [instrumentId=" + instrumentId + ", externalIdType=" + externalIdType + ", externalId="
				+ externalId + ", categoryId=" + categoryId + ", instrumentDescription=" + instrumentDescription
				+ ", maxQuantity=" + maxQuantity + ", minQuantity=" + minQuantity + "]";
	}    
    
}


