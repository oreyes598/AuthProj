package com.example.seedauth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.seedauth.R;
import com.seedmapper.auth.AuthManager;
import com.seedmapper.auth.AuthManager.GetAccessTokenListener;
import com.seedmapper.auth.AuthManager.GetAuthorizationTokenListener;
import com.seedmapper.auth.AuthManager.GetIDTokenListener;
import com.seedmapper.auth.AuthManager.GrantorType;
import com.seedmapper.auth.TokenType;

public class MainActivity extends Activity {
    static final String TAG = MainActivity.class.getSimpleName();

    private AuthManager mAuthMgr;
    private String mToken;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuthMgr = new AuthManager(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    
    
    public void getToken(GrantorType grantor, TokenType type) {
    }

    public void getTokenOnClick(View v) {
       
        mAuthMgr.getToken(this, GrantorType.GOOGLE, TokenType.GOOGLE_ACCESS_TOKEN, new GetAccessTokenListener() {
            @Override
            public void onSuccess(String token) {
                Log.d(TAG, "Token: " + token);
            }
        });
        
//        mAuthMgr.getAccessToken(this, GrantorType.GOOGLE, new GetAccessTokenListener() {
//            @Override
//            public void onSuccess(String accessToken) {
//                mToken = accessToken;
//                Log.d(TAG, "getAccessToken: " + mToken);
//            }
//        });
//
//        mAuthMgr.getIDToken(this, GrantorType.GOOGLE, new GetIDTokenListener() {
//            @Override
//            public void onSuccess(String idToken) {
//                Log.d(TAG, "getIDToken: " + idToken);
//            }
//        });        
//        
//        mAuthMgr.getAuthorizationToken(this, GrantorType.GOOGLE, new GetAuthorizationTokenListener() {
//            @Override
//            public void onSuccess(String authToken) {
//                Log.d(TAG, "getAuthorizationToken: " + authToken);
//            }
//        });        

        
        
//        token = getUserToken();
        
//        if (TextUtils.isEmpty(token)) {
//            mAuthMgr.initUserToken(this, GrantorType.GOOGLE, new AuthManagerListener() {
//                @Override
//                public void onAccessTokenInitComplete() {
//                    token = getUserToken();
//                }
//            });
//        }

//        if (TextUtils.isEmpty(token)) {
//            mAuthMgr.initUserToken(this, GrantorType.GOOGLE, new Runnable() {
//                @Override
//                public void run() {
//                    getUserToken();
//                }
//            });
//        }
    }

//    public String getUserToken() {
//        String t = mAuthMgr.getAccessToken();
//        Log.d(TAG, "getUserToken: " + t);
//        return t;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + String.format("requestCode: %s, resultCode: %s, data: %s", requestCode, resultCode, data));
        mAuthMgr.processActivityResult(requestCode, resultCode, data);
    }
}
