package com.zsq.musiclibrary.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateConfig;
import com.zsq.musiclibrary.R;
import com.zsq.musiclibrary.adapter.FolderAdapter;
import com.zsq.musiclibrary.listener.IOperationProgressListener;
import com.zsq.musiclibrary.util.ConstantSet;
import com.zsq.musiclibrary.util.FileUtil;
import com.zsq.musiclibrary.util.OpenFileUtil;
import com.zsq.musiclibrary.util.PopWindowUtil;
import com.zsq.musiclibrary.util.StringUtil;
import com.zsq.musiclibrary.util.UIUtil;
import com.zsq.musiclibrary.widget.CustomDialog.Builder;

public class MainActivity extends ActivityBase implements OnClickListener, OnItemClickListener, OnItemLongClickListener {
	private static final long WAIT_TIME = 2000;
	private static final int SEARCH_REQUEST_CODE = 1;
	private static final int ABOUT_REQUEST_CODE = 2;
	private static final int TAKE_PHONE_REQUEST_CODE = 3;
	private static final int DIALOG_ACTION_DELETE = 0;
	private static final int DIALOG_ACTION_RENAME = 1;
	private GridView mGvRootFolder;
	private ArrayList<File> mFileList;
	private FolderAdapter mFilAdapter;
	private File mResDir;
	private File mCurrentFile;
	private File mChoosedFile;
	private LinearLayout mLlBack;
	private long mTouchTime;
	private View mActionSheetView;
	private PopWindowUtil mPopWindowUtil;
	private EditText mEdtFileName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 友盟检查更新
		UmengUpdateAgent.update(this);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateCheckConfig(true);
		UpdateConfig.setDebug(false);
		initVariables();
		initView();
		setListener();
		getFileList();
	}

	private void initVariables() {
		mFileList = new ArrayList<File>();
		mResDir = FileUtil.getResDir(this);
		mCurrentFile = mResDir;
	}

	private void initView() {
		mGvRootFolder = (GridView) findViewById(R.id.gv_root_folder);
		mFilAdapter = new FolderAdapter(this, mFileList, mGvRootFolder);
		mGvRootFolder.setAdapter(mFilAdapter);
		TextView titleTextView = (TextView) findViewById(R.id.title_with_back_title_btn_mid);
		titleTextView.setText(R.string.app_name);
		mLlBack = (LinearLayout) findViewById(R.id.title_with_back_title_btn_left);
		TextView mTvBack = (TextView) findViewById(R.id.tv_title_with_back_left);
		mTvBack.setText(R.string.title_back_text);
		mTvBack.setBackgroundResource(R.drawable.btn_back_bg);
		initActionSheet();
	}

	private void initActionSheet() {
		mActionSheetView = View.inflate(this, R.layout.view_action_sheet, null);
		mPopWindowUtil = new PopWindowUtil(mActionSheetView, mLlBack, null);
		mActionSheetView.findViewById(R.id.rl_pop_view).setOnClickListener(this);
		mActionSheetView.findViewById(R.id.tv_cation_rename).setOnClickListener(this);
		mActionSheetView.findViewById(R.id.tv_cation_delete).setOnClickListener(this);
	}

	private void setListener() {
		mGvRootFolder.setOnItemClickListener(this);
		mGvRootFolder.setOnItemLongClickListener(this);
		mLlBack.setOnClickListener(this);
	}

	private void getFileList() {
		if (null == mCurrentFile) {
			return;
		}
		if (null != mResDir && mCurrentFile.getAbsolutePath().equals(mResDir.getAbsolutePath())) {
			mLlBack.setVisibility(View.GONE);
		} else {
			mLlBack.setVisibility(View.VISIBLE);
		}
		File[] files = FileUtil.listFiles(this, mCurrentFile);
		if (files == null) {
			return;
		} else {
			mFileList.clear();
			mFileList.addAll(Arrays.asList(files));
			Collections.sort(mFileList, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					if (o1.isDirectory() && o2.isFile())
						return -1;
					if (o1.isFile() && o2.isDirectory())
						return 1;
					return o1.getName().compareTo(o2.getName());
				}
			});
		}
		mFilAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		File tempFile = (File) parent.getAdapter().getItem(position);
		if (null == tempFile || !tempFile.exists()) {
			return;
		}
		if (tempFile.isDirectory()) {
			mCurrentFile = tempFile;
			getFileList();
		} else if (tempFile.isFile()) {
			Intent intent = new Intent(MainActivity.this, MusicDetailActivity.class);
			ArrayList<File> imageFileList = new ArrayList<File>();
			int size = mFileList.size();
			for (int i = 0; i < size; i++) {
				File file = mFileList.get(i);
				if (!file.isDirectory()
						&& OpenFileUtil.FILE_ENDING_IMAGE == OpenFileUtil.getFileEnding(file, MainActivity.this)) {
					imageFileList.add(file);
					if (tempFile.getAbsolutePath().equals(file.getAbsolutePath())) {
						intent.putExtra(ConstantSet.KEY_INTENT_IMG_POSITION, imageFileList.size() - 1);
					}
				}
			}
			intent.putExtra(ConstantSet.KEY_INTENT_IMGS_LIST, imageFileList);
			startActivity(intent);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_with_back_title_btn_left:
				if (null != mCurrentFile) {
					mCurrentFile = mCurrentFile.getParentFile();
					getFileList();
				}
				break;
			case R.id.btn_search:
				Intent intent = new Intent(MainActivity.this, SearchActivity.class);
				startActivityForResult(intent, SEARCH_REQUEST_CODE);
				break;
			case R.id.tv_cation_rename:
				mPopWindowUtil.dissmiss();
				showDialog(DIALOG_ACTION_RENAME);
				break;
			case R.id.tv_cation_delete:
				mPopWindowUtil.dissmiss();
				showDialog(DIALOG_ACTION_DELETE);
				break;
			case R.id.rl_pop_view:
				mPopWindowUtil.dissmiss();
				break;
			case R.id.tv_take_photo:
				Intent intent2 = new Intent(MainActivity.this, CameraActivity.class);
				startActivityForResult(intent2, TAKE_PHONE_REQUEST_CODE);
				break;
			case R.id.btn_about:
				gotoAbout();
				break;
			default:
				break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG_ACTION_DELETE:
				return createDialogBuilder(MainActivity.this, getString(R.string.toast),
						getString(R.string.toast_delete_file), getString(R.string.cancel), getString(R.string.ensure))
						.create(id);
			case DIALOG_ACTION_RENAME:
				Builder builder = createDialogBuilder(MainActivity.this, getString(R.string.toast), "",
						getString(R.string.cancel), getString(R.string.ensure));
				mEdtFileName = new EditText(this);
				mEdtFileName.setBackgroundResource(R.drawable.edt_teacher_msg_shape);
				mEdtFileName.setPadding(10, 0, 10, 0);
				mEdtFileName.setHintTextColor(Color.GRAY);
				mEdtFileName.setHint(R.string.toast_rename_file);
				builder.setmDialogView(mEdtFileName);
				if (null != mChoosedFile && null != mEdtFileName) {
					mEdtFileName.setText(FileUtil.getFileNameNoEx(mChoosedFile.getName()));
					UIUtil.moveCursolToEnd(mEdtFileName);
				}
				return builder.create(id);
			default:
				break;
		}
		return super.onCreateDialog(id);
	}

	private void gotoAbout() {
		Intent intent = new Intent(MainActivity.this, AboutActivity.class);
		startActivityForResult(intent, ABOUT_REQUEST_CODE);
	}

	@Override
	public void onNegativeBtnClick(int id, DialogInterface dialog, int which) {
		switch (id) {
			case DIALOG_ACTION_DELETE:
				if (null != mChoosedFile) {
					FileUtil.delete(mChoosedFile, new IOperationProgressListener() {

						@Override
						public void onFinish() {
							getFileList();
						}

						@Override
						public void onFileChanged(File file) {
							if (null != file && mChoosedFile.getAbsolutePath().equals(file.getAbsolutePath())) {
								onFinish();
							}
						}
					});
				}
				break;
			case DIALOG_ACTION_RENAME:
				if (null != mEdtFileName && !StringUtil.isNullOrEmpty(mEdtFileName.getText().toString().trim())) {
					String fileName = mEdtFileName.getText().toString().trim();
					if (null != mChoosedFile) {
						boolean isSuccess = FileUtil.rename(MainActivity.this, mChoosedFile, fileName);
						if (isSuccess) {
							getFileList();
							toast(getString(R.string.toast_rename_file_success));
						} else {
							toast(getString(R.string.toast_rename_file_fail));
						}
					}
				}
				break;
			default:
				break;
		}
		super.onPositiveBtnClick(id, dialog, which);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (RESULT_OK != resultCode) {
			return;
		}
		switch (requestCode) {
			case ABOUT_REQUEST_CODE:
			case TAKE_PHONE_REQUEST_CODE:
				mResDir = FileUtil.getResDir(this);
				mCurrentFile = mResDir;
				getFileList();
				break;
			case SEARCH_REQUEST_CODE:
				File tempFile = (File) intent.getSerializableExtra(ConstantSet.KEY_INTENT_CURRENT_FILE);
				if (null != tempFile) {
					mCurrentFile = tempFile;
					getFileList();
				}
				break;

			default:
				break;
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}

	@Override
	public void onBackPressed() {
		if (null != mCurrentFile && null != mResDir
				&& !mCurrentFile.getAbsolutePath().equals(mResDir.getAbsolutePath())) {
			mCurrentFile = mCurrentFile.getParentFile();
			getFileList();
			return;
		} else {
			long currentTime = System.currentTimeMillis();
			if ((currentTime - mTouchTime) >= WAIT_TIME) {
				Toast.makeText(this, getString(R.string.once_press_quit), Toast.LENGTH_SHORT).show();
				mTouchTime = currentTime;
				return;
			} else {
				MobclickAgent.onKillProcess(this);
				finish();
			}
		}
		super.onBackPressed();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		File tempFile = (File) parent.getAdapter().getItem(position);
		if (null == tempFile || !tempFile.exists()) {
			return true;
		}
		mChoosedFile = tempFile;
		if (null != mChoosedFile && null != mEdtFileName) {
			mEdtFileName.setText(FileUtil.getFileNameNoEx(mChoosedFile.getName()));
			UIUtil.moveCursolToEnd(mEdtFileName);
		}
		mPopWindowUtil.show();
		return true;
	}

}
