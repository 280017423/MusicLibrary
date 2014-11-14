package com.zsq.musiclibrary.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.zsq.musiclibrary.R;

public class PhotoActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		getSupportFragmentManager().beginTransaction().replace(R.id.ac_photo_frame_main, new CameraFragment(), "")
				.commitAllowingStateLoss();
		getSupportFragmentManager().executePendingTransactions();
	}
}
