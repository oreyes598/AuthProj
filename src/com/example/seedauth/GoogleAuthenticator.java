package com.example.seedauth;

import java.io.IOException;
import java.util.Collections;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

/**
 * Authentication using Google account.
 * 
 * @author olreyes
 */
public class GoogleAuthenticator implements Authenticator {
	private static final String TAG = GoogleAuthenticator.class.getSimpleName();

	private static final int REQUEST_AUTHORIZATION = 101;
	private static final int REQUEST_ACCOUNT_PICKER = 102;
	
	private Activity mAct;
	private GoogleAccountCredential credential;
	
	private SharedPreferences settings;
	
	private AuthenticatorListener mListener;
	
	public GoogleAuthenticator(final Activity act, final AuthenticatorListener listener) {
		super();
		mAct = act;
		mListener = listener;
	    credential = GoogleAccountCredential.usingOAuth2(act, Collections.singleton("https://www.googleapis.com/auth/userinfo.profile"));
	    settings = act.getSharedPreferences(AuthManager.SHARED_PREF, Context.MODE_PRIVATE);
//	    credential.setSelectedAccountName(settings.getString(AuthManager.PREF_ACCOUNT_NAME, null));	    
	}

	public void chooseAccount() {
		if (credential.getSelectedAccount() == null && checkGooglePlayServicesAvailable()) {
			Log.d(TAG, "No account currently selected. Starting Google account selector activity.");
			mAct.startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
		}
	}	
	
//	  private void haveGooglePlayServices() {
//		    // check if there is already an account selected
//		    if (credential.getSelectedAccountName() == null) {	

//	  private void haveGooglePlayServices() {
//		    // check if there is already an account selected
//		    if (credential.getSelectedAccountName() == null) {
//		      // ask user to choose account
//		      chooseAccount();
//		    }
//	  }
//	
//	
//	  /** Check that Google Play services APK is installed and up to date. */
//	  private boolean checkGooglePlayServicesAvailable() {
//	    final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//	    if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
////	      showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
//	      return false;
//	    }
//	    return true;
//	  }	
//	  
//	  private void haveGooglePlayServices() {
//		    // check if there is already an account selected
//		  String account = credential.getSelectedAccountName();
//		    if (account == null) {
//		    	Log.d(TAG, "No account selected!!!");
//		      // ask user to choose account
//		      chooseAccount();
//		    } else {
//		    	Log.d(TAG, "Account: " + account);
//		      // load calendars
////		      AsyncLoadTasks.run(this);
//		    }
//	  }		
	
	
	
	
	@Override
	  public void processActivityResult(int requestCode, int resultCode, Intent data) {
	    switch (requestCode) {
//	      case REQUEST_GOOGLE_PLAY_SERVICES:
//	        if (resultCode == Activity.RESULT_OK) {
//	          haveGooglePlayServices();
//	        } else {
//	          checkGooglePlayServicesAvailable();
//	        }
//	        break;
	      case REQUEST_AUTHORIZATION:
	        if (resultCode == Activity.RESULT_OK) {
	        	Log.d(TAG, "Authorization approved!!!");
	        	getAccessToken();
//	          AsyncLoadTasks.run(this);
	        } else {
	          chooseAccount();
	        }
	        break;
		case REQUEST_ACCOUNT_PICKER:
			if (resultCode == Activity.RESULT_OK 
					&& data != null
					&& data.getExtras() != null) {
				
				String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
				if (accountName != null) {
					Log.d(TAG, "Google account selected: " + accountName);
					credential.setSelectedAccountName(accountName);
					getAccessToken();

					// Persists selected account.
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(AuthManager.PREF_ACCOUNT_NAME, accountName);
					editor.commit();
				}
			} 
			break;
	    }
	  }
	  
	private void getAccessToken() {
		AsyncTask<GoogleAccountCredential, Void, String> task = new AsyncTask<GoogleAccountCredential, Void, String>() {
			@Override
			protected String doInBackground(GoogleAccountCredential... arg0) {
				String token = "";
				try {
					token = credential.getToken();// ((GoogleAccountCredential) arg0[0]).getToken();
					
				} catch (UserRecoverableAuthException userRecoverableException) {
					Log.d(TAG, "Need to request authorization from user to get access token.");
					mAct.startActivityForResult(userRecoverableException.getIntent(), REQUEST_AUTHORIZATION);
					
				} catch (GoogleAuthException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return token;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				mListener.onAccessTokenReady(result);
				Log.d(TAG, "Google account access token: " + result);
			}
		};

		task.execute(credential);
	}
	  
	  /** Check that Google Play services APK is installed and up to date. */
	  private boolean checkGooglePlayServicesAvailable() {
	    final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mAct);
	    if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
//	      showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
	      return false;
	    }
	    return true;
	  }	  
}
