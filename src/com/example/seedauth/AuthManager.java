/**
 * 
 */
package com.example.seedauth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.example.seedauth.Authenticator.AuthenticatorListener;

/**
 * Authentication manager
 * 
 * @author olreyes
 */
public class AuthManager implements AuthenticatorListener {
	private static final String TAG = AuthManager.class.getSimpleName();
	
	public static final String SHARED_PREF = "authSharedPref";
	public static final String PREF_ACCOUNT_NAME = "accountName";
	
	public static final int GET_TOKEN_COMPLETED = 301;
	public static final int GET_TOKEN_FAILED = 302;
	
	
	private static Context sCtx;
	private static Activity sAct;
	private Runnable mClientRunnable;
	
	private GoogleAuthenticator mGoogleAuth;
	private FacebookAuthenticator mFacebookAuth;
	
	private Authenticator mAuth;
	
	/**
	 * Email address of the user account either from Google or Facebook.
	 */
	private String mAccount;
	
	/**
	 * Contains the access token of the user account.
	 */
	private String mAccessToken;
	private AuthGrantor mGrantor;

	private static AuthManager INSTANCE;

	public static AuthManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AuthManager();
		}
		return INSTANCE;
	}

	private AuthManager() {
		mAccessToken = "";
		mGrantor = AuthGrantor.UNKNOWN;
	}
	
//	public void getToken(final Context ctx, final AuthGrantor grantor) {
//		sCtx = ctx;
//		if (TextUtils.isEmpty(mAccessToken)) {
//			Log.d(TAG, "No access token available yet.");
////			 getGrantorToken(grantor);
//			// start AuthManagerActivity
//			// AuthManagerActivity would be the one starting the grantor activity and getting result back.
//			AuthManagerActivity.start(ctx, AuthGrantor.GOOGLE);
//		} else {
//			// return mAccessToken
//		}
//	}

	public String getToken() {
		return mAccessToken;
	}
	
	public void prepareUserToken(final Activity act, final AuthGrantor grantor, final Runnable r) {
		sAct = act;
		mClientRunnable = r;
		
		if (TextUtils.isEmpty(mAccessToken)) {
			Log.d(TAG, "No access token available yet.");
			mAuth = new GoogleAuthenticator(act, this);
			((GoogleAuthenticator)mAuth).chooseAccount();
		} else {
			Log.d(TAG, "Access token is available.");
			Handler handler = act.getWindow().getDecorView().getHandler();
			handler.post(r);
		}
		
	}
	
//	public IAuthenticator getAuthenticator(final AuthGrantor grantor) {
//		if (AuthGrantor.GOOGLE.equals(grantor) {
//			return new GoogleAuthenticator(act);
//		} else if (AuthGrantor.FACEBOOK.equals(grantor)) {
//			return new FacebookAuthenticator(act);
//		}
//		return null;
//	}

	
//	private void getGrantorToken(final AuthGrantor grantor) {
//		if (AuthGrantor.GOOGLE.equals(mGrantor)) {
//			// Get access token from Google Account Manager.
//			mGoogleAuth = new GoogleAuthenticator(sCtx);
//		} else if (AuthGrantor.FACEBOOK.equals(mGrantor)) {
//			// Get access token from Facebook.
//		}
//	}
	
	public void processActivityResult(int requestCode, int resultCode, Intent data) {
		mAuth.processActivityResult(requestCode, resultCode, data);
	}
	
	public enum AuthGrantor {
		UNKNOWN,
		GOOGLE,
		FACEBOOK;
	}

	@Override
	public void onAccessTokenReady(String accessToken) {
		mAccessToken = accessToken;
		Handler handler = sAct.getWindow().getDecorView().getHandler();
		handler.post(mClientRunnable);		
	}
}
