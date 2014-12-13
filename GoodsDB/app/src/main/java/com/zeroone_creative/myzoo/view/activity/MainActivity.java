package com.zeroone_creative.myzoo.view.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;

import com.zeroone_creative.myzoo.R;
import com.zeroone_creative.myzoo.model.pojo.Account;
import com.zeroone_creative.myzoo.model.system.AccountHelper;
import com.zeroone_creative.myzoo.view.fragment.MainFragment_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

    private static final int REGIST_ACTIVITY = 101;

    @AfterViews
    public void afterViews() {
        setFragment();
        translateRegist();
    }

    private void setFragment(){
        Fragment fragment = MainFragment_.builder().build();
        getFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REGIST_ACTIVITY) {
            if(resultCode != RESULT_OK) {
                translateRegist();
            }
        }
    }

    private void translateRegist() {
        Account account = AccountHelper.getAccount(getApplicationContext());
        if(!account.isAccount()) {
            Intent intent = new Intent(getApplicationContext(), RegistActivity_.class);
            startActivityForResult(intent, REGIST_ACTIVITY);
        }
    }

}
