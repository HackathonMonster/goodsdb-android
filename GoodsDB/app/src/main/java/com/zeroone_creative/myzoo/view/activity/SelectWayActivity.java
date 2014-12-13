package com.zeroone_creative.myzoo.view.activity;

import android.app.Activity;
import android.content.Intent;

import com.zeroone_creative.myzoo.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_select_way)
public class SelectWayActivity extends Activity {

    private static int CANVAS_ACTIVITY = 101;
    private static int TRIMING_ACTIVITY = 202;


    @Click(R.id.select_way_rippleview_camera)
    void getImageCamera() {
        Intent intent = new Intent(getApplicationContext(), TrimingActivity_.class);
        startActivityForResult(intent, TRIMING_ACTIVITY);
    }

    @Click(R.id.select_way_rippleview_draw)
    void getDraw() {
        Intent intent = new Intent(getApplicationContext(), CanvasActivity_.class);
        startActivityForResult(intent, CANVAS_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( (requestCode == TRIMING_ACTIVITY || requestCode == CANVAS_ACTIVITY) && resultCode == RESULT_OK ) {
            if(data != null) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }



}
