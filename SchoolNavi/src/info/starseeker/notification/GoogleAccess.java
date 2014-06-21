/**
 * GoogleへRegistrationIDを取得しに行くクラス
 * @author yuzu
 */
package info.starseeker.notification;

import static info.starseeker.datacontroller.PreferenceInfo.getPrefs;
import static info.starseeker.notification.NotificationService.PROPERTY_APP_VERSION;
import static info.starseeker.notification.NotificationService.PROPERTY_REG_ID;
import info.starseeker.mydebug.DebugTagName;

import java.io.IOException;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GoogleAccess extends AsyncTaskLoader<Boolean> implements DebugTagName
{
	//Googleのプロジェクトナンバー
	private final String PROJECT_NUM = "";
	
	private GoogleCloudMessaging gcm;
	private final Context context;
	
	/**
	 * @param context
	 */
	public GoogleAccess(Context context)
	{
		super(context);
		this.context = context;
	}
	
	@Override
	public Boolean loadInBackground()
	{
		gcm = GoogleCloudMessaging.getInstance(context);
		String regid = getRegistrationId();
		
		if(regid.isEmpty())
		{
			try
			{
				regid = gcm.register(PROJECT_NUM);
				final String registrationId = regid;
				new Runnable(){
					@Override
					public void run()
					{
						storeRegistrationId(registrationId);
					}
				}.run();
				return true;
			}
			catch(IOException ex)
			{
				Log.e(TAG, ex.getMessage());
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Googleのサーバーと通信し、IDを返す
	 * @return RegistrationId
	 */
	private String getRegistrationId()
	{
		SharedPreferences prefs = getPrefs(context);
		String regid = prefs.getString(NotificationService.PROPERTY_REG_ID, "");
		if(regid.isEmpty())
		{
			Log.i(TAG, "Registration not found");
			return "";
		}
		
		int registredVersion = prefs.getInt(NotificationService.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion();
		if(registredVersion != currentVersion)
		{
			Log.i(TAG, "App version changed.");
			return "";
		}
		return regid;
	}
	
	/**
	 * バージョン情報を取得
	 * @return AppVersion
	 */
	private int getAppVersion() 
	{
        try
        {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        }
        catch (NameNotFoundException e)
        {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
	
	/**
	 * プリファレンスにRegistrationIDを保存する
	 * @param regid RegistrationID
	 */
	private void storeRegistrationId(String regid)
	{
		final SharedPreferences prefs = getPrefs(context);
		int appVersion = getAppVersion();
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regid);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
		
	}
}
