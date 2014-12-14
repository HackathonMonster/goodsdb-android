package com.zeroone_creative.goodsdb.view.activity;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.Gson;
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
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.activity_search)
public class SearchActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    @ViewById(R.id.search_edittext)
    EditText mSearchEditText;
    @ViewById(R.id.toolbar_actionbar)
    Toolbar mToolbar;
    @ViewById(R.id.seach_gridbview)
    GridView mGridView;
    @ViewById(R.id.search_textview_hits)
    TextView mHitsTextView;

    private GoodsAdapter mAdapter;

    @AfterViews
    void onAfterViews() {
        mHitsTextView.setText(R.string.search_hits_nav);

        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setAdapter();
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onSearch(v.getText().toString(), "found");
                }
                return true; // falseを返すと, IMEがSearch→Doneへと切り替わる
            }
        });
    }

    private void setAdapter() {
        mAdapter = new GoodsAdapter(getApplicationContext(), new ArrayList<Goods>());
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
    }

    private void onSearch(String text, String type) {
        List<String> tagParams = new ArrayList<String>();
        tagParams.add(text);


        Account account = AccountHelper.getAccount(getApplicationContext());
        Map<String, String> header = new HashMap<String, String>();
        header.put("X-Token", account.user.token);
        JSONArrayRequestUtil searchTask = new JSONArrayRequestUtil(new NetworkTaskCallback() {
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
        header
        );
        searchTask.onRequest(VolleyHelper.getRequestQueue(getApplicationContext()),
                Request.Priority.HIGH,
                UriUtil.getSearchUri(tagParams, type),
                NetworkTasks.GoodsSearch);
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
        mHitsTextView.setText(getString(R.string.search_hits_result, goodsArray.length()));
        mAdapter.updateContent(content);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Goods goods = mAdapter.getItem(position);
        String goodsObject = new Gson().toJson(goods);
        PostActivity_.intent(this).mRunchType(PostActivity_.RUNCH_DETAIL).mGoodsJson(goodsObject).start();
    }
}
