package com.zsq.musiclibrary.widget;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zsq.musiclibrary.R;
import com.zsq.musiclibrary.util.StringUtil;

/**
 * 加载等待框 此等待框基于单个Activity内部显示
 * 
 * @version 2012-11-1 下午1:31:10 xu.xb
 */
public class LoadingUpView {
	private static final String TAG = "EvtLoadingUpView";
	// 限制用户操作时间
	private static final int Default_DialogTimeOut = 300;
	private static final int SRART_ANIM = 3;
	private static final int ERROR = 4;
	private static final int DISMISS = 5;
	private static final int SHOW_DIALOG = 6;
	private static final int Handler_TimeTask_Timeout = 7;
	private int DialogTimeOut = Default_DialogTimeOut;
	private LinearLayout mLoadingView;
	private Animation mAnim;
	private ImageView mIvLoading;
	private TextView mTvMsg;
	private Activity mCurrentActivity;
	private Boolean mIsShowing = false;
	private View mParentView;
	private ProgressDialog mProgressdDialog;
	private boolean mIsFrameLayoutParentView;

	private int mScreenWidth;
	private int mScreenHeight;
	private int mStatusbarHeight;
	// 是否为限制用户操作view，为透明，显示后一定时间自动隐藏
	private boolean mIsLimitView;

	private DialogTimeTask mDialogTimeTask;

