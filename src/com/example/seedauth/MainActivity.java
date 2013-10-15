package com.example.seedauth;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.example.seedauth.AuthManager.AuthGrantor;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

public class MainActivity extends Activity {
	static final String TAG = MainActivity.class.getSimpleName();
	
	  static final int REQUEST_AUTHORIZATION = 1;

	  static final int REQUEST_ACCOUNT_PICKER = 2;
	
	
	GoogleAccountCredential credential;
	
	AuthManager mAuthMgr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mAuthMgr = AuthManager.getInstance();
//	    credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton("https://www.googleapis.com/auth/userinfo.profile"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	  @Override
	  protected void onResume() {
		  Log.d(TAG, "OLIVER: onResume");
	    super.onResume();
//	    if (checkGooglePlayServicesAvailable()) {
//	      haveGooglePlayServices();
//	    }
	  }
	  
	  Handler h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case AuthManager.GET_TOKEN_COMPLETED:
					break;
				case AuthManager.GET_TOKEN_FAILED:
					break;
				default:
					break;
				}
			}
	  };
	  
	  public void getTokenOnClick(View v) {
		  mAuthMgr.prepareUserToken(this, AuthGrantor.GOOGLE, new Runnable() {
			@Override
			public void run() {
				getUserToken();
			}
		  });
		  Log.d(TAG, "Call to AuthManager getToken done.");
	  }
	  
	  public String getUserToken() {
		  String t = mAuthMgr.getToken();
		  Log.d(TAG, "getUserToken: " + t);
		  return t;
	  }
	  
//	@Override
//	protected void onResume() {
//		super.onResume();
//		
//		checkGooglePlayServicesAvailable();
//	}
	
	  /** Check that Google Play services APK is installed and up to date. */
	  private boolean checkGooglePlayServicesAvailable() {
	    final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
//	      showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
	      return false;
	    }
	    return true;
	  }	
	
	  private void chooseAccount() {
		    startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
	  }	

	  private void haveGooglePlayServices() {
		    // check if there is already an account selected
		  String account = credential.getSelectedAccountName();
		    if (account == null) {
		    	Log.d(TAG, "No account selected!!!");
		      // ask user to choose account
		      chooseAccount();
		    } else {
		    	Log.d(TAG, "Account: " + account);
		      // load calendars
//		      AsyncLoadTasks.run(this);
		    }
	  }	
	  
	  @Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
		  Log.d(TAG, "onActivityResult: " + String.format("requestCode: %s, resultCode: %s, data: %s", requestCode, resultCode, data));
	    
		  
		  mAuthMgr.processActivityResult(requestCode, resultCode, data);
		  
//	    switch (requestCode) {
////	      case REQUEST_GOOGLE_PLAY_SERVICES:
////	        if (resultCode == Activity.RESULT_OK) {
////	          haveGooglePlayServices();
////	        } else {
////	          checkGooglePlayServicesAvailable();
////	        }
////	        break;
////	      case REQUEST_AUTHORIZATION:
////	        if (resultCode == Activity.RESULT_OK) {
////	          AsyncLoadTasks.run(this);
////	        } else {
////	          chooseAccount();
////	        }
////	        break;
//	      case REQUEST_ACCOUNT_PICKER:
//	        if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
//	          String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
//	          if (accountName != null) {
//	            credential.setSelectedAccountName(accountName);
//	            SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
//	            SharedPreferences.Editor editor = settings.edit();
//
//	        	  Log.d(TAG, "Account Selected: " + accountName);
//	        	  
//	        	  getAccessToken();
//	        	  
////	        	  try {
////					Log.d(TAG, "Account Token: " + credential.getToken());
////				} catch (IOException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				} catch (GoogleAuthException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
//	            
//	            
////	            editor.putString(PREF_ACCOUNT_NAME, accountName);
////	            editor.commit();
////	            AsyncLoadTasks.run(this);
//	          }
//	        }
//	        break;
//	    }
	  }	 
	  
	  private void getAccessToken() {
		  AsyncTask<GoogleAccountCredential, Void, String> task = new AsyncTask<GoogleAccountCredential, Void, String>() {
			@Override
			protected String doInBackground(GoogleAccountCredential... arg0) {
				String token = "";
				try {
					token = credential.getToken();//((GoogleAccountCredential) arg0[0]).getToken();
					Log.d(TAG, "Google account token: " + token);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (GoogleAuthException e) {
					e.printStackTrace();
				}
				return token;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				Log.d(TAG, "onPostExecute: Google account token: " + result);
			}
		  };
		  
		  task.execute(credential);
	  }
	  
}
