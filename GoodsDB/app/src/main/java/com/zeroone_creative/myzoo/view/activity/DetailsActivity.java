package com.zeroone_creative.myzoo.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.LocationSource;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.zeroone_creative.myzoo.R;
import com.zeroone_creative.myzoo.controller.provider.NetworkTaskCallback;
import com.zeroone_creative.myzoo.controller.provider.NetworkTasks;
import com.zeroone_creative.myzoo.controller.util.PostPictureRequestUtil;
import com.zeroone_creative.myzoo.controller.util.UriUtil;
import com.zeroone_creative.myzoo.model.pojo.Account;
import com.zeroone_creative.myzoo.model.pojo.Creature;
import com.zeroone_creative.myzoo.model.system.AccountHelper;
import com.zeroone_creative.myzoo.model.system.AppConfig;
import com.zeroone_creative.myzoo.view.fragment.MessageDialogFragment;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.zeroone_creative.myzoo.view.fragment.MessageDialogFragment.MessageDialogCallback;


@EActivity(R.layout.activity_details)
public class DetailsActivity extends Activity implements LocationSource.OnLocationChangedListener, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener, GooglePlayServicesClient.ConnectionCallbacks {
    private static int GET_IMAGE_ACTIVITY = 303;

    @Extra("json")
    String mJson;

    @ViewById(R.id.capture_imageview)
    ImageView mImageView;
    @ViewById(R.id.capture_edittext_name)
    EditText mNameEditText;
    @ViewById(R.id.capture_edittext_caption)
    EditText mCaptionEditText;

    @ViewById(R.id.capture_textView_sound)
    TextView mRecordTextView;

    private Location mLocation;
    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;

    private MediaRecorder mRecorder;

    private Creature mCreature = null;

    private boolean mIsRecording = false;

    @AfterInject
    void afterInject() {
        if(mJson == null) return;
        Log.d("tag", mJson);
        mCreature = new Gson().fromJson(mJson,Creature.class);
    }

    @AfterViews
    void onAfterViews() {
        if( mCreature==null ) {
            //インスタンスの取得
            mLocationRequest = LocationRequest.create();
            //リクエストのインターバル設定５秒
            mLocationRequest.setInterval(5*1000);
            //リクエストのプライオリティー設定
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            //最短のリクエスト取得
            mLocationRequest.setFastestInterval(1*1000);
            //現在位置の取得
            mLocationClient = new LocationClient(getApplicationContext(),this,this);
            if (mLocationClient != null) {
                //Google Play Servicesに接続
                mLocationClient.connect();
            }
        } else {
            setUi(mCreature);
        }
        setRecorder();
    }

