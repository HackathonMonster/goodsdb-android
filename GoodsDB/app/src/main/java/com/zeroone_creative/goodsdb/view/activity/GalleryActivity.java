package com.zeroone_creative.goodsdb.view.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;
import com.zeroone_creative.goodsdb.R;
import com.zeroone_creative.goodsdb.controller.provider.NetworkTaskCallback;
import com.zeroone_creative.goodsdb.controller.provider.NetworkTasks;
import com.zeroone_creative.goodsdb.controller.provider.VolleyHelper;
import com.zeroone_creative.goodsdb.controller.util.JSONArrayRequestUtil;
import com.zeroone_creative.goodsdb.controller.util.UriUtil;
import com.zeroone_creative.goodsdb.model.pojo.Account;
import com.zeroone_creative.goodsdb.model.pojo.Goods;
import com.zeroone_creative.goodsdb.model.system.AccountHelper;
import com.zeroone_creative.goodsdb.model.system.AppConfig;
import com.zeroone_creative.goodsdb.view.adapter.GoodsAdapter;
import com.zeroone_creative.goodsdb.view.fragment.MessageDialogFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.activity_gallery)
public class GalleryActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    @ViewById(R.id.toolbar_actionbar)
    Toolbar mActionBarToolbar;
    @ViewById(R.id.gallery_gridbview)
    GridView mGridView;
    @ViewById(R.id.gallery_textview)
    TextView mTextView;
    @ViewById(R.id.gallery_fab)
    FloatingActionButton mFAButton;
    @ViewById(R.id.gallery_spinner)
    Spinner mTypeSpinner;

    private GoodsAdapter mAdapter;

    private static final int TAG_LOGIN_ACTIVITY = 100;
    private static final int TAG_POST_ACTIVITY = 200;

    @AfterViews
    void onAfterViews() {
        setActionBarToolbar();
        setAdapter();
        mTypeSpinner.setOnItemSelectedListener(this);

        mFAButton.attachToListView(mGridView);

        Account account = AccountHelper.getAccount(getApplicationContext());
        if(account.isAccount()) {
            onRequest();
        } else {
            translateLogin();
        }
    }

    private void setActionBarToolbar() {
        if (mActionBarToolbar != null) {
            mActionBarToolbar.setTitle("");
            mActionBarToolbar.setTitleTextColor(getResources().getColor(R.color.accent_text));
            setSupportActionBar(mActionBarToolbar);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                SearchActivity_.intent(this).start();
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void setAdapter() {
        mAdapter = new GoodsAdapter(getApplicationContext(), new ArrayList<Goods>());
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
    }

    private void onRequest() {
        Account account = AccountHelper.getAccount(getApplicationContext());
        Map<String, String> header = new HashMap<String, String>();
        header.put("X-Token", account.user.token);
        //header.put("X-Token", "dummy_token");
        JSONArrayRequestUtil getGoodsTask = new JSONArrayRequestUtil(new NetworkTaskCallback() {
            @Override
            public void onSuccessNetworkTask(int taskId, Object object) {
                parseGoodsJsonArray((JSONArray) object);
            }
            @Override
            public void onFailedNetworkTask(int taskId, Object object) {
                MessageDialogFragment.newInstance("通信に失敗しました\nもう一度アプリを開いてください", "閉じる").show(getFragmentManager(), AppConfig.TAG_MESSSAGE_DIALOG);
            }
        },
        getClass().getSimpleName(),
        header);
        getGoodsTask.onRequest(VolleyHelper.getRequestQueue(getApplicationContext()),
                Request.Priority.HIGH,
                UriUtil.getGoodsUri(),
                NetworkTasks.GoodsGet);
    }

    @Click({R.id.gallery_textview, R.id.gallery_fab})
    void translatePost() {
        PostActivity_.intent(this).mRunchType(PostActivity_.RUNCH_POST).mGoodsJson("").startForResult(TAG_POST_ACTIVITY);
    }

    // To login
    private void translateLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(intent, TAG_LOGIN_ACTIVITY);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(getClass().getSimpleName(),"onItemClickListener");
        Goods goods = mAdapter.getItem(position);
        String goodsObject = new Gson().toJson(goods);
        PostActivity_.intent(this).mRunchType(PostActivity_.RUNCH_DETAIL).mGoodsJson(goodsObject).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TAG_LOGIN_ACTIVITY) {
            if(resultCode == RESULT_OK) {
                onRequest();
            } else {
                MessageDialogFragment dialog = MessageDialogFragment.newInstance("ログインしないとご利用いただけません", "OK");
                dialog.setCallback(new MessageDialogFragment.MessageDialogCallback() {
                    @Override
                    public void onMessageDialogCallback() {
                        translateLogin();
                    }
                });
                dialog.show(getFragmentManager(), AppConfig.TAG_MESSSAGE_DIALOG);
            }
        } else if(requestCode == TAG_POST_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                onRequest();
            }
        }
    }

    private void parseGoodsJsonArray(JSONArray goodsArray) {
        List<Goods> content = new ArrayList<Goods>();
        for(int i=0; i < goodsArray.length(); i++ ) {
            try {
                JSONObject goodsObject = goodsArray.getJSONObject(i);
                Goods goods = new Gson().fromJson(goodsObject.toString(), Goods.class);
                content.add(goods);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(goodsArray.length() == 0) {
            mTextView.setText(getString(R.string.gallery_noitem));
            mTextView.setVisibility(View.VISIBLE);
        } else {
            mTextView.setVisibility(View.GONE);
        }
        mAdapter.updateContent(content);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView textView = (TextView) view;
        StringBuilder sb = new StringBuilder();
        sb.append("parent=").append(parent.getClass().getSimpleName())
                .append(" position=").append(position).append(" id=").append(id)
                .append(" textView.getText()=").append(textView.getText());
        Toast.makeText(getApplicationContext(), sb.toString(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
