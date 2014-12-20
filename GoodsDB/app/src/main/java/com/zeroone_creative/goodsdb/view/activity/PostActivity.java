package com.zeroone_creative.goodsdb.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zeroone_creative.goodsdb.R;
import com.zeroone_creative.goodsdb.controller.provider.NetworkTaskCallback;
import com.zeroone_creative.goodsdb.controller.provider.NetworkTasks;
import com.zeroone_creative.goodsdb.controller.provider.VolleyHelper;
import com.zeroone_creative.goodsdb.controller.util.CropSquareTransformation;
import com.zeroone_creative.goodsdb.controller.util.ImageUtil;
import com.zeroone_creative.goodsdb.controller.util.JSONRequestUtil;
import com.zeroone_creative.goodsdb.controller.util.PostPictureRequestUtil;
import com.zeroone_creative.goodsdb.controller.util.UriUtil;
import com.zeroone_creative.goodsdb.model.pojo.Account;
import com.zeroone_creative.goodsdb.model.pojo.Goods;
import com.zeroone_creative.goodsdb.model.pojo.Picture;
import com.zeroone_creative.goodsdb.model.pojo.PostItem;
import com.zeroone_creative.goodsdb.model.pojo.Tag;
import com.zeroone_creative.goodsdb.model.system.AccountHelper;
import com.zeroone_creative.goodsdb.model.system.AppConfig;
import com.zeroone_creative.goodsdb.model.system.ImageUriUtil;
import com.zeroone_creative.goodsdb.view.fragment.AddTagDialogFragment;
import com.zeroone_creative.goodsdb.view.fragment.MessageDialogFragment;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.activity_post)
public class PostActivity extends ActionBarActivity implements AddTagDialogFragment.AddTagDialogCallback {
    public final static int RUNCH_POST = 0;
    public final static int RUNCH_DETAIL = 1;
    public final static int RUNCH_EDIT = 2;

    @Extra("runch_type")
    int mRunchType = RUNCH_POST;
    @Extra("goods_json")
    String mGoodsJson;

    //For activity result tag
    private static int GET_IMAGE_CAMERA = 201;

    private static Uri mImageUri;
    private List<Bitmap> mImages = new ArrayList<Bitmap>();
    private List<String> mTagTexts = new ArrayList<String>();
    private LayoutInflater mInflater;
    private String mSelectString = null;

    @ViewById(R.id.toolbar_actionbar)
    Toolbar mToolbar;
    @ViewById(R.id.post_edittext_name)
    EditText mNameEditText;
    @ViewById(R.id.post_imageview_main)
    ImageView mImageView;
    @ViewById(R.id.post_layout_image_container)
    LinearLayout mImageContainerLayout;
    @ViewById(R.id.post_gridlayout_tag_container)
    GridLayout mGridLayout;
    @ViewById(R.id.post_button_send)
    LinearLayout mSendButton;
    @ViewById(R.id.post_imageview_add)
    ImageView mAddImageView;
    @ViewById(R.id.post_textview_send_text)
    TextView mSendTextView;
    @ViewById(R.id.post_imageview_send)
    ImageView mSendImageView;

    private Context mContext;

