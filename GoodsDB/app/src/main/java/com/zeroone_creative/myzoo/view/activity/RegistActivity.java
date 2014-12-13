package com.zeroone_creative.myzoo.view.activity;

import android.app.Activity;
import android.util.Log;
import android.widget.EditText;

import com.android.volley.Request;
import com.zeroone_creative.myzoo.R;
import com.zeroone_creative.myzoo.controller.provider.NetworkTaskCallback;
import com.zeroone_creative.myzoo.controller.provider.NetworkTasks;
import com.zeroone_creative.myzoo.controller.provider.VolleyHelper;
import com.zeroone_creative.myzoo.controller.util.JSONRequestUtil;
import com.zeroone_creative.myzoo.controller.util.UriUtil;
import com.zeroone_creative.myzoo.model.pojo.Account;
import com.zeroone_creative.myzoo.model.system.AccountHelper;
import com.zeroone_creative.myzoo.model.system.AppConfig;
import com.zeroone_creative.myzoo.view.fragment.MessageDialogFragment;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

@EActivity(R.layout.activity_regist)
public class RegistActivity extends Activity {

    @ViewById(R.id.regist_edittext_name)
    EditText mNameEditText;
    private String mName;

    @Click(R.id.regist_button)
    void onRegist() {
        findViewById(R.id.regist_button).setEnabled(false);
        Log.d(getClass().getSimpleName(),"onRegist()");
        mName = mNameEditText.getText().toString();
        if(mName.length() > 0) {
            JSONObject params = new JSONObject();
            try {
                params.put("name", mName);
                JSONRequestUtil registTask = new JSONRequestUtil(new NetworkTaskCallback() {
                    @Override
                    public void onSuccessNetworkTask(int taskId, Object object) {
                        JSONObject json = (JSONObject) object;
                        try {
                            String id = json.getString("id");
                            Account account = AccountHelper.getAccount(getApplicationContext());
                            account.userId = id;
                            account.saveAccount();
                            setResult(RESULT_OK, null);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            findViewById(R.id.regist_button).setEnabled(true);
                        }
                    }
                    @Override
                    public void onFailedNetworkTask(int taskId, Object object) {
                        findViewById(R.id.regist_button).setEnabled(true);
                        MessageDialogFragment.newInstance("つうしんに失敗しちゃった\nもう一度ためしてみてね", "とじる").show(getFragmentManager(), AppConfig.TAG_MESSSAGE_DIALOG);
                    }
                },
                getClass().getSimpleName(),
                null);
                registTask.onRequest(VolleyHelper.getRequestQueue(getApplicationContext()),
                        Request.Priority.HIGH,
                        UriUtil.getRegistUri(),
                        NetworkTasks.Regist,
                        params);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
