package com.marshals.integration;

import java.util.List;

import com.marshals.business.Holding;

public interface ClientActivityReportDao {
	
	//Holdings report
	List<Holding> getClientHoldings(String clientId);
	
	
	
}
