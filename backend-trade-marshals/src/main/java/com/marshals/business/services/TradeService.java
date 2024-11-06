package com.marshals.business.services;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.marshals.business.ClientPortfolio;
import com.marshals.business.ClientPreferences;
import com.marshals.business.FMTSValidatedClient;
import com.marshals.business.Holding;
import com.marshals.business.Order;
import com.marshals.business.Price;
import com.marshals.business.Instrument;
import com.marshals.business.Trade;
import com.marshals.business.TradeHistory;
import com.marshals.integration.ClientTradeDao;
import com.marshals.integration.DatabaseException;
import com.marshals.integration.FMTSException;
import com.marshals.utils.PriceScorer;

@Service("tradeService")
public class TradeService {
	@Autowired
	private Logger logger;
	
	private PortfolioService portfolioService;
	
	private ClientTradeDao dao; //Client Trade Dao that interacts with DB
	
	private List<Price> priceList;
	
	private FMTSService fmtsService;

	public TradeService() {}

	@Autowired
	public TradeService(@Qualifier("clientTradeDao") ClientTradeDao dao, @Qualifier("portfolioService") PortfolioService portfolioService,  @Qualifier("fmtsService") FMTSService fmtsService) {
		//Initializing Portfolio Service
//		this.portfolioService = new PortfolioService(dao); // For getting and updating client portfolio
		this.dao = dao;
		this.portfolioService = portfolioService;
		this.fmtsService = fmtsService;
		try {
			this.priceList = fmtsService.getLivePrices(); //Get Live Prices from FMTSService
		} catch(FMTSException e) {
			//Mock price list
			this.priceList = new ArrayList<>();
			this.priceList.add(new Price(new BigDecimal("104.75"), new BigDecimal("104.25"),
					"21-AUG-19 10.00.01.042000000 AM GMT", new Instrument("N123456", "CUSIP", "46625H100", "STOCK",
							"JPMorgan Chase & Co. Capital Stock", 1000, 1)));
 
			this.priceList.add(new Price(new BigDecimal("312500"), new BigDecimal("312000"),
					"21-AUG-19 05.00.00.040000000 AM -05:00",
					new Instrument("N123789", "ISIN", "US0846707026", "STOCK", "Berkshire Hathaway Inc. Class A", 10, 1)));
 
			this.priceList.add(new Price(new BigDecimal("95.92"), new BigDecimal("95.42"), "21-AUG-19 10.00.02.042000000 AM GMT",
					new Instrument("C100", "CUSIP", "48123Y5A0", "CD", "JPMorgan Chase Bank, National Association 01/19",
							1000, 100)));
 
			this.priceList.add(new Price(new BigDecimal("1.03375"), new BigDecimal("1.03390625"),
					"21-AUG-19 10.00.02.000000000 AM GMT",
					new Instrument("T67890", "CUSIP", "9128285M8", "GOVT", "USA, Note 3.125 15nov2028 10Y", 10000, 100)));
 
			this.priceList.add(new Price(new BigDecimal("0.998125"), new BigDecimal("0.99828125"),
					"21-AUG-19 10.00.02.002000000 AM GMT",
					new Instrument("T67894", "CUSIP", "9128285Z9", "GOVT", "USA, Note 2.5 31jan2024 5Y", 10000, 100)));
 
			this.priceList.add(new Price(new BigDecimal("1"), new BigDecimal("1.00015625"),
					"21-AUG-19 10.00.02.002000000 AM GMT",
					new Instrument("T67895", "CUSIP", "9128286A3", "GOVT", "USA, Note 2.625 31jan2026 7Y", 10000, 100)));
 
			this.priceList.add(new Price(new BigDecimal("0.999375"), new BigDecimal("0.999375"),
					"21-AUG-19 10.00.02.002000000 AM GMT",
					new Instrument("T67897", "CUSIP", "9128285X4", "GOVT", "USA, Note 2.5 31jan2021 2Y", 10000, 100)));
 
			this.priceList.add(new Price(new BigDecimal("0.999375"), new BigDecimal("0.999375"),
					"21-AUG-19 10.00.02.002000000 AM GMT",
					new Instrument("T67899", "CUSIP", "9128285V8", "GOVT", "USA, Notes 2.5% 15jan2022 3Y", 10000, 100)));
 
			this.priceList.add(new Price(new BigDecimal("1.00375"), new BigDecimal("1.00375"),
					"21-AUG-19 10.00.02.002000000 AM GMT",
					new Instrument("T67880", "CUSIP", "9128285U0", "GOVT", "USA, Note 1.5 31dec2023 5Y", 10000, 100)));
 
			this.priceList.add(new Price(new BigDecimal("1.0596875"), new BigDecimal("1.0596875"),
					"21-AUG-19 10.00.02.002000000 AM GMT",
					new Instrument("T67883", "CUSIP", "912810SE9", "GOVT", "USA, Bond 3.375 15nov2048 30Y", 10000, 100)));
 
			this.priceList.add(new Price(new BigDecimal("0.9853125"), new BigDecimal("0.98546875"),
					"21-AUG-19 10.00.02.002000000 AM GMT",
					new Instrument("T67878", "CUSIP", "912810SD1", "GOVT", "USA, Bond 3 15aug2048 30Y", 10000, 100)));
 
			this.priceList.add(new Price(new BigDecimal("1162.42"), new BigDecimal("1161.42"),
					"21-AUG-19 06.52.20.350000000 PM AMERICA/NEW_YORK",
					new Instrument("Q123", "CUSIP", "02079K107", "STOCK", "Alphabet Inc. Class C Capital Stock", 1000, 1)));
 
			this.priceList.add(new Price(new BigDecimal("323.39"), new BigDecimal("322.89"),
					"21-AUG-19 06.52.20.356000000 PM AMERICA/NEW_YORK",
					new Instrument("Q456", "CUSIP", "88160R101", "STOCK", "Tesla, Inc. Common Stock", 1000, 1)));
		}
	}
   
