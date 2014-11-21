package com.zsq.musiclibrary.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zsq.musiclibrary.R;
import com.zsq.musiclibrary.util.FileUtil;
import com.zsq.musiclibrary.util.ImageUtil;
import com.zsq.musiclibrary.util.StringUtil;
import com.zsq.musiclibrary.util.UIUtil;
import com.zsq.musiclibrary.widget.BadgeView;
import com.zsq.musiclibrary.widget.CustomDialog.Builder;

public class CameraActivity extends ActivityBase implements SurfaceHolder.Callback, OnClickListener {

	public static final int DIALOG_ACTION_SAVE = 1;
	public static final int ACTION_SAVE_PHOTO_SUCCESS = 2;
	public static final int IMG_PICTURE_WIDTH = 768;
	public static final int IMG_PICTURE_HEIGHT = 1240;
	public static final int ANIMATION_DURATION = 500;
	public static final int THUMBNAIL_SIZE = 80;
	private ArrayList<File> mPhotosList;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Camera mCamera;
	private boolean mHasStartPreview;
	private MediaPlayer mMediaPlayer;
	private EditText mEdtFileName;
	private ImageView mIvThumb;
	private TextView mBtnTakePhone;
	private BadgeView mBadgeView;
	private PictureCallback mPictureCallBack = new Camera.PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			new SavePictureTask().execute(data);
		}
	};

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case ACTION_SAVE_PHOTO_SUCCESS:
					if (null != msg.obj) {
						Bitmap thumbnail = (Bitmap) msg.obj;
						mIvThumb.setImageBitmap(thumbnail);
					}
					if (View.VISIBLE != mIvThumb.getVisibility()) {
						startAnim();
					}
					refreashThumbView();
					refreashBadgeView();
					break;

				default:
					break;
			}
			super.handleMessage(msg);
		}

	};

	private void startAnim() {
		final ScaleAnimation animation = new ScaleAnimation(
				0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(ANIMATION_DURATION);
		mIvThumb.setAnimation(animation);
		mBadgeView.setAnimation(animation);
		animation.start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_camera);
		mCamera = Camera.open();
		mPhotosList = new ArrayList<File>();
		initViews();
	}

	private void initViews() {
		mBtnTakePhone = (TextView) findViewById(R.id.btn_take_photo);
		mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mIvThumb = (ImageView) findViewById(R.id.iv_thumb_photo);
		mBadgeView = new BadgeView(this, mIvThumb);
		mBadgeView.setGravity(Gravity.CENTER);
		mBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		mBadgeView.setBackgroundResource(R.drawable.choose_no_bg);
		mBadgeView.setBadgeMargin(10, 8);
		refreashBadgeView();
	}

	private void refreashThumbView() {
		if (null == mPhotosList || mPhotosList.isEmpty()) {
			mIvThumb.setVisibility(View.GONE);
		} else {
			mIvThumb.setVisibility(View.VISIBLE);
		}
	}

	private void refreashBadgeView() {
		if (null == mPhotosList || mPhotosList.isEmpty()) {
			mBadgeView.hide();
		} else {
			int size = mPhotosList.size();
			mBadgeView.show();
			mBadgeView.setText(size + "");
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		initCamera();
	}

	private void initCamera() {
		if (mCamera == null) {
			mCamera = Camera.open();
		}
		Camera.Parameters parameters = mCamera.getParameters();
		mCamera.setParameters(parameters);
		mCamera.startPreview();
		mCamera.setDisplayOrientation(90);
		mHasStartPreview = true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			mCamera.release();
			mCamera = null;
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mHasStartPreview) {
			mCamera.stopPreview();
		}
		try {
			mCamera.release();
			mCamera = null;
			mHasStartPreview = false;
		} catch (Exception e) {
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mCamera == null)
			return true;
		if (event.getAction() == MotionEvent.ACTION_UP) {
			mCamera.autoFocus(new AutoFocusCallback() {
				@Override
				public void onAutoFocus(boolean success, Camera arg1) {
					if (success) {
					}
				}
			});

		}
		return true;
	}

	class SavePictureTask extends AsyncTask<byte[], String, Bitmap> {

		@Override
		protected Bitmap doInBackground(byte[]... params) {
			Bitmap thumbnail = null;
			FileOutputStream fos = null;
			if (mCamera != null) {
				try {
					File file = ImageUtil.getOutputMediaFile(CameraActivity.this);
					fos = new FileOutputStream(file);
					Bitmap bitmap = Bitmap.createScaledBitmap(
							BitmapFactory.decodeByteArray(params[0], 0, params[0].length), IMG_PICTURE_HEIGHT,
							IMG_PICTURE_WIDTH, false);
					Matrix m = new Matrix();
					int width = bitmap.getWidth();
					int height = bitmap.getHeight();
					m.setRotate(90); // 旋转90度
					Bitmap newBitMap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);// 从新生成图片
					bitmap.recycle();
					newBitMap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
					mPhotosList.add(file);
					thumbnail = ImageUtil.getImageThumbnail(file.getAbsolutePath(),
							UIUtil.dpToPx(getResources(), THUMBNAIL_SIZE),
							UIUtil.dpToPx(getResources(), THUMBNAIL_SIZE));
					newBitMap.recycle();
					initCamera();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						fos.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return thumbnail;
		}

		@Override
		protected void onPostExecute(Bitmap thumbnail) {
			mBtnTakePhone.setEnabled(true);
			Message msg = mHandler.obtainMessage();
			msg.what = ACTION_SAVE_PHOTO_SUCCESS;
			msg.obj = thumbnail;
			mHandler.sendMessage(msg);
			super.onPostExecute(thumbnail);
		}

	}

	private void shootSound() {
		AudioManager meng = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
		try {
			if (volume != 0) {
				if (mMediaPlayer == null) {
					mMediaPlayer = MediaPlayer.create(getBaseContext(),
							Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
				}
				if (mMediaPlayer != null) {
					mMediaPlayer.start();
				}
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_cancel:
				setResult(RESULT_OK);
				finish();
				break;
			case R.id.btn_take_photo:
				if (null != mCamera) {
					shootSound();
					mBtnTakePhone.setEnabled(false);
					mCamera.takePicture(null, null, mPictureCallBack);
				}
				break;
			case R.id.btn_save:
				if (null == mPhotosList || mPhotosList.isEmpty()) {
					Toast.makeText(CameraActivity.this, "请先拍摄乐谱", Toast.LENGTH_LONG).show();
				} else {
					showDialog(DIALOG_ACTION_SAVE);
				}
				break;
			default:
				break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG_ACTION_SAVE:
				Builder builder = createDialogBuilder(CameraActivity.this, getString(R.string.toast), "请输入乐谱名字",
						getString(R.string.cancel), getString(R.string.ensure));
				mEdtFileName = new EditText(this);
				mEdtFileName.setBackgroundResource(R.drawable.edt_teacher_msg_shape);
				mEdtFileName.setPadding(10, 0, 10, 0);
				mEdtFileName.setHintTextColor(Color.GRAY);
				mEdtFileName.setHint(R.string.toast_music_name);
				builder.setmDialogView(mEdtFileName);
				return builder.create(id);
			default:
				break;
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onNegativeBtnClick(int id, DialogInterface dialog, int which) {
		switch (id) {
			case DIALOG_ACTION_SAVE:
				if (null != mEdtFileName && !StringUtil.isNullOrEmpty(mEdtFileName.getText().toString().trim())) {
					String fileName = mEdtFileName.getText().toString().trim();
					File destDir = new File(FileUtil.getResDir(CameraActivity.this), fileName);
					for (int i = 0; i < mPhotosList.size(); i++) {
						try {
							File file = mPhotosList.get(i);
							FileUtils.moveFileToDirectory(file, destDir, true);
							File destFile = new File(destDir, file.getName());
							String newName = fileName + "_" + i;
							FileUtil.rename(CameraActivity.this, destFile, newName);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				setResult(RESULT_OK);
				finish();
				break;
			default:
				break;
		}
		super.onPositiveBtnClick(id, dialog, which);
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
		super.onBackPressed();
	}
}