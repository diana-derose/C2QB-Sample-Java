package com.intuit.developer.sampleapp.c2qb.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.developer.sampleapp.c2qb.domain.CompanyConfig;
import com.intuit.developer.sampleapp.c2qb.repository.CompanyConfigRepository;
import com.intuit.ia.connection.IAPlatformClient;
import com.intuit.ia.exception.OAuthException;

/**
 * @author dderose
 *
 */
@RestController
public class OAuthController {

    @Autowired
    private CompanyConfigRepository companyConfigRepository;
    
    private static final String cosumerKey = "insert consumer key"; 
    private static final String cosumerSecret = "insert consumer secret";

    /*
     * This is the first REST endpoint invoked in the OAuth 1.0 flow.
	 *
	 * It is redirected to by Intuit, when the user clicks on the 'Connect
	 * to Quick Books' button from the Setup Page to get the 'Request Token'.
	 *
	 * In other words, the URL required to invoke this method should be put in the 'Connect To QuickBooks' button's
	 * "grantUrl" parameter.
	 *
	 * For example in your Javascript you would do the following:
	 *
	 * intuit.ipp.anywhere.setup({grantUrl: https://localhost:8080/request_token?appCompanyId=<companyIdInYourApp>});
	 */

    @RequestMapping(value = "/request_token", method = RequestMethod.GET)
    public void requestOAuthToken(final HttpServletResponse response,
                                  @RequestParam(value = "appCompanyId", required = true) String companyId) throws IOException {

        //Instantiate the QuickBook SDK's IAPlatformClient object
        IAPlatformClient client = new IAPlatformClient();
        try {
            //Use the IAPlatformClient (from the SDK) to get a Request Token and Request Token Secret from Intuit
            final Map<String, String> requestTokenAndSecret = client.getRequestTokenAndSecret(cosumerKey, cosumerSecret);

            //Pull the values out of the map
            final String requestToken = requestTokenAndSecret.get("requestToken");
            final String requestTokenSecret = requestTokenAndSecret.get("requestTokenSecret");

            //Persist the request token and request token secret in the app database on the given company, we will need it later
            setRequestTokenValuesForCompany(companyId, requestToken, requestTokenSecret);

            // Retrieve the Authorize URL
            final String authURL = client.getOauthAuthorizeUrl(requestToken);

            // Redirect to the authorize URL
            response.sendRedirect(authURL);

        } catch (OAuthException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * This is the second REST endpoint invoked in the OAuth 1.0 flow.
     *
     * Using information received during the first endpoint (e.g. RequestToken and RequestTokenSecret), it makes a final request to Intuit's OAuth servers to
     * get the AccessToken and AccessTokenSecret. It then persists the AccessToken And AccessTokenSecret on the the sample app's
     * company object using the OAuthInfoProvider.
     *
     * Finally it redirects to the "close.html" page so that the popup, in which the OAuth 1.0 flow took place in, is closed
     * and the parent page is refreshed.
     *
     */
    @RequestMapping(value = "/request_token_ready", method = RequestMethod.GET)
    public void requestTokenReady(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        IAPlatformClient client = new IAPlatformClient();

        final String verifierCode = request.getParameter("oauth_verifier");
        final String realmID = request.getParameter("realmId");
        final String requestToken = request.getParameter("oauth_token");
        
        final CompanyConfig companyConfig = companyConfigRepository.findByRealmId(realmID);

        try {
            final Map<String, String> oAuthAccessToken = client.getOAuthAccessToken(verifierCode, requestToken, companyConfig.getRequestTokenSecret(),
            		cosumerKey, cosumerSecret);

            final String accessToken = oAuthAccessToken.get("accessToken");
            final String accessTokenSecret = oAuthAccessToken.get("accessTokenSecret");

            setAccessTokenForCompany(realmID, accessToken, accessTokenSecret);
            response.sendRedirect(getProtocolHostnameAndPort(request) + "/close");


        } catch (OAuthException e) {
            throw new RuntimeException(e);
        }

    }

    public static String getProtocolHostnameAndPort(final HttpServletRequest request) {
        String protocol = request.getProtocol().split("/")[0].toLowerCase();
        String hostname = request.getServerName();
        int port = request.getServerPort();

        StringBuilder result = new StringBuilder(protocol + "://" + hostname);
        if (port != 80) {
            result.append(":").append(port);
        }

        return result.toString();
    }
    
    public void setRequestTokenValuesForCompany(String appCompanyId, String requestToken, String requestTokenSecret) throws OAuthException {
        CompanyConfig companyConfig = companyConfigRepository.findByRealmId(appCompanyId);
        if (companyConfig == null) {
            companyConfig = new CompanyConfig();
            companyConfig.setRealmId(appCompanyId);
        }
        
        companyConfig.setRequestToken(requestToken);
        companyConfig.setRequestTokenSecret(requestTokenSecret);
        companyConfigRepository.save(companyConfig);
    }
    
    public void setAccessTokenForCompany(String appCompanyId, String accessToken, String accessTokenSecret) throws OAuthException {
    	final CompanyConfig companyConfig = companyConfigRepository.findByRealmId(appCompanyId);

        if (companyConfig == null) {
            throw new OAuthException("Could not find a company with an id of " + appCompanyId);
        }
     
        companyConfig.setAccessToken(accessToken);
        companyConfig.setAccessTokenSecret(accessTokenSecret);
        
        companyConfigRepository.save(companyConfig);

    }

}
