/**
 * 
 */
package com.example.seedauth;

import android.content.Intent;

/**
 * Authenticator interface
 * 
 * @author olreyes
 */
public interface Authenticator {
	
	public interface AuthenticatorListener {
		void onAccessTokenReady(String accessToken);
	}
	
	void processActivityResult(int requestCode, int resultCode, Intent data);
	
	
}