	public List<Price> getPriceList() {
		return priceList;
	}
	
	//Adding the trade to the DB
	public void addTrade(Trade trade) {
		if(trade == null) {
			throw new NullPointerException("trade must not be null");
		}
		 try {
			 dao.addTrade(trade);
		 } catch(DatabaseException e) {
			 throw e;
		 } catch(NullPointerException e) {
			 throw e;
		 }
	}
	
	//Executing the trade
    public Trade executeTrade(Order order) {
    	try {
    		if (order == null) {
                throw new NullPointerException("order cannot be null");
            }
        	        
        	ClientPortfolio clientPortfolio = portfolioService.getClientPortfolio(order.getClientId());
        	
        	if(order.getDirection().equals("B")) {
        		for(Price price: priceList) {
            		if(price.getInstrument().getInstrumentId().equals(order.getInstrumentId())) {
            			//Call FMTS Service to create the trade
            			Trade trade;
    					
						trade = fmtsService.createTrade(order);
    						
        				trade.getOrder().setOrderId(order.getOrderId());
        				System.out.println(trade);
        				//Buy Condition Validation
        				//Getting the cost of trade and checking if its lesser than or equal to balance
        				BigDecimal totalCostOfTrade = trade.getCashValue();
    	                if (clientPortfolio.getCurrBalance().compareTo(totalCostOfTrade) >= 0) {	
	        				//Updating portfolio and adding trade
    	                	this.addTrade(trade);
	        				portfolioService.updateClientPortfolio(trade);	
    	                } else {
    	                    throw new IllegalArgumentException("Insufficient balance! Cannot buy the instrument");
    	                }
    	                return trade;
            		}
            	}
        	} else if (order.getDirection().equals("S")){
        		for(Holding holding: clientPortfolio.getHoldings()) {
        			//Sell condition checking
        			if(holding.getInstrumentId().equals(order.getInstrumentId())) {
        				//One more sell validation checking - To check if user has more quantity to sell
        				if(holding.getQuantity() >= order.getQuantity()) {
        					//Call FMTS Service to create the trade
        					Trade trade;
        					
    						trade = fmtsService.createTrade(order);
    						System.out.println("Trade Object: "+ trade);
        						
            				trade.getOrder().setOrderId(order.getOrderId());
            				System.out.println(trade);
            				
            				//Updating portfolio and adding Trade 
            				this.addTrade(trade);
            				portfolioService.updateClientPortfolio(trade);	
                			return trade;
        				} else {
        					throw new IllegalArgumentException("Insufficient quantity in holdings to sell the instrument");
        				}
        			}
        		}
        		//If above loop was completely executed, instrument wasnt part of holdings - SELL CONDITION VALIDATION
        		 throw new IllegalArgumentException("Instrument not part of holdings! Cannot sell the instrument");
        	} else {
        		throw new IllegalArgumentException("Order direction is invalid");
        	}    
            throw new IllegalArgumentException("Instrument is not present in the platform");
    	} catch(NullPointerException e) {
    		logger.error(e.getMessage());
    		throw new NullPointerException(e.getMessage());
    	} catch(IllegalArgumentException e) {
    		logger.error(e.getMessage());
    		throw new IllegalArgumentException(e.getMessage());
    	} catch(DatabaseException e) {
			logger.error(e.getMessage());
			throw new DatabaseException(e.getMessage());
		} catch(FMTSException e) {
			logger.error(e.getMessage());
			throw new FMTSException(e.getMessage());
		}
    	
    }
     