	// 是否阻塞用户操作
	private boolean mIsBlock;
	// 是否按返回键可以消失，使用阻塞时设置此参数
	private boolean mCancelable;
	// 按返回键关闭Activity
	private boolean mCancelCloseActivity;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "handleMessage, " + msg.what);
			switch (msg.what) {
				case SRART_ANIM:
					if (mIvLoading != null) {
						Log.d(TAG, "开始加载动画");
						mIvLoading.startAnimation(mAnim);
						// 获取焦点，阻塞用户操作
						// if (mIsBlock) {
						// mParentView.setFocusable(true);
						// mParentView.setFocusableInTouchMode(true);
						// mLoadingView.requestFocus();
						// mLoadingView.setOnClickListener(new OnClickListener()
						// {
						// @Override
						// public void onClick(View v) {
						// }
						// });
						// }
					}
					break;
				case ERROR:
					break;
				case DISMISS:
					if (mIsBlock) {
						if (mProgressdDialog != null) {
							mProgressdDialog.dismiss();
							mProgressdDialog = null;
						}
					} else {
						removeLoadingView();
						mLoadingView = null;
					}
					break;
				case SHOW_DIALOG:
					showProgressDialog(mCurrentActivity, String.valueOf(msg.obj));
					break;
				case Handler_TimeTask_Timeout:
					if (mProgressdDialog != null) {
						mProgressdDialog.dismiss();
						mProgressdDialog = null;
					}
					cancelTimeTask();
					break;
				default:
					break;
			}
		}
	};

	/**
	 * 构造方法
	 * 
	 * @param act
	 *            Activity
	 * @param isBlock
	 *            是否阻塞用户操作
	 */
	public LoadingUpView(Activity act, boolean isBlock) {
		this.mIsBlock = isBlock;
		init(act);
	}

	/**
	 * 构造方法
	 * 
	 * @param act
	 *            Activity
	 */
	public LoadingUpView(Activity act) {
		init(act);
	}

	/**
	 * 
	 * setmIsLimitView 是否是显示限制用户点击view
	 * 
	 * @param mIsLimitView
	 * @return void
	 */
	public void setmIsLimitView(boolean mIsLimitView) {
		this.mIsLimitView = mIsLimitView;
		this.mIsBlock = mIsLimitView;
	}

	public void setmIsLimitAndShowTime(boolean mIsLimitView, int dialogTimeOut) {
		this.mIsLimitView = mIsLimitView;
		this.mIsBlock = mIsLimitView;
		this.DialogTimeOut = dialogTimeOut;
	}

	/**
	 * 初始化
	 * 
	 * @param act
	 *            Activity
	 */
	private void init(Activity act) {
		this.mCurrentActivity = act;
		if (act.getWindow() == null) {
			return;
		}
		mParentView = act.getWindow().getDecorView().getRootView();
		if (mParentView instanceof FrameLayout) {
			mIsFrameLayoutParentView = true;
		} else {
			mIsFrameLayoutParentView = false;
		}
	}

	private void initView() {
		LayoutInflater inflater = (LayoutInflater) mCurrentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLoadingView = (LinearLayout) inflater.inflate(R.layout.view_popup, null);
		mTvMsg = (TextView) mLoadingView.findViewById(R.id.tv_popup);
		mIvLoading = (ImageView) mLoadingView.findViewById(R.id.img_popup);
		mAnim = AnimationUtils.loadAnimation(mCurrentActivity, R.anim.popup_loading);
	}

	/**
	 * 显示
	 * 
	 */
	public void showPopup() {
		// showPopup(PackageUtil.getString(R.string.downloading_data));
		showPopup("");
	}

	/**
	 * show EvtLoadingUpView
	 * 
	 * @param msg
	 *            内容
	 */
	public void showPopup(final String msg) {
		// 在show之前判断是否已经关闭
		if (mCurrentActivity != null && mCurrentActivity.isFinishing()) {
			return;
		}
		// 阻塞
		if (mIsBlock) {
			if (mIsLimitView) {
				setTimeTask();
			}
			changeShowStatus(true);
			Message mMsg = new Message();
			mMsg.what = SHOW_DIALOG;
			mMsg.obj = msg;
			mHandler.sendMessage(mMsg);
		} else {
			if (mParentView == null) {
				return;
			}
			if (mLoadingView == null) {
				initView();
			}
			removeLoadingView();
			addLoadingView(msg);
		}

	}

	/**
	 * dismiss EvtLoadingUpView
	 */
	public void dismiss() {
		Log.d(TAG, "dismiss");
		if (mIsShowing) {
			changeShowStatus(false);
			mHandler.sendEmptyMessage(DISMISS);
		}
	}

	/**
	 * 
	 * 设置是否阻塞用户操作，在使用showPopup方法前调用
	 * 
	 * @param isBlock
	 *            是否阻塞用户操作
	 */
	public void setIsBlock(boolean isBlock) {
		this.mIsBlock = isBlock;
	}

	/**
	 * 是否按返回键可以消失，使用阻塞时设置此参数
	 * 
	 * @param mCancelable
	 *            是否按返回键可以消失
	 */
	public void setCancelable(boolean mCancelable) {
		this.mCancelable = mCancelable;
	}

	/**
	 * 是否按返回键关闭Activity(show之前调用)
	 * 
	 * @param cancelCloseActivity
	 *            是否关闭Activity
	 */
	public void setCancelCloseActivity(boolean cancelCloseActivity) {
		this.mCancelCloseActivity = cancelCloseActivity;
	}

	private void removeLoadingView() {
		if (mIsFrameLayoutParentView) {
			((FrameLayout) mParentView).removeView(mLoadingView);
		}
	}

	private void addLoadingView(String msg) {
		msg = "";
		if (mIsFrameLayoutParentView) {
			if (mScreenWidth == 0) {
				getDisplayConfig();
			}
			((FrameLayout) mParentView).addView(mLoadingView, new FrameLayout.LayoutParams(mScreenWidth, mScreenHeight
					- mStatusbarHeight));
			if (!mIsLimitView) {
				mIvLoading.setVisibility(View.VISIBLE);
				if (StringUtil.isNullOrEmpty(msg)) {
					mTvMsg.setVisibility(View.GONE);
				} else {
					mTvMsg.setVisibility(View.VISIBLE);
					mTvMsg.setText(msg);
				}
			} else {
				mIvLoading.setVisibility(View.INVISIBLE);
				mTvMsg.setVisibility(View.INVISIBLE);
			}

			changeShowStatus(true);
			mHandler.sendEmptyMessage(SRART_ANIM);
		}
	}

	/**
	 * onResume 非阻塞式 时需使用
	 * 
	 * @Description: 保持loadingview状态 在Activity onResume() 方法中调用
	 */
	public void onResume() {
		if (mIsShowing && mLoadingView != null) {
			mHandler.sendEmptyMessage(SRART_ANIM);
		}
	}

	/**
	 * 返回EvtLoadingUpView是否显示
	 * 
	 * @return EvtLoadingUpView是否显示
	 */
	public boolean isShowing() {
		return mIsShowing;
	}

	private void changeShowStatus(boolean bShowing) {
		this.mIsShowing = bShowing;
	}

	/**
	 * showProgressDialog 创建显示
	 * 
	 * @param act
	 *            Activity
	 * @param msg
	 *            消息
	 */
	public void showProgressDialog(final Activity act, String msg) {
		Activity activity = act;
		msg = "";
		if (act.isFinishing()) {
			return;
		}
		if (act.getParent() != null) {
			activity = act.getParent();
		}
		if (activity.getParent() != null) {
			activity = activity.getParent();
		}
		if (mProgressdDialog == null) {
			mProgressdDialog = new ProgressDialog(activity);
			// 如果退出要关闭Activity，就不能设置是否取消，一定可以取消
			if (mCancelCloseActivity) {
				mProgressdDialog.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface arg0) {
						if (act != null && !act.isFinishing()) {
							act.finish();
						}
					}
				});
			}
			// 在show之前判断是否已经关闭
			if (activity.isFinishing()) {
				return;
			}
			mProgressdDialog.show();
			LayoutInflater mLayoutInflater = LayoutInflater.from(activity);
			View view = mLayoutInflater.inflate(R.layout.view_popup, null);
			ImageView mRetote = (ImageView) view.findViewById(R.id.img_popup);
			TextView mLoadMsg = (TextView) view.findViewById(R.id.tv_popup);

			if (!mIsLimitView) {
				mRetote.setVisibility(View.VISIBLE);
				if (mAnim == null) {
					mAnim = AnimationUtils.loadAnimation(mCurrentActivity, R.anim.popup_loading);
				}
				mAnim.setInterpolator(new LinearInterpolator());
				mRetote.startAnimation(mAnim);
				if (StringUtil.isNullOrEmpty(msg)) {
					mLoadMsg.setVisibility(View.GONE);
				} else {
					mLoadMsg.setVisibility(View.VISIBLE);
					mLoadMsg.setText(msg);
				}
			} else {
				mLoadMsg.setVisibility(View.INVISIBLE);
				mRetote.setVisibility(View.INVISIBLE);
			}
			Window window = mProgressdDialog.getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.alpha = 1f;
			lp.dimAmount = 0.000001f; // 修改在2.1和2.2的模拟器上黑屏的问题
			window.setAttributes(lp);
			window.setContentView(view);

			mProgressdDialog.setIndeterminate(true);
			// 如果退出要关闭Activity，就不能设置是否取消，一定可以取消
			if (!mCancelCloseActivity) {
				mProgressdDialog.setCancelable(mCancelable);
			} else {
				mProgressdDialog.setCancelable(true);
			}
			mProgressdDialog.setCanceledOnTouchOutside(false);
		}
	}

	/**
	 * 超时计时器
	 */
	private void setTimeTask() {
		cancelTimeTask();
		Timer mTimer = new Timer();
		mDialogTimeTask = new DialogTimeTask();
		Log.d(TAG, "启动计时器...");
		mTimer.schedule(mDialogTimeTask, DialogTimeOut);
	}

	private void cancelTimeTask() {
		if (mDialogTimeTask != null) {
			Log.d(TAG, "关闭计时器...");
			mDialogTimeTask.cancel();
			mDialogTimeTask = null;
		}
	}

	class DialogTimeTask extends TimerTask {

		/**
		 * run
		 * 
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			Message msg = mHandler.obtainMessage(Handler_TimeTask_Timeout);
			mHandler.sendMessage(msg);
		}
	}

	/*
	 * 获取分辨率
	 */
	private void getDisplayConfig() {

		DisplayMetrics dm = new DisplayMetrics();
		((Activity) mCurrentActivity).getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 宽度
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;
		Rect frame = new Rect();
		mCurrentActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		mStatusbarHeight = frame.top;

	}
}