    @AfterInject
    void onAfterInject() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mRunchType == RUNCH_DETAIL) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_post, menu);
        } else {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_edit, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                PostActivity_.intent(this).mRunchType(PostActivity_.RUNCH_EDIT).mGoodsJson(mGoodsJson).start();
                finish();
                break;
            case R.id.menu_remove:
                removeGoods();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @AfterViews
    void onAfterViews() {
        mContext = this;
        mSelectString = null;
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setSupportActionBar(mToolbar);
        refleshGridLayout();
        if(mRunchType == RUNCH_POST ) {
            runchCameraIntent();
        } else {
            //Load
            loadGoods();
        }
    }

    @Click(R.id.post_imageview_add)
    void runchCameraIntent() {
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
                    mImages.add(ImageUtil.loadImage(getApplicationContext(), mImageUri));
                    if(mImages.size() == 1) {
                        //データ入力
                        Picasso.with(getApplicationContext()).load(mImageUri).error(R.drawable.img_detail_noimg).into(mImageView);
                    } else {
                        ImageView imageView = (ImageView) mInflater.inflate(R.layout.item_image, null);
                        Picasso.with(getApplicationContext()).load(mImageUri).transform(new CropSquareTransformation()).error(R.drawable.img_detail_noimg).resize(getResources().getDimensionPixelSize(R.dimen.post_iamgeview_sub_size), getResources().getDimensionPixelSize(R.dimen.post_iamgeview_sub_size)).into(imageView);
                        //imageView.setImageBitmap(ImageUtil.loadImage(getApplicationContext(), mImageUri));
                        mImageContainerLayout.addView(imageView, 0);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if(mImages.size() < 1) {
                    finish();
                }
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

    /**
     * This method is refreshing all tags.
     */
    private void refleshGridLayout() {
        mGridLayout.removeAllViews();
        for(String tag : mTagTexts) {
            if(tag != null) {
                TextView textView = getTagTextView(tag);
                if(mRunchType != RUNCH_DETAIL) {
                    textView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            Object tag = v.getTag();
                            if(tag != null) {
                                mTagTexts.remove(tag);
                                mSelectString = (String) tag;
                                showAddTagDialog((String) tag);
                            }
                            return false;
                        }
                    });
                } else {
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Object tag = v.getTag();
                            if(tag!=null) {
                                SearchActivity_.intent(mContext).mTag((String) tag).start();
                            }
                        }
                    });
                }
                mGridLayout.addView(textView);
            }
        }
        if(mRunchType != RUNCH_DETAIL) {
            TextView plusTextView = getTagTextView("+");
            plusTextView.setTextSize(18);
            plusTextView.setBackgroundResource(R.drawable.bg_tag_plus);
            plusTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddTagDialog("");
                }
            });
            mGridLayout.addView(plusTextView);
        }
    }

    /**
     * This method is return view for "tag"'s TextView.
     * @param tag
     * @return TextView
     */
    private TextView getTagTextView(String tag) {
        TextView textView = (TextView) mInflater.inflate(R.layout.item_tag, null);
        textView.setText(tag);
        textView.setTag(tag);
        if(tag.substring(0,1).equals("#") && Tag.isSystem(tag)) {
            textView.setBackgroundResource(R.drawable.bg_tag_system);
            textView.setTextColor(getResources().getColor(R.color.primary_orange));
        }
        return textView;
    }

    @Click(R.id.post_button_send)
    void onSend() {
        mSendButton.setEnabled(false);
        if(mRunchType == RUNCH_POST) {
            postGoods();
        } else if(mRunchType == RUNCH_EDIT) {
            editGoods();
        } else if(mRunchType == RUNCH_DETAIL) {
            postLoves();
        }
    }

    private void postGoods() {
        Account account = AccountHelper.getAccount(getApplicationContext());
        List<NameValuePair> header = new ArrayList<NameValuePair>();
        header.add(new BasicNameValuePair("X-Token", account.user.token));
        PostPictureRequestUtil postTask = new PostPictureRequestUtil(NetworkTasks.GoodsPost, new NetworkTaskCallback() {
            @Override
            public void onSuccessNetworkTask(int taskId, final Object object) {
                Log.d("Response", object.toString());
                MessageDialogFragment dialog = MessageDialogFragment.newInstance("投稿が完了しました！","閉じる");
                dialog.setCallback(new MessageDialogFragment.MessageDialogCallback() {
                    @Override
                    public void onMessageDialogCallback() {
                        Intent data = new Intent();
                        data.putExtra("response_json", object.toString());
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
                dialog.show(getFragmentManager(),AppConfig.TAG_MESSSAGE_DIALOG);
            }
            @Override
            public void onFailedNetworkTask(int taskId, Object object) {
                MessageDialogFragment dialog = MessageDialogFragment.newInstance("投稿に失敗しました！","閉じる");
                dialog.setCallback(new MessageDialogFragment.MessageDialogCallback() {
                    @Override
                    public void onMessageDialogCallback() {
                        //finish();
                        mSendButton.setEnabled(true);
                    }
                });
                dialog.show(getFragmentManager(),AppConfig.TAG_MESSSAGE_DIALOG);
            }
        });
        postTask.setHeader(header);
        List<NameValuePair> stringParams = new ArrayList<NameValuePair>();
        //TODO Chage test -> user input
        stringParams.add(new BasicNameValuePair("item[name]",mNameEditText.getText().toString()));
        for(String tag : mTagTexts) {
            stringParams.add(new BasicNameValuePair("item[tags][]",tag));
        }
        postTask.onRequest(com.zeroone_creative.goodsdb.controller.util.UriUtil.postGoodsUri(), stringParams, mImages);
    }


    private void postLoves() {
        Goods goods = new Gson().fromJson(mGoodsJson, Goods.class);
        if(goods==null) return ;
        Account account = AccountHelper.getAccount(getApplicationContext());
        Map<String, String> header = new HashMap<String, String>();
        header.put("X-Token", account.user.token);
        JSONRequestUtil loveTask = new JSONRequestUtil(new NetworkTaskCallback() {
            @Override
            public void onSuccessNetworkTask(int taskId, final Object object) {
                Log.d("Response", object.toString());
                if(taskId == NetworkTasks.LikePost.id) {
                    if(getFragmentManager().findFragmentByTag(AppConfig.TAG_MESSSAGE_DIALOG) == null) {
                        MessageDialogFragment.newInstance("お気に入りに追加されました！", "閉じる").show(getFragmentManager(), AppConfig.TAG_MESSSAGE_DIALOG);
                    }
                    mSendTextView.setText(R.string.post_button_unfavo);
                    mSendImageView.setImageResource(R.drawable.ic_heart_broken);
                } else if(taskId == NetworkTasks.LikeDelete.id) {
                    if(getFragmentManager().findFragmentByTag(AppConfig.TAG_MESSSAGE_DIALOG) == null) {
                        MessageDialogFragment.newInstance("お気に入りから削除されました", "閉じる").show(getFragmentManager(), AppConfig.TAG_MESSSAGE_DIALOG);
                    }
                    mSendTextView.setText(R.string.post_button_favo);
                    mSendImageView.setImageResource(R.drawable.ic_heart);
                }
            }
            @Override
            public void onFailedNetworkTask(int taskId, Object object) {
                MessageDialogFragment dialog = MessageDialogFragment.newInstance("通信に失敗しました！","閉じる");
                dialog.setCallback(new MessageDialogFragment.MessageDialogCallback() {
                    @Override
                    public void onMessageDialogCallback() {
                        //finish();
                        mSendButton.setEnabled(true);
                    }
                });
                dialog.show(getFragmentManager(),AppConfig.TAG_MESSSAGE_DIALOG);
            }
        },
        getClass().getSimpleName(),
        header);
        loveTask.onRequest(VolleyHelper.getRequestQueue(getApplicationContext()),
                Request.Priority.HIGH,
                UriUtil.changeLikeUri(Integer.toString(goods.id)),
                goods.liked?NetworkTasks.LikeDelete : NetworkTasks.LikePost);
    }

    private void editGoods() {
        Goods goods = new Gson().fromJson(mGoodsJson, Goods.class);
        if(goods==null) return ;
        Account account = AccountHelper.getAccount(getApplicationContext());
        Map<String, String> header = new HashMap<String, String>();
        header.put("X-Token", account.user.token);
        JSONRequestUtil putTask = new JSONRequestUtil(new NetworkTaskCallback() {
            @Override
            public void onSuccessNetworkTask(int taskId, final Object object) {
                Log.d("Response", object.toString());
                MessageDialogFragment dialog = MessageDialogFragment.newInstance("変更が完了しました！","閉じる");
                dialog.setCallback(new MessageDialogFragment.MessageDialogCallback() {
                    @Override
                    public void onMessageDialogCallback() {
                        Intent data = new Intent();
                        data.putExtra("response_json", object.toString());
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
                dialog.show(getFragmentManager(),AppConfig.TAG_MESSSAGE_DIALOG);
            }
            @Override
            public void onFailedNetworkTask(int taskId, Object object) {
                MessageDialogFragment dialog = MessageDialogFragment.newInstance("通信に失敗しました！","閉じる");
                dialog.setCallback(new MessageDialogFragment.MessageDialogCallback() {
                    @Override
                    public void onMessageDialogCallback() {
                        //finish();
                        mSendButton.setEnabled(true);
                    }
                });
                dialog.show(getFragmentManager(),AppConfig.TAG_MESSSAGE_DIALOG);
            }
        },
        getClass().getSimpleName(),
        header
        );
        try {
            PostItem item = new PostItem();
            item.name = mNameEditText.getText().toString();
            item.tags = mTagTexts;
            JSONObject params = new JSONObject();
            params.put("item", new JSONObject(new Gson().toJson(item)));
            Log.d("PostActivity EditGoods", params.toString());
            putTask.onRequest(VolleyHelper.getRequestQueue(getApplicationContext()),
                    Request.Priority.HIGH,
                    UriUtil.putGoodsUri(Integer.toString(goods.id)),
                    NetworkTasks.GoodsEdit,
                    params);
        } catch (JSONException e) {
            putTask = null;
            e.printStackTrace();
        }
    }

    private void removeGoods() {
        Goods goods = new Gson().fromJson(mGoodsJson, Goods.class);
        if(goods==null) return ;
        Account account = AccountHelper.getAccount(getApplicationContext());
        Map<String, String> header = new HashMap<String, String>();
        header.put("X-Token", account.user.token);
        JSONRequestUtil deleteTask = new JSONRequestUtil(new NetworkTaskCallback() {
            @Override
            public void onSuccessNetworkTask(int taskId, final Object object) {
                Log.d("Response", object.toString());
                MessageDialogFragment dialog = MessageDialogFragment.newInstance("削除が完了しました！","閉じる");
                dialog.setCallback(new MessageDialogFragment.MessageDialogCallback() {
                    @Override
                    public void onMessageDialogCallback() {
                        finish();
                    }
                });
                dialog.show(getFragmentManager(),AppConfig.TAG_MESSSAGE_DIALOG);
            }
            @Override
            public void onFailedNetworkTask(int taskId, Object object) {
                MessageDialogFragment dialog = MessageDialogFragment.newInstance("投稿に失敗しました！","閉じる");
                dialog.setCallback(new MessageDialogFragment.MessageDialogCallback() {
                    @Override
                    public void onMessageDialogCallback() {
                        //finish();
                        mSendButton.setEnabled(true);
                    }
                });
                dialog.show(getFragmentManager(),AppConfig.TAG_MESSSAGE_DIALOG);
            }
        },
                getClass().getSimpleName(),
                header
        );
        deleteTask.onRequest(VolleyHelper.getRequestQueue(getApplicationContext()),
                Request.Priority.HIGH,
                UriUtil.deleteGoodsUri(Integer.toString(goods.id)),
                NetworkTasks.GoodsDelete);
    }

    private void loadGoods() {
        Goods goods = new Gson().fromJson(mGoodsJson, Goods.class);
        if(goods!=null) {
            if(goods.name!=null) {
                mNameEditText.setText(goods.name);
            }
            if(goods.tags!=null) {
                for(Tag tag : goods.tags) {
                    mTagTexts.add(tag.name);
                }
                refleshGridLayout();
            }
            if(goods.pictures!=null) {
                for(Picture picture : goods.pictures) {
                    Picasso.with(getApplicationContext()).load(picture.imageUrl).into(mPicassoTarget);
                }
            }
            if(mRunchType == RUNCH_DETAIL) {
                mNameEditText.setHint("");
                mNameEditText.setEnabled(false);
                mAddImageView.setVisibility(View.GONE);
                if(goods.liked) {
                    mSendTextView.setText(R.string.post_button_unfavo);
                    mSendImageView.setImageResource(R.drawable.ic_heart_broken);
                } else {
                    mSendTextView.setText(R.string.post_button_favo);
                    mSendImageView.setImageResource(R.drawable.ic_heart);
                }
            } else {
                mNameEditText.setEnabled(true);
                mAddImageView.setVisibility(View.VISIBLE);
                mSendButton.setVisibility(View.VISIBLE);
                if(mRunchType == RUNCH_POST) {
                    mSendTextView.setText(R.string.post_button_save);
                } else if(mRunchType == RUNCH_EDIT) {
                    mSendTextView.setText(R.string.post_button_update);
                }
            }
        }
    }

    private Target mPicassoTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mImages.add(bitmap);
            if(mImages.size() == 1) {
                mImageView.setImageBitmap(bitmap);
            } else {
                ImageView imageView = (ImageView) mInflater.inflate(R.layout.item_image, null);
                bitmap = ImageUtil.resize(bitmap,
                        getResources().getDimensionPixelSize(R.dimen.post_iamgeview_sub_size),
                        getResources().getDimensionPixelSize(R.dimen.post_iamgeview_sub_size));
                imageView.setImageBitmap(bitmap);
                mImageContainerLayout.addView(imageView,0);
            }
        }
        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            if(mImages.size() < 1) {
                mImageView.setImageResource(R.drawable.img_detail_noimg);
            }
        }
        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

}
