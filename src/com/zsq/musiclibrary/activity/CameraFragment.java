package com.zsq.musiclibrary.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.zsq.musiclibrary.R;
import com.zsq.musiclibrary.util.ImageUtil;
import com.zsq.musiclibrary.widget.Preview;

public class CameraFragment extends Fragment implements OnClickListener {
	private Preview mPreview;
	private Camera mCamera;
	private FrameLayout mFrameLayout;
	private ImageButton mImbTakePhoto;
	private Button mBtnCancel;
	private int mNumCameras;
	private int mDefaultCameraId;
	private boolean mIsBackCamera;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		mNumCameras = Camera.getNumberOfCameras();
		findIdOfDefaultCamera();
		super.onCreate(savedInstanceState);
	}

	private void findIdOfDefaultCamera() {
		CameraInfo cameraInfo = new CameraInfo();
		for (int i = 0; i < mNumCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
				mDefaultCameraId = i;
				mIsBackCamera = true;
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_camera, container, false);
		initViews(view);
		setListeners();
		return view;
	}

	private void initViews(View view) {
		mFrameLayout = (FrameLayout) view.findViewById(R.id.frag_cam_frame_surface);
		mPreview = new Preview(getActivity(), (SurfaceView) view.findViewById(R.id.frag_cam_surfaceView));
		mPreview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mFrameLayout.addView(mPreview);
		mImbTakePhoto = (ImageButton) view.findViewById(R.id.frag_cam_imb_take_photo);
		mBtnCancel = (Button) view.findViewById(R.id.frag_cam_imb_cancel);
	}

	private void setListeners() {
		mImbTakePhoto.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mCamera = Camera.open(mDefaultCameraId);
		mPreview.setmCameraID(mDefaultCameraId);
		mPreview.setCamera(mCamera);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mCamera != null) {
			mPreview.setCamera(null);
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.frag_cam_imb_take_photo:
				mCamera.takePicture(null, null, jpegCallback);
				break;
			case R.id.frag_cam_imb_cancel:
				getActivity().finish();
				break;
			default:
				break;
		}
	}

	/**
	 * Callback for taken photo
	 */
	PictureCallback jpegCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			saveTakenPhoto(data);
		}
	};

	/**
	 * Saves taken image to Sdcard picrute derictory
	 * 
	 * @param data
	 *            - byte[] of taken picture
	 */
	private void saveTakenPhoto(byte[] data) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			bitmap = ImageUtil.rescaleBitmap(bitmap);

			if (mIsBackCamera) {
				bitmap = ImageUtil.rotate(bitmap, 90, false);
			} else {
				bitmap = ImageUtil.rotate(bitmap, -90, true);
			}
		} catch (Exception error) {
			error.printStackTrace();
		}
		File pictureFile = ImageUtil.getOutputMediaFile(getActivity());

		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			Log.d("tag", "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.d("tag", "Error accessing file: " + e.getMessage());
		} finally {
			Toast.makeText(getActivity(), "恭喜，拍摄成功", Toast.LENGTH_LONG).show();
			getActivity().setResult(Activity.RESULT_OK);
			getActivity().finish();
		}

	}
}
