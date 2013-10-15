/**
 * 
 */
package com.example.seedauth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.seedauth.AuthManager.AuthGrantor;

/**
 * Authentication manager activity
 * 
 * @author olreyes
 */
public class AuthManagerActivity extends Activity {

	private static final String AUTH_GRANTOR = "authGrantor";

	private GoogleAuthenticator mGoogleAuth;
	private FacebookAuthenticator mFacebookAuth;
	
	private AuthGrantor mGrantor;
	
	public static void start(final Context ctx, final AuthGrantor grantor) {
		Intent intent = new Intent(ctx, AuthManagerActivity.class);
		intent.putExtra(AUTH_GRANTOR, grantor);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mGrantor = AuthGrantor.GOOGLE;//(AuthGrantor) getIntent().getSerializableExtra(AUTH_GRANTOR);
		
		if (AuthGrantor.GOOGLE.equals(mGrantor)) {
//			mGoogleAuth = new GoogleAuthenticator(this);
		} else if (AuthGrantor.FACEBOOK.equals(mGrantor)) {
//			mFacebookAuth = new FacebookAuthenticator(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGoogleAuth.chooseAccount();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mGoogleAuth.processActivityResult(requestCode, resultCode, data);
	}
}
