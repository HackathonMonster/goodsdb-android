package com.zeroone_creative.myzoo.view.fragment;

import android.app.Fragment;
import android.content.Intent;

import com.zeroone_creative.myzoo.R;
import com.zeroone_creative.myzoo.view.activity.DetailsActivity_;
import com.zeroone_creative.myzoo.view.activity.GalleryActivity_;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

@EFragment(R.layout.fragment_main)
public class MainFragment extends Fragment {

    @Click(R.id.main_rippleview_capture)
    void moveCapture() {
        Intent intent = new Intent(getActivity().getApplicationContext(), DetailsActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.main_rippleview_watch)
    void moveLook() {
        Intent intent = new Intent(getActivity().getApplicationContext(), GalleryActivity_.class);
        startActivity(intent);
    }

}
