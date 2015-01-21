package com.zsq.musiclibrary.app;

import android.app.Application;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.bugly.crashreport.CrashReport.UserStrategy;

/**
 * 
 * Description the class 全局应用程序
 * 
 * @version 1.0
 * @author zou.sq
 * 
 */
public class MusicLibraryApplication extends Application {
	public static final String TAG = "QianJiangApplication";

	@Override
	// 建议在您app的退出之前调用mapadpi的destroy()函数，避免重复初始化带来的时间消耗
	public void onTerminate() {
		super.onTerminate();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initCrashReport();
		Log.d(TAG, "FileApplication, onCreate");
	}

	private void initCrashReport() {
		UserStrategy strategy = new UserStrategy(getApplicationContext());
		strategy.setAppChannel("");
		try {
			String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			strategy.setAppVersion(versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		strategy.setAppReportDelay(5000); // 设置SDK处理延时，毫秒
		CrashReport.initCrashReport(getApplicationContext(), "900001795", false, strategy);
	}

}
