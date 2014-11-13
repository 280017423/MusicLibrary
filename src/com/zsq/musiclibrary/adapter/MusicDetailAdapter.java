package com.zsq.musiclibrary.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.zsq.musiclibrary.util.ImageUtil;
import com.zsq.musiclibrary.util.StringUtil;

/**
 * 乐谱详细列表适配器
 * 
 * @author zou.sq
 * @since 2013-03-12 下午04:37:29
 * @version 1.0
 */
public class MusicDetailAdapter extends PagerAdapter {

	private List<File> mNewsList;
	private Activity mContext;
	private View mCurrentView;

	/**
	 * 实例化对象
	 * 
	 * @param context
	 *            上下文
	 * @param dataList
	 *            数据列表
	 */
	public MusicDetailAdapter(Activity context, ArrayList<File> dataList) {
		this.mContext = context;
		this.mNewsList = dataList;
	}

	@Override
	public int getCount() {
		if (null == mNewsList) {
			return 0;
		}
		return mNewsList.size();
	}

	public File getItem(int position) {
		if (mNewsList != null) {
			return mNewsList.get(position);
		}
		return null;
	}

	@Override
	public View instantiateItem(ViewGroup container, int position) {
		final ImageView photoView = new ImageView(mContext);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		photoView.setLayoutParams(params);
		photoView.setScaleType(ScaleType.FIT_CENTER);
		File imgFile = getItem(position);
		if (null != imgFile && !StringUtil.isNullOrEmpty(imgFile.getAbsolutePath())) {
			String url = imgFile.getAbsolutePath();
			Bitmap bitmap = ImageUtil.readBitMap(url);
			photoView.setImageBitmap(bitmap);
		} else {
			photoView.setVisibility(View.GONE);
		}
		container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		return photoView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
		mCurrentView = (View) object;
	}

	public View getPrimaryItem() {
		return mCurrentView;
	}

}
