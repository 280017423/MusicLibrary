package com.zsq.musiclibrary.activity;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.widget.TextView;

import com.zsq.musiclibrary.R;
import com.zsq.musiclibrary.adapter.MusicDetailAdapter;
import com.zsq.musiclibrary.listener.IActionListener;
import com.zsq.musiclibrary.util.ConstantSet;
import com.zsq.musiclibrary.util.FileUtil;
import com.zsq.musiclibrary.util.UIUtil;
import com.zsq.musiclibrary.widget.AutoScrollViewPager;

public class MusicDetailActivity extends ActivityBase {
	private static final int SCROLL_DURATION = 4;
	private static final int DELAY_TIME = 1000;
	private static final String TAG = "MusicDetailActivity";
	private ArrayList<File> mImgsList;
	private int mPosition;
	private AutoScrollViewPager mViewPager;
	private TextView mTvCurrentTotal;
	private TextView mTvMusicName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_detail);
		initVariables();
		initViews();
		setListener();
		// 设置全屏
		// UIUtil.systemUivisibility(this, Build.VERSION.RELEASE);
	}

	private void initVariables() {
		Intent intent = getIntent();
		if (null != intent) {
			mImgsList = (ArrayList<File>) intent.getSerializableExtra(ConstantSet.KEY_INTENT_IMGS_LIST);
			mPosition = intent.getIntExtra(ConstantSet.KEY_INTENT_IMG_POSITION, 0);
		} else {
			finish();
		}
	}

	private void initViews() {
		mViewPager = (AutoScrollViewPager) findViewById(R.id.vp_photo_view);
		mTvCurrentTotal = (TextView) findViewById(R.id.tv_current_total_num);
		mTvMusicName = (TextView) findViewById(R.id.tv_current_music_name);
		mViewPager.setAdapter(new MusicDetailAdapter(this, mImgsList));
		mViewPager.setCurrentItem(mPosition, false);
		mTvCurrentTotal.setText((mPosition + 1) + "/" + mImgsList.size());
		mTvMusicName.setText(FileUtil.getFileNameNoEx(mImgsList.get(mPosition).getName()));
		mViewPager.setScrollDurationFactor(SCROLL_DURATION);
	}

	private void setListener() {
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int postion) {
				mPosition = postion;
				mTvCurrentTotal.setText((mPosition + 1) + "/" + mImgsList.size());
				mTvMusicName.setText(FileUtil.getFileNameNoEx(mImgsList.get(postion).getName()));
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	@Override
	public boolean onKeyDown(final int keyCode, KeyEvent event) {
		UIUtil.limitReClick(TAG, DELAY_TIME, new IActionListener() {

			@Override
			public void doAction() {
				int currentPosition = mViewPager.getCurrentItem();
				if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
					if (currentPosition < mImgsList.size() - 1) {
						mViewPager.setScrollDurationFactor(SCROLL_DURATION);
						mViewPager.setCurrentItem(currentPosition + 1, true);
					}
				} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
					if (0 < currentPosition) {
						mViewPager.setScrollDurationFactor(SCROLL_DURATION);
						mViewPager.setCurrentItem(currentPosition - 1, true);
					}
				}
			}
		});
		return super.onKeyDown(keyCode, event);
	}
}
