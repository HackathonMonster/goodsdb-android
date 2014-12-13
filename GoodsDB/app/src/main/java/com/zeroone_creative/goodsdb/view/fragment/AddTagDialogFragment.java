package com.zeroone_creative.goodsdb.view.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.zeroone_creative.goodsdb.R;

public class AddTagDialogFragment extends DialogFragment implements OnClickListener {

    private static final String ARGS_CONTENT = "args_content";

    public interface AddTagDialogCallback {
        void onTagDialogCallback(String content);
        void onCancelDialogCallback();
    }

    private AddTagDialogCallback mCallback;
    private EditText mContentEditText;

	public static AddTagDialogFragment newInstance(String content) {
		AddTagDialogFragment fragment = new AddTagDialogFragment();
		Bundle args = new Bundle();
        if(content==null) {
            args.putString(ARGS_CONTENT, "");
        } else {
            args.putString(ARGS_CONTENT, content);
        }

		fragment.setArguments(args);
		return fragment;
	}

    public void setCallback(AddTagDialogCallback callback) {
        this.mCallback = callback;
    }

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity());
		// タイトル非表示
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // フルスクリーン
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.fragment_add_tag_dialog);
        // 背景を透明にする
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Bundle args = getArguments();
        mContentEditText = (EditText) dialog.findViewById(R.id.add_tag_dialog_edittext_content);
        mContentEditText.setText(args.getString(ARGS_CONTENT));
		Button positiveButton = (Button) dialog.findViewById(R.id.add_tag_dialog_button_confilm);
        positiveButton.setOnClickListener(this);
        Button cancelButton = (Button)dialog.findViewById(R.id.add_tag_dialog_button_cancel);
		cancelButton.setOnClickListener(this);

		return dialog;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.add_tag_dialog_button_confilm:
            if(mCallback != null) {
                mCallback.onTagDialogCallback(mContentEditText.getText().toString());
            }
            this.dismiss();
			break;
		case R.id.add_tag_dialog_button_cancel:
            if(mCallback != null) {
                mCallback.onCancelDialogCallback();
            }
            this.dismiss();
			break;
		}
	}
}
