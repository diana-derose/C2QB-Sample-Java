package com.intuit.developer.sampleapp.c2qb.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Entity to store oauth tokens and other configs for the QBO company
 * 
 * @author dderose
 *
 */
@Entity
public class CompanyConfig {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	private String realmId; //companyId
	
	private String accessToken;
	
	private String accessTokenSecret;
	
	private String requestToken;
    private String requestTokenSecret;
	
	public CompanyConfig(String realmId, String accessToken, String accessTokenSecret, String requestToken, String requestTokenSecret) {
        this.realmId = realmId;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
        this.requestToken = requestToken;
        this.requestTokenSecret = requestTokenSecret;
    }

	public CompanyConfig() {
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRealmId() {
		return realmId;
	}

	public void setRealmId(String realmId) {
		this.realmId = realmId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}

	public void setAccessTokenSecret(String accessTokenSecret) {
		this.accessTokenSecret = accessTokenSecret;
	}

	public String getRequestToken() {
		return requestToken;
	}

	public void setRequestToken(String requestToken) {
		this.requestToken = requestToken;
	}

	public String getRequestTokenSecret() {
		return requestTokenSecret;
	}

	public void setRequestTokenSecret(String requestTokenSecret) {
		this.requestTokenSecret = requestTokenSecret;
	}
	
	
	
}