    private void setRecorder() {
        // MediaRecorderのインスタンスを作成
        mRecorder = new MediaRecorder();
        // マイクからの入力とする
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 記録フォーマットを3GPPに設定
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        // 音声コーデックをAMR-NBに設定
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // 出力ファイルパスを設定
        mRecorder.setOutputFile("/sdcard/audio_sample.3gp");
        try {
            // レコーダーを準備
            mRecorder.prepare();
        } catch(IllegalStateException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void setUi(Creature creature) {
        Picasso.with(getApplicationContext()).load(creature.image).into(mImageView);
        mNameEditText.setText(creature.name);
        mCaptionEditText.setText(creature.text);

        findViewById(R.id.capture_rippleview_capture).setVisibility(View.INVISIBLE);
    }

    @Click(R.id.capture_rippleview_sound)
    void startRecord() {
        if(!mIsRecording) {
            mRecordTextView.setText("■ ていしする");
            try {
                // 録音開始
                mRecorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mIsRecording = true;

            MessageDialogFragment dialog = MessageDialogFragment.newInstance("ろくおんちゅう．．．", "■ ていしする");
            dialog.setCallback(new MessageDialogCallback() {
                @Override
                public void onMessageDialogCallback() {
                    mRecordTextView.setText("● ろくおんする");
                    try {
                        // 録音停止
                        mRecorder.stop();
                        // 再使用に備えてレコーダーの状態をリセット
                        mRecorder.reset();
                        mIsRecording = false;
                    } catch(IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            });
            dialog.show(getFragmentManager(),AppConfig.TAG_MESSSAGE_DIALOG);
        } else {
            mRecordTextView.setText("● ろくおんする");
            try {
                // 録音停止
                mRecorder.stop();
                // 再使用に備えてレコーダーの状態をリセット
                mRecorder.reset();
                setRecorder();
                mIsRecording = false;
            } catch(IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 使わなくなった時点でレコーダーリソースを解放
        mRecorder.release();
    }

    @Click(R.id.capture_imageview)
    void getImage() {
        Intent intent = new Intent(getApplicationContext(), SelectWayActivity_.class);
        startActivityForResult(intent, GET_IMAGE_ACTIVITY);
    }

    @Click(R.id.capture_rippleview_capture)
    void onPost() {
        Account account = AccountHelper.getAccount(getApplicationContext());
        PostPictureRequestUtil createTask = new PostPictureRequestUtil(NetworkTasks.Create, new NetworkTaskCallback() {
            @Override
            public void onSuccessNetworkTask(int taskId, Object object) {
                findViewById(R.id.capture_rippleview_capture).setEnabled(true);
                MessageDialogFragment dialog = MessageDialogFragment.newInstance("ほかくに成功したよ！", "とじる");
                dialog.setCallback(new MessageDialogCallback() {
                    @Override
                    public void onMessageDialogCallback() {
                        finish();
                    }
                });
                dialog.show(getFragmentManager(), AppConfig.TAG_MESSSAGE_DIALOG);
            }
            @Override
            public void onFailedNetworkTask(int taskId, Object object) {
                findViewById(R.id.capture_rippleview_capture).setEnabled(true);
                MessageDialogFragment.newInstance("ほかくに失敗しちゃった\nもう一度ためしてみてね", "とじる").show(getFragmentManager(), AppConfig.TAG_MESSSAGE_DIALOG);
            }
        });

        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        if(bitmap != null) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", account.userId));
            params.add(new BasicNameValuePair("name", mNameEditText.getText().toString()));
            params.add(new BasicNameValuePair("text", mCaptionEditText.getText().toString()));
            if(mLocation != null) {
                params.add(new BasicNameValuePair("lat", Double.toString(mLocation.getLatitude())));
                params.add(new BasicNameValuePair("lng",  Double.toString(mLocation.getLongitude())));
            }
            createTask.onRequest(UriUtil.getCreateUri(), params, bitmap);
        }
        findViewById(R.id.capture_rippleview_capture).setEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_IMAGE_ACTIVITY && resultCode == RESULT_OK) {
            String imagePath = data.getStringExtra("image_path");
            Picasso.with(getApplicationContext()).load(new File(imagePath)).into(mImageView);

        }
    }

    @Override
    public void onConnected(Bundle location) {
        Log.d(getClass().getSimpleName(), "onConnected");
        // 接続成功
        // 位置情報取得を開始
        //位置情報の取得開始
        if(mLocationClient.isConnected()){
            mLocationClient.requestLocationUpdates(mLocationRequest, this);
            // 最新の位置情報を取得
            mLocation = mLocationClient.getLastLocation();

        }
    }

    @Override
    public void onDisconnected() {
        // 接続解除
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg) {
        //TODO 位置情報の取得に失敗したダイアログを出す
        if(getFragmentManager().findFragmentByTag(AppConfig.TAG_MESSSAGE_DIALOG) == null) {
            MessageDialogFragment.newInstance("いちじょうほうの取得に失敗しました", "とじる").show(getFragmentManager(), AppConfig.TAG_MESSSAGE_DIALOG);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mLocationClient.isConnected()){
            mLocation = location;
            mLocationClient.removeLocationUpdates(this);
        }
    }
}