    //Get Trade History
    public TradeHistory getClientTradeHistory(String clientId) {
    	TradeHistory tradeHistory = null;
    	try {
    		 if (clientId == null) {
    	            throw new NullPointerException("Client ID must not be null");
    	        }
    	        tradeHistory = dao.getClientTradeHistory(clientId);
    	}catch (NullPointerException e) {
    		throw e;
    	}
       
        return tradeHistory;
 
    }
    
//    --------------------------------ROBO ADVISOR-------------------------------------------
    
    public List<Price> recommendTopBuyInstruments(ClientPreferences preferences){
    	try {
    		BigDecimal currBalance = portfolioService.getClientPortfolio(preferences.getClientId()).getCurrBalance();
    		if(preferences.getAcceptAdvisor()=="false") 
    			throw new UnsupportedOperationException("Cannot recommend with robo advisor without accepting to it");
    	    PriceScorer scorer = new PriceScorer(preferences);
            List<Price> recommendedPrice = new ArrayList<Price>();
            //System.out.println("Instruments before sorting -> " + priceList);
            for(Price trade: priceList) {
            	if(calculateScore(trade, preferences).compareTo(new BigDecimal(scorer.calculateScore()).divide(new BigDecimal(25))) < 0) {
            		if(currBalance.subtract(trade.getBidPrice()).compareTo(BigDecimal.ZERO)<0) //Buy Condition - Not enough balance
            			continue;
            		recommendedPrice.add(trade);
            	}
            }
            
            //System.out.println("Instruments after sorting -> " + recommendedPrice.toString());
            
            // Return top 5 trades or fewer if there aren't enough trades
            return priceList.size() > 5 ? recommendedPrice.subList(0, 5) : recommendedPrice;
    	} catch(UnsupportedOperationException e) {
    		throw e;
    	}
    }
    
    public List<Price> recommendTopSellInstruments(ClientPreferences preferences){
    	try {
    		List<Holding> userHoldings = portfolioService.getClientPortfolio(preferences.getClientId()).getHoldings();
    		if(preferences.getAcceptAdvisor()=="false") 
    			throw new UnsupportedOperationException("Cannot recommend with robo advisor without accepting to it");
    		List<Holding> topSellTradesInHoldings = new ArrayList<>();
        	if(userHoldings.size() <= 5) {
//        		return everything
        		topSellTradesInHoldings = userHoldings; 
        	} else {
        		for (int i = 0; i < 5; i++) 
                {
                   // generating the index using Math.random()
                    int index = (int)(Math.random() * userHoldings.size());
                    topSellTradesInHoldings.add(userHoldings.get(index));  
                }
        	}
        	//Retrieve the list of prices corresponding to the holdings
        	List<Price> topSellTrades = new ArrayList<>();
        	for(Holding holding:topSellTradesInHoldings) {
        		for(Price trade:priceList) {
        			if(holding.getInstrumentId().equals(trade.getInstrument().getInstrumentId())) {
        				topSellTrades.add(trade);
        				break;
        			}
        		}
        	}
        	return topSellTrades;
    	} catch(UnsupportedOperationException e) {
    		throw e;
    	}	
    }
    
    
    public BigDecimal calculateScore(Price trade, ClientPreferences client) {
        BigDecimal bidAskSpread = (trade.getAskPrice().subtract(trade.getBidPrice())).setScale(4, RoundingMode.HALF_UP);
        BigDecimal price = trade.getAskPrice().setScale(4,RoundingMode.HALF_UP);
        BigDecimal score = BigDecimal.ZERO;
        BigDecimal value = new BigDecimal(1000);
        
        score = bidAskSpread.abs();
        
        if(bidAskSpread.abs().compareTo(new BigDecimal(1)) > 0) {
        	score = bidAskSpread.divide(new BigDecimal(1000));
        }

        // Calculate base score from bid-ask spread and price
//        score = value.multiply( bidAskSpread.abs()); // Simple scoring example
        
//        score = score.multiply(new BigDecimal(client.getRiskTolerance()).divide(new BigDecimal(5))); // Scale by risk tolerance
        // Adjust score based on client preferences

        //System.out.println("Score for trade - " + trade.getInstrument().getInstrumentDescription() + " , Score -> " + score);
        return score;
    }

    
}