package com.zsq.musiclibrary.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.zsq.musiclibrary.listener.IDialogProtocol;
import com.zsq.musiclibrary.util.DialogManager;
import com.zsq.musiclibrary.widget.CustomDialog.Builder;

public class ActivityBase extends Activity implements IDialogProtocol {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Builder createDialogBuilder(Context context, String title, String message, String positiveBtnName,
			String negativeBtnName) {
		return DialogManager
				.createMessageDialogBuilder(context, title, message, positiveBtnName, negativeBtnName, this);
	}

	@Override
	public void onPositiveBtnClick(int id, DialogInterface dialog, int which) {

	}

	@Override
	public void onNegativeBtnClick(int id, DialogInterface dialog, int which) {

	}

}
