package com.zsq.musiclibrary.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zsq.musiclibrary.R;
import com.zsq.musiclibrary.adapter.FolderAdapter;
import com.zsq.musiclibrary.listener.OnFileSearchListener;
import com.zsq.musiclibrary.util.ConstantSet;
import com.zsq.musiclibrary.util.FileUtil;
import com.zsq.musiclibrary.util.OpenFileUtil;
import com.zsq.musiclibrary.util.StringUtil;

public class SearchActivity extends ActivityBase implements OnClickListener, OnItemClickListener {

	private static final int FILE_FOUND_CODE = 1;
	private LinearLayout mLlBack;
	private EditText mEdtSearch;
	private GridView mGvRootFolder;
	private FolderAdapter mFilAdapter;
	private ArrayList<File> mFileList;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case FILE_FOUND_CODE:
					if (null != msg.obj) {
						File file = (File) msg.obj;
						if (null != file) {
							mFileList.add(file);
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
							mFilAdapter.notifyDataSetChanged();
						}
					}
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		initVariables();
		initView();
		setListener();
	}

	private void initVariables() {
		mFileList = new ArrayList<File>();
		mFilAdapter = new FolderAdapter(this, mFileList);
	}

	private void setListener() {
		mLlBack.setOnClickListener(this);
		mGvRootFolder.setOnItemClickListener(this);
		mEdtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				mFileList.clear();
				mFilAdapter.notifyDataSetChanged();
				final String kyeWord = editable.toString();
				if (!StringUtil.isNullOrEmpty(kyeWord)) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							FileUtil.searchFile(kyeWord, FileUtil.getResDir(SearchActivity.this),
									new OnFileSearchListener() {

										@Override
										public void onFileFound(File file) {
											Message msg = mHandler.obtainMessage();
											msg.what = FILE_FOUND_CODE;
											msg.obj = file;
											mHandler.sendMessage(msg);
										}
									});
						}
					}).start();
				}
			}
		});
	}

	private void initView() {
		mGvRootFolder = (GridView) findViewById(R.id.gv_search_folder);
		mGvRootFolder.setAdapter(mFilAdapter);
		mLlBack = (LinearLayout) findViewById(R.id.title_with_back_title_btn_left);
		mEdtSearch = (EditText) findViewById(R.id.edt_search);
		TextView mTvBack = (TextView) findViewById(R.id.tv_title_with_back_left);
		mTvBack.setText(R.string.title_back_text);
		mTvBack.setBackgroundResource(R.drawable.btn_back_bg);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_with_back_title_btn_left:
				back();
				break;
			case R.id.btn_search_clear:
				mEdtSearch.setText("");
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		File tempFile = (File) parent.getAdapter().getItem(position);
		if (null == tempFile || !tempFile.exists()) {
			return;
		}
		if (tempFile.isDirectory()) {
			Intent intent = new Intent();
			intent.putExtra(ConstantSet.KEY_INTENT_CURRENT_FILE, tempFile);
			setResult(RESULT_OK, intent);
		} else if (tempFile.isFile()) {
			ArrayList<File> fileList = new ArrayList<File>();
			ArrayList<File> imageFileList = new ArrayList<File>();
			Intent intent = new Intent(SearchActivity.this, MusicDetailActivity.class);
			File[] files = FileUtil.listFiles(this, tempFile.getParentFile());
			if (files == null) {
				return;
			} else {
				fileList.addAll(Arrays.asList(files));
				Collections.sort(fileList, new Comparator<File>() {
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

			int size = fileList.size();
			for (int i = 0; i < size; i++) {
				File file = fileList.get(i);
				if (!file.isDirectory()
						&& OpenFileUtil.FILE_ENDING_IMAGE == OpenFileUtil.getFileEnding(file, SearchActivity.this)) {
					imageFileList.add(file);
					if (tempFile.getAbsolutePath().equals(file.getAbsolutePath())) {
						intent.putExtra(ConstantSet.KEY_INTENT_IMG_POSITION, imageFileList.size() - 1);
					}
				}
			}
			intent.putExtra(ConstantSet.KEY_INTENT_IMGS_LIST, imageFileList);
			startActivity(intent);
			finish();
		}
	}
}
