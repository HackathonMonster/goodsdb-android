package com.zeroone_creative.goodsdb.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.volley.Request;
import com.facebook.AppEventsLogger;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;
import com.zeroone_creative.goodsdb.R;
import com.zeroone_creative.goodsdb.controller.provider.NetworkTaskCallback;
import com.zeroone_creative.goodsdb.controller.provider.NetworkTasks;
import com.zeroone_creative.goodsdb.controller.provider.VolleyHelper;
import com.zeroone_creative.goodsdb.controller.util.JSONRequestUtil;
import com.zeroone_creative.goodsdb.controller.util.UriUtil;
import com.zeroone_creative.goodsdb.model.pojo.Account;
import com.zeroone_creative.goodsdb.model.pojo.FBUser;
import com.zeroone_creative.goodsdb.model.system.AccountHelper;
import com.zeroone_creative.goodsdb.model.system.AppConfig;
import com.zeroone_creative.goodsdb.view.fragment.MessageDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends ActionBarActivity {


    private final static String TAG = "LoginActivity";

    private UiLifecycleHelper uiHelper;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolbar.setTitle("はじめに");
        setSupportActionBar(toolbar);

    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
            Log.i(TAG, "Application id"+session.getApplicationId());
            Log.i(TAG, "Application Token" + session.getAccessToken());
            //ログインの通信
            getFacebookId();
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }

    private void getFacebookId() {
        final Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            com.facebook.Request request = com.facebook.Request.newMeRequest(session, new com.facebook.Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser graphUser, Response response) {
                    // If the response is successful
                    if (session == Session.getActiveSession()) {
                        if (graphUser != null) {
                            postLoginRequest(graphUser.getId(), session.getAccessToken());
                        }
                    }
                }
            });
            com.facebook.Request.executeBatchAsync(request);
        }
    }

    private void postLoginRequest(String fbId, String fbToken) {
        JSONObject params = new JSONObject();
        try {
            params.put("facebook_id", fbId);
            params.put("facebook_token", fbToken);
            JSONRequestUtil loginTask = new JSONRequestUtil(new NetworkTaskCallback() {
                @Override
                public void onSuccessNetworkTask(int taskId, Object object) {
                    Account account = AccountHelper.getAccount(getApplicationContext());
                    FBUser user = new Gson().fromJson(object.toString(), FBUser.class);
                    account.saveAccount(user);
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                @Override
                public void onFailedNetworkTask(int taskId, Object object) {
                    MessageDialogFragment dialog = MessageDialogFragment.newInstance("ログイン情報の登録に失敗しました","閉じる");
                    dialog.show(getFragmentManager(), AppConfig.TAG_MESSSAGE_DIALOG);
                }
            }, getClass().getSimpleName(), null);
            loginTask.onRequest(VolleyHelper.getRequestQueue(getApplicationContext()),
                    Request.Priority.HIGH,
                    UriUtil.postLoginUri(),
                    NetworkTasks.UserLogin,
                    params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
}
