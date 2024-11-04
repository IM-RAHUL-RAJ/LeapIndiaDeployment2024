package com.marshals.utils;


import com.marshals.business.ClientPreferences;

public class PriceScorer{
	
	private ClientPreferences clientPreferences;

	public PriceScorer(ClientPreferences clientPreferences) {
		this.clientPreferences = clientPreferences;
	}
	
	public int calculateScore() {
        int score = 0;

        // Example scoring logic
        switch (this.clientPreferences.getInvestmentPurpose()) {
            case "Retirement":
                score += 5;
                break;
            case "Education":
                score += 4;
                break;
            case "Major Expense":
                score += 3;
                break;
            case "Gift":
                score += 2;
                break;
        }

        switch (this.clientPreferences.getIncomeCategory()) {
            case "LIG":
//            	Low
                score += 1;
                break;
            case "MIG":
//            	Middle
                score += 2;
                break;
            case "HIG":
//            	High
                score += 3;
                break;
            case "VHIG":
//            	Very high
                score += 4;
                break;
        }

        // Adjust for length of investment
        if (this.clientPreferences.getLengthOfInvestment().equals("Long")) {
            score += 3;
        } else if (this.clientPreferences.getLengthOfInvestment().equals("Medium")) {
            score += 2;
        } else if (this.clientPreferences.getLengthOfInvestment().equals("Short")) {
            score += 1;
        }

        // Adjust for percentage of spend
        if(this.clientPreferences.getPercentageOfSpend() != null) {
        	switch (this.clientPreferences.getPercentageOfSpend()) {
            case "Tier1":
//            	Less than 25%
                score += 1;
                break;
            case "Tier2":
//            	Between 26-50%
                score += 2;
                break;
            case "Tier3":
//            	Between 51-75%
                score += 3;
                break;
            case "Tier4":
//            	Between 76-100%
                score += 4;
                break;
            	
            default:
            	score += 0;
            	break;
        }
        }
        

        // Risk tolerance (assuming trade risk is directly comparable to user risk tolerance)
        // This is a placeholder; actual implementation may vary
        score += Math.max(0, this.clientPreferences.getRiskTolerance() - 3); // Simple example, adjust as needed

        //System.out.println("Score -> " + score);
        return score;
    }


}
