package com.zsq.musiclibrary.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

/**
 * 
 * Description the class 文件帮助类
 * 
 * @version 1.0
 * @author zou.sq
 * 
 */
public class ImageUtil {

	private static final int IMAGE_MAX_SIZE = 500;

	private ImageUtil() {
	}

	public static Bitmap readBitMap(String url) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		return BitmapFactory.decodeFile(url, opt);
	}

	/**
	 * 
	 * @Name scanMedia
	 * @Description 及时扫描拍照后的照片，在相册就能看到
	 * @param context
	 *            上下文对象
	 * @param path
	 *            照片的路径
	 * 
	 */
	public static void scanMedia(Context context, String path) {
		File file = new File(path);
		Uri uri = Uri.fromFile(file);
		Intent scanFileIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
		context.sendBroadcast(scanFileIntent);
	}

	/**
	 * 图片缩放处理
	 * 
	 * @param filename
	 *            图片文件名字
	 * @param maxWidth
	 *            图片宽
	 * @param maxHeight
	 *            图片高
	 * @return 处理后的图片
	 */
	public static Bitmap scalePicture(String filename, int maxWidth, int maxHeight) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			BitmapFactory.decodeFile(filename, opts);
			int srcWidth = opts.outWidth;
			int srcHeight = opts.outHeight;
			int desWidth = 0;
			int desHeight = 0;
			// 缩放比例
			double ratio = 0.0;
			if (srcWidth > srcHeight) {
				ratio = srcWidth / maxWidth;
				desWidth = maxWidth;
				desHeight = (int) (srcHeight / ratio);
			} else {
				ratio = srcHeight / maxHeight;
				desHeight = maxHeight;
				desWidth = (int) (srcWidth / ratio);
			}
			// 设置输出宽度、高度
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			newOpts.inSampleSize = (int) (ratio);
			newOpts.inJustDecodeBounds = false;
			newOpts.outWidth = desWidth;
			newOpts.outHeight = desHeight;
			bitmap = BitmapFactory.decodeFile(filename, newOpts);

		} catch (Exception e) {
		}
		return bitmap;
	}

	public static Bitmap rescaleBitmap(Bitmap bitmap) {
		int scale = 1;
		if (bitmap.getHeight() > IMAGE_MAX_SIZE || bitmap.getWidth() > IMAGE_MAX_SIZE) {
			scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE
					/ (double) Math.max(bitmap.getHeight(), bitmap.getWidth()))
					/ Math.log(0.5)));
		}
		return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / scale, bitmap.getHeight() / scale, false);
	}

	public static Bitmap rotate(Bitmap bitmap, int degree, boolean flip) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix matrix = new Matrix();
		if (flip) {
			matrix.preScale(-1, 1);
		}
		matrix.preRotate(degree);

		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, false);
	}

	/**
	 * Create a File for saving an image or video
	 */
	public static File getOutputMediaFile(Context context) {
		String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault()).format(new Date());

		File mediaStorageDir = FileUtil.getResDir(context);
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		return mediaFile;
	}
}