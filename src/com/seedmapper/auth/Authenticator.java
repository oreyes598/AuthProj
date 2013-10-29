/**
 * 
 */
package com.seedmapper.auth;

import android.content.Intent;

/**
 * Authenticator interface
 * 
 * @author olreyes
 */
public interface Authenticator {
	
	public interface AuthenticatorListener {
		
	    /**
	     * This method gets called when the access token and account is now available.
	     * 
		 * @param accessToken is the user's access token.
		 * @param account is the user's email address.
		 */
		void onAccessTokenReady(String accessToken, String account);
	}
	
	/**
	 * Processes the results in onActivityResult() call. 
	 * This method should be called in the calling Activity's onActivityResult(). 
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	void processActivityResult(int requestCode, int resultCode, Intent data);
	
	void chooseAccount();
	
	void getToken(String account);
}
