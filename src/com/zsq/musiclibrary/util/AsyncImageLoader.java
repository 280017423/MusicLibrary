package com.zsq.musiclibrary.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.zsq.musiclibrary.listener.ImageLoadListener;

public class AsyncImageLoader {

	public AsyncImageLoader() {
	}

	public Drawable loadDrawable(final String imageUrl, final int size, final ImageLoadListener imageCallback) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);
			}
		};
		new Thread() {
			@Override
			public void run() {
				Bitmap bitmap = ImageUtil.getImageThumbnail(imageUrl, size, size);
				Message message = handler.obtainMessage(0, bitmap);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}

}