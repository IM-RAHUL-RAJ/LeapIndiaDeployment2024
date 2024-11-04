package com.marshals.restcontroller;

import com.marshals.business.Holding;
import com.marshals.business.TradeHistory;
import com.marshals.business.TradePL;
import com.marshals.business.services.ActivityReportService;
import com.marshals.integration.DatabaseException;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/activity-report")
public class ActivityReportController {
	
	@Autowired
	private Logger logger;
	
    private ActivityReportService activityReportService;

    public ActivityReportController(@Qualifier("activityReportService") ActivityReportService activityReportService) {
        this.activityReportService = activityReportService;
    }

    // Generate holdings report
    @GetMapping(value = "/holdings/{clientId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Holding>> generateHoldingsReport(@PathVariable String clientId) {
        try {
            List<Holding> holdings = activityReportService.generateHoldingsReport(clientId);
            if(holdings == null || holdings.isEmpty()) {
            	throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Client has no holdings");
            }
            return ResponseEntity.ok(holdings);
        } catch(ResponseStatusException e) {
			logger.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, e.getLocalizedMessage());
		} catch(NullPointerException e) {
			logger.error(e.getMessage());
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
		} catch(DatabaseException e) {
			logger.error(e.getMessage());
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getLocalizedMessage());
		} catch(RuntimeException e) {
			logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
    }

    // Generate trade report
    @GetMapping(value = "/trades/{clientId}" , produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<TradeHistory> generateTradeReport(@PathVariable String clientId) {
        try {
            TradeHistory tradeHistory = activityReportService.generateTradeReport(clientId);
            if(tradeHistory.getTrades() == null || tradeHistory.getTrades().isEmpty()) {
            	throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Client has no trade history");
            }
            return ResponseEntity.ok(tradeHistory);
        } catch(ResponseStatusException e) {
			logger.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, e.getLocalizedMessage());
		} catch (NullPointerException e) {
        	logger.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        } catch(DatabaseException e) {
			logger.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
		} catch(RuntimeException e) {
			logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
    }

    // Generate P&L report
    @GetMapping(value = "/pl/{clientId}" , produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<TradePL>> generatePLReport(@PathVariable String clientId) {
        try {
            List<TradePL> profitLossMap = activityReportService.generatePLReport(clientId);
            if(profitLossMap == null || profitLossMap.isEmpty()) {
            	throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Client has no holdings for pl report");
            }
            return ResponseEntity.ok(profitLossMap);
        } catch (ResponseStatusException e) {
        	logger.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, e.getLocalizedMessage());
        } catch (NullPointerException e) {
        	logger.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        } catch(DatabaseException e) {
			logger.error(e.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
		} catch(RuntimeException e) {
			logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
    }
}
