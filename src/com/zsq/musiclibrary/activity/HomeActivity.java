package com.zsq.musiclibrary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zsq.musiclibrary.R;
import com.zsq.musiclibrary.util.ConstantSet;
import com.zsq.musiclibrary.util.SharedPreferenceUtil;

public class HomeActivity extends ActivityBase implements OnClickListener {
	private static final long WAIT_TIME = 2000;
	private long mTouchTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		testJump();
	}

	private void testJump() {
		//
	}

	@Override
	public void onBackPressed() {
		long currentTime = System.currentTimeMillis();
		if ((currentTime - mTouchTime) >= WAIT_TIME) {
			Toast.makeText(this, getString(R.string.once_press_quit), Toast.LENGTH_SHORT).show();
			mTouchTime = currentTime;
			return;
		} else {
			MobclickAgent.onKillProcess(this);
			finish();
		}
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_landscape:
				SharedPreferenceUtil.saveValue(HomeActivity.this, ConstantSet.CONFIG_FILE,
						ConstantSet.KEY_IS_LANDSCAPE, true);
				startActivity(new Intent(HomeActivity.this, MainActivity.class));
				break;
			case R.id.btn_portrait:
				SharedPreferenceUtil.saveValue(HomeActivity.this, ConstantSet.CONFIG_FILE,
						ConstantSet.KEY_IS_LANDSCAPE, false);
				startActivity(new Intent(HomeActivity.this, MainActivity.class));
				break;
			case R.id.btn_about:
				startActivity(new Intent(HomeActivity.this, AboutActivity.class));
				break;

			default:
				break;
		}
	}

}
