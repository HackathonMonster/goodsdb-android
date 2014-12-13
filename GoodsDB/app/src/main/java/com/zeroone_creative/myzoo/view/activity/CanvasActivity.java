package com.zeroone_creative.myzoo.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.zeroone_creative.myzoo.R;
import com.zeroone_creative.myzoo.controller.util.ImageUtil;
import com.zeroone_creative.myzoo.view.widget.ColorPickerDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

@EActivity(R.layout.activity_canvas)
public class CanvasActivity extends Activity implements View.OnTouchListener, ColorPickerDialog.OnColorChangedListener {

    //使う変数。
    Canvas mCanvas;
    Bitmap mBitmap;
    Paint mPaint;
    Path mPath;
    AlertDialog.Builder mAlertBuilder;
    float x1,y1;
    int width,height;

    @ViewById(R.id.canvas_imageview_canvas)
	ImageView mImageView;
	
	@AfterViews
    void onAfterViews() {
        setCanvas();
    }

    private void setCanvas() {
        Display disp = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        //width = Math.min(disp.getWidth(), disp.getHeight());
        width = Math.min(disp.getWidth() + (int) (getResources().getDimension(R.dimen.main_left_margin) + getResources().getDimension(R.dimen.activity_horizontal_margin) + getResources().getDimension(R.dimen.canvas_padding)*2)*-1, disp.getHeight());
        height = width;
        //任意の大きさで新規のビットマップを作成する。
        //指定の配列をピクセルデータとして読みこんで、ビットマップを作成する。その他にも、ファイルからビットマップを作成したり出来る。
        mBitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        //オブジェクトをインスタンス化（新しく作る）
        //Paintオブジェクト；色や線の太さなどの情報を設定できる
        mPaint=new Paint();
        //Pathオブジェクト：線を書く際に、線の情報を管理している
        mPath=new Path();
        //Canvasオブジェクト：絵を書いたりするキャンバスと同じ。絵を描くための土台です。今回はここにかいた内容がmBitmapに反映するよ。
        mCanvas = new Canvas(mBitmap);
        //線の太さを指定。
        mPaint.setStrokeWidth(5);
        //5.0fでも同じ意味
        //mPaint.setStrokeWidth(5.0f);

        //何を描くかを設定する。今回は線なので、以下のように設定。
        mPaint.setStyle(Paint.Style.STROKE);
        //線と線のつなぎめを丸くする。
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        //線の先端を丸くする。
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        //↑他にも、いろんな設定があるから試してみてね。
        //白い背景を描画
        mCanvas.drawColor(Color.WHITE);
        //image viewに今作ったBitmapを表示する。
        mImageView.setImageBitmap(mBitmap);
        //イメージビューをタッチできるようにする。
        mImageView.setOnTouchListener(this);
    }

    //ImageViewの上をタッチするとこのメソッドが呼ばれる。
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //まずはタッチされた場所を取得
        float x=event.getX();
        float y=event.getY();
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //今まで溜まっていた線の情報を一旦リセット。
                mPath.reset();
                //今タッチした点に始点を移動。
                mPath.moveTo(x, y);
                //x1とy1という一つ前の線の位置を保存しておく変数に、今のxとyを保存。
                x1=x;
                y1=y;
                break;
            case MotionEvent.ACTION_MOVE:
                //なめらかな曲線を引く。
                mPath.quadTo(x1, y1, x, y);
                x1=x;
                y1=y;
                mCanvas.drawPath(mPath,mPaint);
                mPath.reset();
                mPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                //なめらかな曲線を引く。
                mPath.quadTo(x1, y1, x, y);
                mCanvas.drawPath(mPath,mPaint);
                mPath.reset();
                break;
        }
        //ImageViewにセット。これをしないと線を書いたのが見えないよ。
        mImageView.setImageBitmap(mBitmap);
        return true;
    }

    @Click(R.id.canvas_imagebutton_open)
    void onOpen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent,0);
    }

    @Click(R.id.canvas_imagebutton_save)
    void onSave() {
        save();
    }

    @Click(R.id.canvas_imagebutton_color)
    void onColorChange() {
        int initColor = 0xFFFFFFFF;
        ColorPickerDialog cpDialog = new ColorPickerDialog(this,this,initColor,(int)(width/2*(0.9)),(int)(width/2*0.9));
        cpDialog.show();
        /*
        final String[] items = getResources().getStringArray(R.array.ColorName);
        final int[] colors = getResources().getIntArray(R.array.Color);
        mAlertBuilder=new AlertDialog.Builder(this);
        mAlertBuilder.setTitle(R.string.menu_color_change);
        mAlertBuilder.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                mPaint.setColor(colors[item]);
            }
        });
        mAlertBuilder.show();
        */
    }

    @Click(R.id.canvas_imagebutton_clear)
    void onClear() {
        //ダイアログを作成
        mAlertBuilder = new AlertDialog.Builder(this);
        mAlertBuilder.setTitle("かくにん");
        mAlertBuilder.setMessage("ぜんぶきえちゃうよ？");
        //OKボタンが押された時の処理
        mAlertBuilder.setPositiveButton("いいよ",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mCanvas.drawColor(Color.WHITE);
                    mImageView.setImageBitmap(mBitmap);
                }
            }
        );
        //キャンセルボタンが押された時の処理（今回は何もしない。）
        mAlertBuilder.setNegativeButton("だめ",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }
        );
        //ダイアログを表示
        mAlertBuilder.show();
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK ) {
            Uri uri = data.getData();
            try{
                mBitmap = ImageUtil.loadImage(getApplicationContext(), uri);
            }catch(IOException e){
                e.printStackTrace();
            }
            mCanvas = new Canvas(mBitmap);
            mImageView.setImageBitmap(mBitmap);
        }
    }

    @Click(R.id.canvas_button_finish)
    void onFinish() {
        String imagePath = save();

        if(imagePath != null) {
            // 返すデータ(Intent&Bundle)の作成
            Intent data = new Intent();
            //Bundle bundle = new Bundle();
            //bundle.putString("image_path", imagePath);
            data.putExtra("image_path", imagePath);
            setResult(RESULT_OK, data);
            finish();
        }

    }

    String save(){
        //sharedpreferenceはデータをずっと保存しておくための仕組み
        SharedPreferences prefs = getSharedPreferences("FingarPaintPreferences",MODE_PRIVATE);
        int imageNumber = prefs.getInt("imageNumber", 1);
        File file = null;
        DecimalFormat form = new DecimalFormat("0000");
        String dirPath = Environment.getExternalStorageDirectory()+"/MyZoo/";
        File outDir = new File(dirPath);
        if(!outDir.exists()){
            outDir.mkdir();
        }
        do{
            file = new File(dirPath+"img"+form.format(imageNumber)+".png");
            imageNumber++;
        }while(file.exists());
        if(writeImage(file)){
            scanMedia(file.getPath());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("imageNumber", imageNumber+1);
            editor.commit();
            return file.getPath();
        } else {
            return null;
        }
    }

    boolean writeImage(File file){
        try{
            FileOutputStream fo = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG,100, fo);
            fo.flush();
            fo.close();
        }catch(Exception e){
            System.out.println(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    MediaScannerConnection mc;
    void scanMedia(final String fp){
        mc = new MediaScannerConnection(this,
                new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        mc.disconnect();
                    }
                    @Override
                    public void onMediaScannerConnected() {
                        mc.scanFile(fp, "image/*");
                    }
                }
        );
        mc.connect();
    }

    @Override
    public void colorChanged(int color) {
        mPaint.setColor(color);
    }

}
