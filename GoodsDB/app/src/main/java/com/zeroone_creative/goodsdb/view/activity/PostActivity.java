package com.zeroone_creative.goodsdb.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zeroone_creative.goodsdb.R;
import com.zeroone_creative.goodsdb.controller.provider.NetworkTaskCallback;
import com.zeroone_creative.goodsdb.controller.provider.NetworkTasks;
import com.zeroone_creative.goodsdb.controller.util.ImageUtil;
import com.zeroone_creative.goodsdb.controller.util.PostPictureRequestUtil;
import com.zeroone_creative.goodsdb.model.system.AppConfig;
import com.zeroone_creative.goodsdb.model.system.ImageUriUtil;
import com.zeroone_creative.goodsdb.view.fragment.AddTagDialogFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_post)
public class PostActivity extends Activity implements AddTagDialogFragment.AddTagDialogCallback {
    @Extra("is_post")
    boolean mIsPost = true;

    //For activity result tag
    private static int GET_IMAGE_CAMERA = 201;

    private static Uri mImageUri;
    private Bitmap mImageData;
    private List<String> mTagTexts = new ArrayList<String>();
    private LayoutInflater mInflater;
    private String mSelectString = null;

    //Image Holder
    //TODO => List<ImageView> in LinearLayout(Horizontal)[Image Container]
    @ViewById(R.id.post_imageview)
    ImageView mImageView;
    @ViewById(R.id.post_gridlayout_tag_container)
    GridLayout mGridLayout;
    @ViewById(R.id.post_button_send)
    Button mSendButton;

    @AfterViews
    void onAfterViews() {
        mSelectString = null;
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        refleshGridLayout();
        if(mIsPost) {
            runchCameraIntent();
        }
    }

    private void runchCameraIntent() {
        mImageUri = ImageUriUtil.getPhotoUri(getApplicationContext());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent, GET_IMAGE_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_IMAGE_CAMERA) {
            if( resultCode == RESULT_OK ) {
                try {
                    mImageData = ImageUtil.loadImage(getApplicationContext(), mImageUri);
                    //データ入力
                    Picasso.with(getApplicationContext()).load(mImageUri).into(mImageView);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                finish();
            }
        }
    }

    private void showAddTagDialog(String content) {
        if(getFragmentManager().findFragmentByTag(AppConfig.TAG_ADD_TAG_DIALOG) == null) {
            AddTagDialogFragment dialog = AddTagDialogFragment.newInstance(content);
            dialog.setCallback(this);
            dialog.show(getFragmentManager(), AppConfig.TAG_ADD_TAG_DIALOG);
        }
    }

    @Override
    public void onTagDialogCallback(String content) {
        if(content!=null && !content.equals("")) {
            mTagTexts.add(content);
        }
        refleshGridLayout();
        mSelectString = null;
    }

    @Override
    public void onCancelDialogCallback() {
        if(mSelectString!=null && !mSelectString.equals("")) {
            mTagTexts.add(mSelectString);
        }
        refleshGridLayout();
        mSelectString = null;
    }

    private void refleshGridLayout() {
        Log.d("PostActivity","refleshGridLayout");
        mGridLayout.removeAllViews();
        for(String tag : mTagTexts) {
            if(tag!=null) {
                TextView textView = getTagTextView(tag);
                textView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Object tag = v.getTag();
                        if(tag!=null) {
                            mTagTexts.remove(tag);
                            mSelectString = (String) tag;
                            showAddTagDialog((String) tag);
                        }
                        return false;
                    }
                });
                mGridLayout.addView(textView);

            }
        }
        TextView plusTextView = getTagTextView("+");
        plusTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTagDialog("");
            }
        });
        mGridLayout.addView(plusTextView);
    }

    private TextView getTagTextView(String tag) {
        TextView textView = (TextView) mInflater.inflate(R.layout.item_tag, null);
        textView.setText(tag);
        textView.setTag(tag);
        return textView;
    }

    @Click(R.id.post_button_send)
    void onSend() {
        PostPictureRequestUtil postTask = new PostPictureRequestUtil(NetworkTasks.GoodsPost, new NetworkTaskCallback() {
            @Override
            public void onSuccessNetworkTask(int taskId, Object object) {
                Log.d("Response", object.toString());
            }
            @Override
            public void onFailedNetworkTask(int taskId, Object object) {

            }
        });

        List<NameValuePair> stringParams = new ArrayList<NameValuePair>();
        //TODO Chage test -> user input
        stringParams.add(new BasicNameValuePair("item[name]","test"));
        for(String tag : mTagTexts) {
            stringParams.add(new BasicNameValuePair("item[tags][]",tag));
        }

        List<Bitmap> imageParams = new ArrayList<Bitmap>();
        //TODO Change single image -> multi image
        imageParams.add(mImageData);
        postTask.onRequest(com.zeroone_creative.goodsdb.controller.util.UriUtil.postGoodsUri(), stringParams, imageParams);
    }

}
