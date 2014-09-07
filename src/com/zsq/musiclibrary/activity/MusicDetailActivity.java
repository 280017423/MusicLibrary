package com.zsq.musiclibrary.activity;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.TextView;

import com.zsq.musiclibrary.R;
import com.zsq.musiclibrary.adapter.MusicDetailAdapter;
import com.zsq.musiclibrary.util.ConstantSet;

public class MusicDetailActivity extends ActivityBase {
	private ArrayList<File> mImgsList;
	private int mPosition;
	private ViewPager mViewPager;
	private TextView mTvCurrentTotal;
	private TextView mTvMusicName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_detail);
		initVariables();
		initViews();
		setListener();
	}

	private void initVariables() {
		Intent intent = getIntent();
		if (null != intent) {
			mImgsList = (ArrayList<File>) intent
					.getSerializableExtra(ConstantSet.KEY_INTENT_IMGS_LIST);
			mPosition = intent.getIntExtra(ConstantSet.KEY_INTENT_IMG_POSITION,
					0);
		} else {
			finish();
		}
	}

	private void initViews() {
		mViewPager = (ViewPager) findViewById(R.id.vp_photo_view);
		mTvCurrentTotal = (TextView) findViewById(R.id.tv_current_total_num);
		mTvMusicName = (TextView) findViewById(R.id.tv_current_music_name);
		mViewPager.setAdapter(new MusicDetailAdapter(this, mImgsList));
		mViewPager.setCurrentItem(mPosition, false);
		mTvCurrentTotal.setText((mPosition + 1) + "/" + mImgsList.size());
		mTvMusicName.setText(mImgsList.get(mPosition).getName());
		// mTvCurrentTotal.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
		// }
		// });

	}

	private void setListener() {
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int postion) {
				mPosition = postion;
				mTvCurrentTotal.setText((mPosition + 1) + "/"
						+ mImgsList.size());
				mTvMusicName.setText(mImgsList.get(postion).getName());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}

}
