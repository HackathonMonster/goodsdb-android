package com.zeroone_creative.myzoo.view.fragment;

import android.app.Fragment;
import android.widget.TextView;

import com.zeroone_creative.myzoo.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_pager)
public class PagerFragment extends Fragment {

    @FragmentArg("section_number")
    int mSectionNumber;

    @ViewById(R.id.pager_textview_label)
    TextView mLabelTextView;

    @AfterViews
    void setUI() {
        mLabelTextView.setText("SectionNumber:"+Integer.toString(mSectionNumber));
    }

}