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
import android.widget.TextView;

import com.zeroone_creative.goodsdb.R;

public class MessageDialogFragment extends DialogFragment implements OnClickListener{
	
	private static String TAG_TITLE = "tag_title";
    private static String TAG_BUTTON_POSITIVE = "tag_button_positive";

    public interface MessageDialogCallback{
        void onMessageDialogCallback();
    }

    private MessageDialogCallback mCallback;

	public static MessageDialogFragment newInstance(String title, String positiveButton){
		MessageDialogFragment fragment = new MessageDialogFragment();
		Bundle args = new Bundle();
		args.putString(TAG_TITLE, title);
        args.putString(TAG_BUTTON_POSITIVE, positiveButton);
		fragment.setArguments(args);
		return fragment;
	}

    public void setCallback(MessageDialogCallback callback) {
        this.mCallback = callback;
    }

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity());
		// タイトル非表示
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // フルスクリーン
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setContentView(R.layout.fragment_message_dialog);
        // 背景を透明にする
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
        Bundle args = getArguments();
        
		TextView titleTextView = (TextView)dialog.findViewById(R.id.message_dialog_textview_title);
		titleTextView.setText(args.getString(TAG_TITLE));
		Button positiveButton = (Button) dialog.findViewById(R.id.message_dialog_button_confilm);
        positiveButton.setText(args.getString(TAG_BUTTON_POSITIVE));
        positiveButton.setOnClickListener(this);

        Button cancelButton = (Button)dialog.findViewById(R.id.message_dialog_button_cancel);
		cancelButton.setOnClickListener(this);
        cancelButton.setVisibility(View.GONE);
        dialog.findViewById(R.id.message_dialog_view_sepalate).setVisibility(View.GONE);

		return dialog;
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.message_dialog_button_confilm:
            if(mCallback != null) {
                mCallback.onMessageDialogCallback();

            }
            this.dismiss();
			break;
		case R.id.message_dialog_button_cancel:
            this.dismiss();
			break;
		}
	}
}
