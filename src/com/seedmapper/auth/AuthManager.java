/**
 * 
 */
package com.seedmapper.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.seedmapper.auth.Authenticator.AuthenticatorListener;


/**
 * Authentication manager
 * 
 * @author olreyes
 */
public class AuthManager implements AuthenticatorListener, Parcelable {
    private static final String TAG = AuthManager.class.getSimpleName();

    public static final String SHARED_PREF = "authSharedPref";
    public static final String ACCOUNT_NAME = "accountName";
    public static final String ACCOUNT_GRANTOR_TYPE = "accountNameGrantor";

    private static Activity mAct;
    private Runnable mClientRunnable;
    private GetAccessTokenListener mListener;
    private Authenticator mAuth;

    private String mAccount;
    private String mAccessToken;
    private String mIDToken;
    private String mAuthorizationToken;
    private GrantorType mGrantor;
    

    private SharedPreferences settings;
    
    
    public interface GetAccessTokenListener {
        void onSuccess(String accessToken);
    }
    public interface GetIDTokenListener {
        void onSuccess(String idToken);
    }
    public interface GetAuthorizationTokenListener {
        void onSuccess(String authtoken);
    }
    
    public AuthManager(Context ctx) {
        mAccessToken = "";
        mIDToken = "";
        mAuthorizationToken = "";
        
        // Getting stored account and grantor type if exists.
        settings = ctx.getSharedPreferences(AuthManager.SHARED_PREF, Context.MODE_PRIVATE);
        mAccount = settings.getString(ACCOUNT_NAME, "");
        String grantor = settings.getString(ACCOUNT_GRANTOR_TYPE, "");
        mGrantor = (TextUtils.isEmpty(grantor)) ? GrantorType.UNKNOWN : GrantorType.valueOf(grantor);
    }

    private void persistAccountSelected(String account, GrantorType grantor) {
        // Persists selected account.
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(AuthManager.ACCOUNT_NAME, account);
        editor.putString(AuthManager.ACCOUNT_GRANTOR_TYPE, grantor.name());
        editor.commit();
    }
    
//    public String getAccessToken() {
//        // For debug purpose, clearing up access token in AuthManager.
//        String temp = mAccessToken;
//        mAccessToken = "";
//        return temp;
//    }
    

    public void getToken(final Activity act, final GrantorType grantor, final TokenType tokenType, final GetAccessTokenListener listener) {
        mAct = act;
        mAuth = new GoogleAuthenticator(tokenType, act, this);
        
        if (TextUtils.isEmpty(mAccount)) {
            Log.d(TAG, "No account currently selected. Have the user select an account.");
            mAuth.chooseAccount();
        }
        else {
            Log.d(TAG, "Get token");
            mAuth.getToken(mAccount);
        }
    }    

    public void getAccessToken(final Activity act, final GrantorType grantor, final GetAccessTokenListener listener) {
        mAct = act;
        mAuth = new GoogleAuthenticator(act, this);
        
        if (TextUtils.isEmpty(mAccount)) {
            Log.d(TAG, "No account currently selected. Have the user select an account.");
            mAuth.chooseAccount();
        }
        else if (TextUtils.isEmpty(mAccessToken)) {
            Log.d(TAG, "No access token available yet. Get access token");
            mAuth.getToken(mAccount);
        }
        else {
            Log.d(TAG, "Access token is available.");
            listener.onSuccess(mAccessToken);
        }
    }
    
    public void getIDToken(final Activity act, final GrantorType grantor, final GetIDTokenListener listener) {
        mAct = act;
        mAuth = new GoogleAuthenticator(act, this);
        
        if (TextUtils.isEmpty(mAccount)) {
            Log.d(TAG, "No account currently selected. Have the user select an account.");
            mAuth.chooseAccount();
        }
        else if (TextUtils.isEmpty(mAccessToken)) {
            Log.d(TAG, "No ID token available yet. Get ID token");
            mAuth.getToken(mAccount);
        }
        else {
            Log.d(TAG, "ID token is available.");
            listener.onSuccess(mAccessToken);
        }
    } 
    
