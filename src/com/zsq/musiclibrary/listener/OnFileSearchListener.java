package com.zsq.musiclibrary.listener;

import java.io.File;

public interface OnFileSearchListener {

	void onFileFound(File file);

	void onSearchFinish();

}
