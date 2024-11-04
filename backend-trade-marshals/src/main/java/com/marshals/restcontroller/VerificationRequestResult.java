package com.marshals.restcontroller;

public class VerificationRequestResult {
	private boolean isVerified;
	
	public VerificationRequestResult () {}
	
	public VerificationRequestResult(boolean isVerified) {
		this.isVerified = isVerified;
	}

	public boolean getIsVerified() {
		return isVerified;
	}
	
}
