package com.zsq.musiclibrary.activity;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zsq.musiclibrary.R;
import com.zsq.musiclibrary.util.ConstantSet;
import com.zsq.musiclibrary.util.FileUtil;
import com.zsq.musiclibrary.util.SharedPreferenceUtil;
import com.zsq.musiclibrary.util.StringUtil;

public class AboutActivity extends ActivityBase implements OnClickListener {

	private LinearLayout mLlBack;
	private LinearLayout mLlPath;
	private EditText mEdtPath;
	private String mVersionName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		initVariables();
		initView();
		setListener();
	}

	private void initVariables() {
		mVersionName = getString(R.string.unknow_version_name);
		try {
			mVersionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void setListener() {
		mLlBack.setOnClickListener(this);
		findViewById(R.id.iv_icon).setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				mLlPath.setVisibility(View.VISIBLE);
				mEdtPath.setText(FileUtil.getResDir(AboutActivity.this).getAbsolutePath());
				return true;
			}
		});
	}

	private void initView() {

		TextView titleTextView = (TextView) findViewById(R.id.title_with_back_title_btn_mid);
		titleTextView.setText(R.string.about);
		mLlBack = (LinearLayout) findViewById(R.id.title_with_back_title_btn_left);
		mLlPath = (LinearLayout) findViewById(R.id.ll_path_layout);
		mEdtPath = (EditText) findViewById(R.id.edt_path_name);
		TextView mTvBack = (TextView) findViewById(R.id.tv_title_with_back_left);
		mTvBack.setText(R.string.title_back_text);
		mTvBack.setBackgroundResource(R.drawable.btn_back_bg);
		TextView tvVersion = (TextView) findViewById(R.id.tv_version_code);
		tvVersion.setText(getString(R.string.text_version_code, mVersionName));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_with_back_title_btn_left:
				back();
				break;
			case R.id.btn_path_name:
				String path = mEdtPath.getText().toString().trim();
				if (StringUtil.isNullOrEmpty(path)) {
					Toast.makeText(this, getString(R.string.input_custom_dir), Toast.LENGTH_SHORT).show();
				} else {
					boolean isLandscape = SharedPreferenceUtil.getBooleanValueByKey(this, ConstantSet.CONFIG_FILE,
							ConstantSet.KEY_IS_LANDSCAPE);
					if (isLandscape) {
						SharedPreferenceUtil.saveValue(AboutActivity.this, ConstantSet.CONFIG_FILE,
								ConstantSet.CUSTOM_HENGPU_DIR, path);
					} else {
						SharedPreferenceUtil.saveValue(AboutActivity.this, ConstantSet.CONFIG_FILE,
								ConstantSet.CUSTOM_SHUPU_DIR, path);
					}
					setResult(RESULT_OK);
					finish();
				}

				break;
			default:
				break;
		}
	}

	@Override
	public void onBackPressed() {
		back();
		super.onBackPressed();
	}

	private void back() {
		setResult(RESULT_CANCELED);
		finish();
	}
}
