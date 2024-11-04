package com.marshals.integration;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.marshals.business.Holding;
import com.marshals.integration.mapper.ClientActivityReportMapper;


@Repository("clientActivityReportDao")
public class ClientActivityReportDaoImpl implements ClientActivityReportDao {
	
	@Autowired
	 private ClientActivityReportMapper clientActivityReportMapper;

	@Autowired
	private Logger logger;
	
	@Override
	public List<Holding> getClientHoldings(String clientId) {
		List<Holding> holdings = new ArrayList<>();
		try {
			logger.debug("enter");
			holdings = clientActivityReportMapper.getClientHoldings(clientId);
			if(holdings.isEmpty()) {
				throw new DatabaseException("client has no holdings");
			}
			return holdings;
		} catch(DataAccessException e) {
		 	logger.error(e.getMessage());
			throw new DatabaseException(e.getMessage());
		} catch(DatabaseException e) {
			logger.error(e.getMessage());
			throw new DatabaseException(e.getMessage());
		}
	}
	

}
