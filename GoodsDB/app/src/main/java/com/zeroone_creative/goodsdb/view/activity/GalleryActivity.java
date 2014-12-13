package com.zeroone_creative.goodsdb.view.activity;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.zeroone_creative.goodsdb.model.pojo.Creature;
import com.zeroone_creative.goodsdb.model.system.AccountHelper;
import com.zeroone_creative.goodsdb.view.adapter.CreatureAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_gallery)
public class GalleryActivity extends Activity implements AdapterView.OnItemClickListener {

    @ViewById(R.id.gallery_gridbview)
    GridView mGridView;

    @ViewById(R.id.gallery_textview)
    TextView mTextView;

    private CreatureAdapter mAdapter;

    @AfterViews
    void onAfterViews() {
        setAdapter();
        onRequest();
    }


    private void setAdapter() {
        mAdapter = new CreatureAdapter(getApplicationContext(), new ArrayList<Creature>());
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
    }

    private void onRequest() {
        JSONArrayRequestUtil getCreaturesTask = new JSONArrayRequestUtil(new NetworkTaskCallback() {
            @Override
            public void onSuccessNetworkTask(int taskId, Object object) {
                JSONArray dataArray = (JSONArray) object;
                List<Creature> content = new ArrayList<Creature>();
                for(int i=0; i < dataArray.length(); i++ ) {
                    try {
                        JSONObject creatureObject = dataArray.getJSONObject(i);
                        Creature creature = new Gson().fromJson(creatureObject.toString(),Creature.class);
                        content.add(creature);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(dataArray.length() == 0) {
                    mTextView.setText(getString(R.string.gallery_noitem));
                    mTextView.setVisibility(View.VISIBLE);
                } else {
                    mTextView.setVisibility(View.GONE);
                }
                mAdapter.updateContent(content);
            }
            @Override
            public void onFailedNetworkTask(int taskId, Object object) {
                mTextView.setText(getString(R.string.gallery_noitem));
                mTextView.setVisibility(View.VISIBLE);
                //MessageDialogFragment.newInstance("しゅとくに失敗しました\nもう一度ためしてみてね", "とじる").show(getFragmentManager(), AppConfig.TAG_MESSSAGE_DIALOG);
            }
        },
        getClass().getSimpleName(),
        null);
        Account account = AccountHelper.getAccount(getApplicationContext());
        getCreaturesTask.onRequest(VolleyHelper.getRequestQueue(getApplicationContext()),
                Request.Priority.NORMAL,
                UriUtil.getGalleryUri(account.userId),
                NetworkTasks.Gallery);
    }

    @Click(R.id.gallery_textview)
    void translateCapture() {
        PostActivity_.intent(this).mIsPost(true).start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(getClass().getSimpleName(),"onItemClickListener");
        Creature creature = mAdapter.getItem(position);
        String creatureObject = new Gson().toJson(creature);
        //DetailsActivity_.intent(this).mJson(creatureObject).start();
    }

}
