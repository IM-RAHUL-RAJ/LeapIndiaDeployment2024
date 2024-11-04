package com.marshals.integration.mapper;
 
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.marshals.business.Order;
import com.marshals.business.Trade;

@Mapper
public interface ClientTradeMapper {

	int addTrade(Trade trade);
	int addOrder(Order order);
	List<Trade> getClientTradeHistory(@Param("clientId") String clientId);
}
