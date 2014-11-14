package com.zsq.musiclibrary.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Locale;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.zsq.musiclibrary.listener.IOperationProgressListener;
import com.zsq.musiclibrary.listener.OnFileSearchListener;

/**
 * 
 * Description the class 文件帮助类
 * 
 * @version 1.0
 * @author zou.sq
 * 
 */
public class FileUtil {
	public static final int BUFSIZE = 256;
	public static final int COUNT = 320;
	private static final String TAG = "FileUtils";
	private static final long SIZE_KB = 1024;
	private static final long SIZE_MB = 1048576;
	private static final long SIZE_GB = 1073741824;

	/**
	 * 
	 * @Description 获取本地资源目录
	 * @param context
	 *            上下文对象
	 * @return File 本地资源目录
	 */
	public static File getResDir(Context context) {
		String pathName = SharedPreferenceUtil.getStringValueByKey(context, ConstantSet.CONFIG_FILE,
				ConstantSet.CUSTOM_DIR);
		if (StringUtil.isNullOrEmpty(pathName)) {
			pathName = ConstantSet.DEFAULT_PATH;
		}
		File downloadFile = null;
		if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			downloadFile = new File(Environment.getExternalStorageDirectory(), pathName);
		}
		return downloadFile;
	}

	/**
	 * 判断指定的文件是否存在
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 是否存在
	 */
	public static boolean isFileExist(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}

	/**
	 * 准备文件夹，文件夹若不存在，则创建
	 * 
	 * @param filePath
	 *            文件路径
	 */
	public static void prepareFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 删除指定的文件或目录
	 * 
	 * @param file
	 *            文件
	 */
	public static void delete(File file, IOperationProgressListener l) {
		if (file == null || !file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			deleteDirRecursive(file, l);
		} else {
			if (file.delete()) {
				l.onFileChanged(file);
			}
		}
	}

	/**
	 * 递归删除目录
	 * 
	 * @param dir
	 *            文件路径
	 */
	public static void deleteDirRecursive(File dir, IOperationProgressListener l) {
		if (dir == null || !dir.exists() || !dir.isDirectory()) {
			return;
		}
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		for (File f : files) {
			if (f.isFile()) {
				if (f.delete()) {
					l.onFileChanged(dir);
				}
			} else {
				deleteDirRecursive(f, l);
			}
		}
		if (dir.delete()) {
			l.onFileChanged(dir);
		}
	}

	/**
	 * 取得文件大小
	 * 
	 * @param f
	 *            文件
	 * @return long 大小
	 * 
	 */
	public long getFileSizes(File f) {
		long s = 0;
		try {
			if (f.exists()) {
				s = new FileInputStream(f).available();
			} else {
				f.createNewFile();
			}
		} catch (Exception e) {
			Log.w(TAG, e.toString());
		}
		return s;
	}

	/**
	 * 递归取得文件夹大小
	 * 
	 * @param filedir
	 *            文件
	 * @return 大小
	 */
	public static long getFileSize(File filedir) {
		long size = 0;
		if (null == filedir) {
			return size;
		}
		File[] files = filedir.listFiles();

		try {
			for (File f : files) {
				if (f.isDirectory()) {
					size += getFileSize(f);
				} else {
					FileInputStream fis = new FileInputStream(f);
					size += fis.available();
					fis.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;

	}

	/**
	 * 转换文件大小
	 * 
	 * @param fileS
	 *            大小
	 * @return 转换后的文件大小
	 */
	public static String formatFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.0");
		String fileSizeString = "";
		if (fileS == 0) {
			fileSizeString = "0" + "KB";
		} else if (fileS < SIZE_KB) {
			fileSizeString = df.format((double) fileS) + "KB";
		} else if (fileS < SIZE_MB) {
			fileSizeString = df.format((double) fileS / SIZE_KB) + "KB";
		} else if (fileS < SIZE_GB) {
			fileSizeString = df.format((double) fileS / SIZE_MB) + "M";
		} else {
			fileSizeString = df.format((double) fileS / SIZE_GB) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 判断SD卡是否已经准备好
	 * 
	 * @return 是否有SDCARD
	 */
	public static boolean isSDCardReady() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 文件流拷贝到文件
	 * 
	 * @param in
	 *            输入流
	 * @param outFile
	 *            输出文件
	 * @return 操作状态
	 */
	public static int copyStreamToFile(InputStream in, String outFile) {
		if (isFileExist(outFile)) {
			// 文件已经存在；
			return -2;
		}
		try {
			OutputStream fosto = new FileOutputStream(outFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = in.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			in.close();
			fosto.close();
			return 0;

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}
		}
	}

	/**
	 * 得到sdcard路径
	 * 
	 * @return
	 */
	public static String getExtPath() {
		String path = "";
		if (isSDCardReady()) {
			path = Environment.getExternalStorageDirectory().getPath();
		}
		return path;
	}

	/**
	 * 通过关键字查找文件
	 * 
	 * @param keyword
	 *            关键字
	 * @param filepath
	 *            搜索路径
	 * @param listener
	 *            回调接口
	 */
	public static void searchFile(String keyword, File file, OnFileSearchListener listener) {
		File[] files = file.listFiles();
		if (null == files) {
			listener.onFileFound(file);
			return;
		}
		if (files.length > 0) {
			for (File subFile : files) {
				if (subFile.isDirectory()) {
					// 如果目录可读就执行（一定要加，不然会挂掉）
					if (subFile.canRead()) {
						searchFile(keyword, subFile, listener);
					}
				} else {
					String fileName = subFile.getName().toLowerCase(Locale.getDefault());
					if (fileName.indexOf(keyword.toLowerCase(Locale.getDefault())) > -1) {
						listener.onFileFound(subFile);
					}
				}
			}
		}
		// 如果是文件，匹配的就回调回去，如果是目录就直接回调回去，，在回调方法里面再做判断，这样做的目的是为了判断是否搜索完毕
		listener.onFileFound(file);
	}

	public static File[] listFiles(final Context context, File filepath) {
		File[] files = filepath.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				boolean isAcceptable = false;
				if (file.isDirectory() || OpenFileUtil.FILE_ENDING_IMAGE == OpenFileUtil.getFileEnding(file, context)) {
					isAcceptable = true;
				}
				return isAcceptable;
			}
		});
		return files;
	}

	public static boolean rename(Context context, File file, String newName) {
		if (file == null || StringUtil.isNullOrEmpty(newName)) {
			return false;
		}
		String path = file.getParentFile() + File.separator + newName;
		if (!file.isDirectory()) {
			String extensionName = getExtensionName(file.getName());
			if (StringUtil.isNullOrEmpty(extensionName)) {
				extensionName = "";
			} else {
				extensionName = "." + extensionName;
			}
			path = file.getParentFile() + File.separator + newName + extensionName;
		}
		ImageUtil.scanMedia(context, path);
		return file.renameTo(new File(path));
	}

	/*
	 * Java文件操作 获取文件扩展名
	 * 
	 * Created on: 2011-8-2 Author: blueeagle
	 */
	public static String getExtensionName(String filename) {
		if (!StringUtil.isNullOrEmpty(filename)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return "";
	}

	/*
	 * Java文件操作 获取不带扩展名的文件名
	 * 
	 * Created on: 2011-8-2 Author: blueeagle
	 */
	public static String getFileNameNoEx(String filename) {
		if (!StringUtil.isNullOrEmpty(filename)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}
}
