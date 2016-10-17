package com.intuit.developer.sampleapp.c2qb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.intuit.developer.sampleapp.c2qb.domain.CompanyConfig;
import com.intuit.developer.sampleapp.c2qb.repository.CompanyConfigRepository;

/**
 * @author dderose
 *
 */
@Controller
public class HomeController {
	
	@Autowired
    private CompanyConfigRepository companyConfigRepository;

	@RequestMapping("/home")
	public String home() {
		return "index";
	}
	
	@RequestMapping("/close")
	public String close() {
		return "close";
	}
	
    @RequestMapping(value = "/companyConfigs", method = RequestMethod.GET)
    public ResponseEntity<Iterable<CompanyConfig>> listAllRealmConfigs() {
    	
    	Iterable<CompanyConfig> log = companyConfigRepository.findAll();
    	
    	return new ResponseEntity<>(log, HttpStatus.OK);
    }

}