    public void getAuthorizationToken(final Activity act, final GrantorType grantor, final GetAuthorizationTokenListener listener) {
        mAct = act;
        mAuth = new GoogleAuthenticator(act, this);
        
        if (TextUtils.isEmpty(mAccount)) {
            Log.d(TAG, "No account currently selected. Have the user select an account.");
            mAuth.chooseAccount();
        }
        else if (TextUtils.isEmpty(mAccessToken)) {
            Log.d(TAG, "No Auth token available yet. Get ID token");
            mAuth.getToken(mAccount);
        }
        else {
            Log.d(TAG, "Auth token is available.");
            listener.onSuccess(mAccessToken);
        }
    }     
    
    
//    /**
//     * Initializes user access token based on the GrantorType selected.
//     * 
//     * @param act - Activity that will host any Activity and/or Dialog the authenticator would start.
//     * @param grantor - The type of authenticator used.
//     * @param runnable - This will be executed once the initialization completes.
//     */
//    public void initUserToken(final Activity act, final GrantorType grantor, final AuthManagerListener listener) {
//        mAct = act;
//        mListener = listener;
//
//        if (TextUtils.isEmpty(mAccount)) {
//            Log.d(TAG, "No account currently selected. Have the user select an account.");
//            mAuth = new GoogleAuthenticator(act, this);
//            mAuth.chooseAccount();
//        } 
//        else if (TextUtils.isEmpty(mAccessToken)) {
//            Log.d(TAG, "No access token available yet. Get access token");
//            mAuth = new GoogleAuthenticator(act, this);
//            mAuth.getToken(mAccount);
//        }
//        else {
//            Log.d(TAG, "Access token is available.");
//            mListener.onAccessTokenInitComplete();
//        }
//    }
    
//    /**
//     * Initializes user access token based on the GrantorType selected.
//     * 
//     * @param act - Activity that will host any Activity and/or Dialog the authenticator would start.
//     * @param grantor - The type of authenticator used.
//     * @param runnable - This will be executed once the initialization completes.
//     */
//    public void initUserToken(final Activity act, final GrantorType grantor, final Runnable runnable) {
//        mAct = act;
//        mClientRunnable = runnable;
//
//        if (TextUtils.isEmpty(mAccount)) {
//            Log.d(TAG, "No account currently selected. Have the user select an account.");
//            mAuth = new GoogleAuthenticator(act, this);
//            mAuth.chooseAccount();
//        } else if (TextUtils.isEmpty(mAccessToken)) {
//            Log.d(TAG, "No access token available yet. Get access token");
//            mAuth = new GoogleAuthenticator(act, this);
//            mAuth.getToken(mAccount);
//        }
//        else {
//            Log.d(TAG, "Access token is available.");
//            Handler handler = act.getWindow().getDecorView().getHandler();
//            handler.post(runnable);
//        }
//    }    
    
    @Override
    public void onAccessTokenReady(String accessToken, String account) {
        mAccessToken = accessToken;
        mAccount = account;
        persistAccountSelected(mAccount, mGrantor);
        Handler handler = mAct.getWindow().getDecorView().getHandler();
        handler.post(mClientRunnable);
    }

    public void processActivityResult(int requestCode, int resultCode, Intent data) {
        mAuth.processActivityResult(requestCode, resultCode, data);
    }

    public enum GrantorType {
        UNKNOWN,
        GOOGLE,
        FACEBOOK;
    }

    
    /*******************
     * Parcelable implementations and related methods.
     *******************/
    
    public AuthManager(Parcel in) {
        super();
        readFromParcel(in);
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAccessToken);
        dest.writeString(mAccount);
        dest.writeString(mGrantor.name());
    }

    private void readFromParcel(Parcel in) {
        mAccessToken = in.readString();
        mAccount = in.readString();
        mGrantor = GrantorType.valueOf(in.readString());
    }

    public static final Parcelable.Creator<AuthManager> CREATOR = new Parcelable.Creator<AuthManager>() {
        public AuthManager createFromParcel(Parcel in) {
            return new AuthManager(in);
        }

        public AuthManager[] newArray(int size) {
            return new AuthManager[size];
        }
    };
    
}
