package com.seedmapper.auth;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
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

    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 100;
    private static final int REQUEST_GOOGLE_AUTHORIZATION = 101;
    private static final int REQUEST_GOOGLE_ACCOUNT_PICKER = 102;

    private TokenType mTokenType;
    private Activity mAct;
    private GoogleAccountCredential mCredential;
    private AuthenticatorListener mListener;

    private static final String WEB_APP_CLIENT_ID = "436413500880-m735s8nqdsk1n8v5iekkf0m8k9phb27e.apps.googleusercontent.com";
    private static final String GET_AUTHORIZATION_SCOPE_FORMAT = "oauth2:server:client_id:%s:api_scope:%s";
    private static final String GET_ID_TOKEN_SCOPE = "server:client_id:" + WEB_APP_CLIENT_ID; 

    /** List of scopes to get authorization */
    private static final List<String> SCOPES = Arrays.asList(new String[] {
            "https://www.googleapis.com/auth/plus.login",
            "https://www.googleapis.com/auth/glass.timeline"
    });
    
    
    // Oliver App

//    private static final String SCOPE_PLUS_LOGIN = "https://www.googleapis.com/auth/plus.login";
//    private static final String SCOPE_USER_INFO_PROFILE = "https://www.googleapis.com/auth/userinfo.profile";
//    private static final String OAUTH_SCOPE = "server:client_id:" + WEB_APP_CLIENT_ID + ":api_scope:" + SCOPE_USER_INFO_PROFILE; 
    
    // SeedMapper
