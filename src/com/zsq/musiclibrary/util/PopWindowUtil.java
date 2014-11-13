package com.zsq.musiclibrary.util;

import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.zsq.musiclibrary.listener.OnPopDismissListener;

/**
 * PoPWindow 弹出窗口控件
 * 
 * @author zou.sq
 * 
 */
public class PopWindowUtil {
	private View mMenuView;
	private PopupWindow mPopupWindow;
	private View mView;
	private OnPopDismissListener mListener;

	/**
	 * @param contentView
	 *            需要显示的view
	 * @param view
	 *            PopWindow相对位置的视图
	 * @param listener
	 *            popwindow消失监听
	 */
	public PopWindowUtil(View contentView, View view, OnPopDismissListener listener) {
		this.mMenuView = contentView;
		mView = view;
		mListener = listener;
		initView();
	}

	private void initView() {
		mPopupWindow = new PopupWindow(mMenuView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mPopupWindow.setContentView(mMenuView);
		mPopupWindow.setFocusable(true);
		// 点击popupwindow窗口之外的区域popupwindow消失
		ColorDrawable dw = new ColorDrawable(0x00);
		mPopupWindow.setBackgroundDrawable(dw);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				if (null != mListener) {
					mListener.onDismiss();
				}
			}
		});
	}

	/**
	 * 
	 * @Description 显示
	 * @return void
	 * @Date 2014-9-10 下午1:32:09
	 * 
	 */
	public void show() {
		if (null != mPopupWindow) {
			mPopupWindow.showAtLocation(mView, Gravity.CENTER, 0, 0);
			mPopupWindow.update();
		}
	}

	/**
	 * 
	 * @Description 消失
	 * @Author admin
	 * @Date 2014-8-26 下午4:11:10
	 * 
	 */
	public void dissmiss() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
			mPopupWindow.update();
		}
	}
}