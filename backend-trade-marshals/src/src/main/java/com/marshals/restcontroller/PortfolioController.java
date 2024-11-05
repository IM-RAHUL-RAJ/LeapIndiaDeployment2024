package com.marshals.restcontroller;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerErrorException;

import com.marshals.business.ClientPortfolio;
import com.marshals.business.Trade;
import com.marshals.business.services.PortfolioService;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {
	@Autowired
	private Logger logger;
 
	@Autowired
	private PortfolioService service;
 
	@GetMapping(value="/ping", produces=MediaType.ALL_VALUE)
	public String ping() {
		return "Portfolio web service is alive at " + LocalDateTime.now();
	}
	
	@GetMapping(value="/client/{clientId}", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ClientPortfolio> getPortfolioById(@PathVariable String clientId) {
	    
        ResponseEntity<ClientPortfolio> response;
        try {
        	ClientPortfolio portfolio = service.getClientPortfolio(clientId);
            if(portfolio!=null) {
                response = ResponseEntity.ok(portfolio);
            }else {
                response = ResponseEntity.notFound().build();
            }
        }catch (ResponseStatusException e) {
            logger.error("ResponseStatusException", e);
            throw e;
        }
        catch (RuntimeException e) {
            throw new ServerErrorException("Unable to fetch portfolio with id {clientId}", e);
        }
        return response;
    }
    
}
