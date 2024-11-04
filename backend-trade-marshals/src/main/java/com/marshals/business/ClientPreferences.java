package com.marshals.business;

import java.util.Objects;

public class ClientPreferences {
	
	private String clientId;
	private String investmentPurpose;
	private String incomeCategory;
	private String lengthOfInvestment;
	private String percentageOfSpend;
	private int riskTolerance;
	private boolean acceptAdvisor;
	
	public ClientPreferences() {}
	
	public ClientPreferences(String clientId, String investmentPurpose, String incomeCategory,
			String lengthOfInvestment, String percentageOfSpend, int riskTolerance, boolean acceptAdvisor) {
		
		try {
			if(clientId == null) throw new NullPointerException("Client ID cannot be null");
				//Only checking validity of fields that are not explicitly covered in Angular
				if(riskTolerance < 1 || riskTolerance > 5) {
					throw new IllegalArgumentException("Tolerance should be between 1 to 5");
				}
				this.clientId = clientId;
				this.investmentPurpose = investmentPurpose;
				this.incomeCategory = incomeCategory;
				this.lengthOfInvestment = lengthOfInvestment;
				this.percentageOfSpend = percentageOfSpend;
				this.riskTolerance = riskTolerance;
				this.acceptAdvisor = acceptAdvisor;
		} catch(NullPointerException e) {
			throw e;
		}	catch(IllegalArgumentException e) {
			throw e;
		}
		
	}
	
	public String getInvestmentPurpose() {
		return investmentPurpose;
	}

	public void setInvestmentPurpose(String investmentPurpose) {
		this.investmentPurpose = investmentPurpose;
	}

	public String getLengthOfInvestment() {
		return lengthOfInvestment;
	}

	public void setLengthOfInvestment(String lengthOfInvestment) {
		this.lengthOfInvestment = lengthOfInvestment;
	}

	public String getPercentageOfSpend() {
		return percentageOfSpend;
	}

	public void setPercentageOfSpend(String percentageOfSpend) {
		this.percentageOfSpend = percentageOfSpend;
	}

	public int getRiskTolerance() {
		return riskTolerance;
	}

	public void setRiskTolerance(int riskTolerance) {
		this.riskTolerance = riskTolerance;
	}

	public String getAcceptAdvisor() {
		return this.acceptAdvisor ? "true": "false";
	}

	public void setAcceptAdvisor(String acceptAdvisor) {
		this.acceptAdvisor = acceptAdvisor.equals("true");
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientId() {
		return this.clientId;
	}
	
	public void setIncomeCategory(String incomeCategory) {
		this.incomeCategory = incomeCategory;
	}
	
	public String getIncomeCategory() {
		return this.incomeCategory;
	}

	@Override
	public int hashCode() {
		return Objects.hash(acceptAdvisor, clientId, incomeCategory, investmentPurpose, lengthOfInvestment,
				percentageOfSpend, riskTolerance);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClientPreferences other = (ClientPreferences) obj;
		return Objects.equals(acceptAdvisor, other.acceptAdvisor) && Objects.equals(clientId, other.clientId)
				&& Objects.equals(incomeCategory, other.incomeCategory)
				&& Objects.equals(investmentPurpose, other.investmentPurpose)
				&& Objects.equals(lengthOfInvestment, other.lengthOfInvestment)
				&& Objects.equals(percentageOfSpend, other.percentageOfSpend) && riskTolerance == other.riskTolerance;
	}

	@Override
	public String toString() {
		return "ClientPreferences [clientId=" + clientId + ", investmentPurpose=" + investmentPurpose
				+ ", incomeCategory=" + incomeCategory + ", lengthOfInvestment=" + lengthOfInvestment
				+ ", percentageOfSpend=" + percentageOfSpend + ", riskTolerance=" + riskTolerance + ", acceptAdvisor="
				+ acceptAdvisor + "]";
	}
	
}
