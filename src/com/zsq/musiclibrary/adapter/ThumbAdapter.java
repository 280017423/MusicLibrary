/**
 * @Title: DishEmptyAdapter.java
 * @Project DCB
 * @Package com.pdw.dcb.ui.adapter
 * @Description: 沽清列表
 * @author zeng.ww
 * @date 2012-12-10 下午04:37:29
 * @version V1.0
 */
package com.zsq.musiclibrary.adapter;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zsq.musiclibrary.R;
import com.zsq.musiclibrary.listener.ImageLoadListener;
import com.zsq.musiclibrary.util.AsyncImageLoader;
import com.zsq.musiclibrary.util.ConstantSet;
import com.zsq.musiclibrary.util.SharedPreferenceUtil;
import com.zsq.musiclibrary.util.UIUtil;
import com.zsq.musiclibrary.widget.HorizontalListView;

/**
 * 文件列表适配器
 * 
 * @author zou.sq
 * @since 2013-03-12 下午04:37:29
 * @version 1.0
 */
public class ThumbAdapter extends BaseAdapter {
	private final int SPACE_VALUE = 10;
	private int NUM_COLUMNS = 4;
	private List<File> mFilesList;
	private Activity mContext;
	private int mImgSize;
	private HorizontalListView mListView;
	private AsyncImageLoader mImageLoader;

	/**
	 * 实例化对象
	 * 
	 * @param context
	 *            上下文
	 * @param dataList
	 *            数据列表
	 */
	public ThumbAdapter(Activity context, List<File> dataList, HorizontalListView listView) {
		this.mContext = context;
		this.mFilesList = dataList;
		mListView = listView;
		mImageLoader = new AsyncImageLoader();
		boolean isLandscape = SharedPreferenceUtil.getBooleanValueByKey(context, ConstantSet.CONFIG_FILE,
				ConstantSet.KEY_IS_LANDSCAPE);
		if (isLandscape) {
			NUM_COLUMNS = 8;
		}
		DisplayMetrics metric = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		mImgSize = (width - UIUtil.dip2px(mContext, SPACE_VALUE) * (NUM_COLUMNS + 1)) / NUM_COLUMNS;
	}

	@Override
	public int getCount() {
		if (mFilesList != null && !mFilesList.isEmpty()) {
			return mFilesList.size();
		}
		return 0;
	}

	@Override
	public File getItem(int position) {
		if (mFilesList != null) {
			return mFilesList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		viewHode view = new viewHode();
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.view_thumb_item, null);
			view.mName = (TextView) convertView.findViewById(R.id.tv_thumb_name);
			view.mIcon = (ImageView) convertView.findViewById(R.id.iv_thumb_photo);
			convertView.setTag(view);
		} else {
			view = (viewHode) convertView.getTag();
		}

		LayoutParams layoutParams = view.mIcon.getLayoutParams();
		layoutParams.width = mImgSize;
		layoutParams.height = mImgSize;
		view.mIcon.setLayoutParams(layoutParams);

		File file = mFilesList.get(position);
		view.mIcon.setTag(file.getAbsolutePath());
		if (null != file) {
			view.mIcon.setImageResource(R.drawable.format_picture);
			mImageLoader.loadDrawable(file.getAbsolutePath(), mImgSize, new ImageLoadListener() {

				@Override
				public void imageLoaded(Bitmap bitmap, String imageUrl) {
					ImageView imageViewByTag = (ImageView) mListView.findViewWithTag(imageUrl);
					if (imageViewByTag != null) {
						imageViewByTag.setImageBitmap(bitmap);
					}
				}
			});
			view.mName.setText(file.getName());
		}
		return convertView;
	}

	class viewHode {
		TextView mName;
		ImageView mIcon;
	}
}