//    private static final String AUDIENCE_SCOPE = "server:client_id:197444283988-p9prn5dj5gt4r0ppvcbc6n0biej41m2j.apps.googleusercontent.com";
//    private static final String AUDIENCE_SCOPE = "server:client_id:197444283988-7nc05diu34bsf3ca2bvdckibh0rhktk6.apps.googleusercontent.com";

    
    public GoogleAuthenticator(final Activity act, final AuthenticatorListener listener) {
        super();
        mTokenType = TokenType.GOOGLE_ID_TOKEN;
        mAct = act;
        mListener = listener;
      
        /** Use this to get Access Token */
        //mCredential = GoogleAccountCredential.usingOAuth2(act, Collections.singleton("https://www.googleapis.com/auth/userinfo.profile"));

        /** Use this to get Authorization token */
//        mCredential = GoogleAccountCredential.usingOAuth2(act, Collections.singleton(OAUTH_SCOPE));
        
        /** Use this to get ID Token*/
        mCredential = GoogleAccountCredential.usingAudience(act, GET_ID_TOKEN_SCOPE);
     
    }
    
    public GoogleAuthenticator(final TokenType tokenType, final Activity act, final AuthenticatorListener listener) {
        super();
        mTokenType = tokenType;
        mAct = act;
        mListener = listener;

        if (TokenType.GOOGLE_ACCESS_TOKEN.equals(mTokenType)) {
            /** Use this to get Access Token */
            mCredential = GoogleAccountCredential.usingOAuth2(act, Collections.singleton("https://www.googleapis.com/auth/userinfo.profile"));
        }
        else if (TokenType.GOOGLE_ID_TOKEN.equals(mTokenType) 
                || TokenType.GOOGLE_AUTHORIZATION_CODE.equals(mTokenType)) {
            
            /** Use this to get ID Token*/
            mCredential = GoogleAccountCredential.usingAudience(act, GET_ID_TOKEN_SCOPE);

        }
//        else if (TokenType.GOOGLE_AUTHORIZATION_CODE.equals(mTokenType)) {
            /** Use this to get Authorization token */
//          mCredential = GoogleAccountCredential.usingOAuth2(act, Collections.singleton(OAUTH_SCOPE));

//        }      
    }    

    @Override
    public void chooseAccount() {
        if (mCredential.getSelectedAccount() == null && checkGooglePlayServicesAvailable()) {
            Log.d(TAG, "No account currently selected. Starting Google account picker activity.");
            mAct.startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_GOOGLE_ACCOUNT_PICKER);
        }
    }

    @Override
    public void processActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == Activity.RESULT_OK) {
                    chooseAccount();
                }
                else {
                    checkGooglePlayServicesAvailable();
                }
                break;
                
            case REQUEST_GOOGLE_AUTHORIZATION:
                if (resultCode == Activity.RESULT_OK) {
                    String account = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                    Log.d(TAG, String.format("Authorization request approved by user:  %s, proceed getting token.", account));
                    getToken(account);
                }
                break;
                
            case REQUEST_GOOGLE_ACCOUNT_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
                    String account = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                    if (account != null) {
                        Log.d(TAG, String.format("Google account selected: %s, proceed getting token.", account));
                        getToken(account);
                    }
                }
                break;
        }
    }

    
    
    @Override
    public void getToken(String account) {
        mCredential.setSelectedAccountName(account);

        AsyncTask<GoogleAccountCredential, Void, AccountVo> task = new AsyncTask<GoogleAccountCredential, Void, AccountVo>() {
            @Override
            protected AccountVo doInBackground(GoogleAccountCredential... arg0) {
                String token = "";
                AccountVo vo = new AccountVo();
                GoogleAccountCredential acctCred = (GoogleAccountCredential) arg0[0];

                try {
                    if (TokenType.GOOGLE_ACCESS_TOKEN.equals(mTokenType) 
                            || TokenType.GOOGLE_ID_TOKEN.equals(mTokenType)) {
                        token = acctCred.getToken();
                    } 
                    else {
                        String scope = String.format(GET_AUTHORIZATION_SCOPE_FORMAT, WEB_APP_CLIENT_ID, TextUtils.join(" ", SCOPES));
                        Log.d(TAG, "OLIVER: Account is: " + acctCred.getSelectedAccountName());
                        token = GoogleAuthUtil.getToken(mAct, acctCred.getSelectedAccountName(), scope);
                    }

                    vo.setToken(token);
                    vo.setAccount(acctCred.getSelectedAccountName());
                }
                catch (UserRecoverableAuthException userRecoverableException) {
                    Log.d(TAG, "Need to request authorization from user to get token.");
                    mAct.startActivityForResult(userRecoverableException.getIntent(), REQUEST_GOOGLE_AUTHORIZATION);

                }
                catch (GoogleAuthException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                return vo;
            }

            @Override
            protected void onPostExecute(AccountVo result) {
                super.onPostExecute(result);
                mListener.onAccessTokenReady(result.getToken(), result.getAccount());
                Log.d(TAG, String.format("Google account token: %s Account: %s", result.getToken(), result.getAccount()));
            }
        };

        task.execute(mCredential);
    }
    
    /**
     * Check that Google Play services APK is installed and up to date.
     */
    private boolean checkGooglePlayServicesAvailable() {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mAct);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        }
        return true;
    }

    private void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GooglePlayServicesUtil.getErrorDialog(connectionStatusCode, mAct, REQUEST_GOOGLE_PLAY_SERVICES).show();
    }

    private class AccountVo {
        private String mToken;

        /** 
         * Token use to send to server to identify native application.
         * @see <a href="https://developers.google.com/accounts/docs/CrossClientAuth">Google CrossClientAuth</a> 
         */
        private String mIDToken;
        
        /**
         * Authorization code, which can be exchanged for an access token and a refresh token.
         * Send this to the server when the server needs to request data to Google services in behalf of the user. 
         * @see <a href="https://developers.google.com/accounts/docs/CrossClientAuth">Google CrossClientAuth</a> 
         */
        private String mAuthorizationCode;
        
        /**
         * The user's Google account/email.
         */
        private String mAccount;

        public String getToken() {
            return mToken;
        }

        public void setToken(String token) {
            mToken = token;
        }
        
        public String getIDToken() {
            return mIDToken;
        }

        public void setIDToken(String iDToken) {
            mIDToken = iDToken;
        }

        public String getAuthorizationCode() {
            return mAuthorizationCode;
        }

        public void setAuthorizationCode(String authorizationCode) {
            mAuthorizationCode = authorizationCode;
        }

        public String getAccount() {
            return mAccount;
        }

        public void setAccount(String account) {
            mAccount = account;
        }
    }
}
