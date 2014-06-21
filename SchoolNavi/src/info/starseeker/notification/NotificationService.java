/**
 * 通知関連を扱うクラス
 * @author yuzu
 */
package info.starseeker.notification;

import info.starseeker.mydebug.DebugTagName;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class NotificationService implements DebugTagName, LoaderCallbacks<Boolean>
{
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "regId";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	private Activity activity;
	private Context context;
	
	/**
	 * RegistrationIDを取得してサーバーに送信するクラス
	 * @param activity
	 */
	public NotificationService(Activity activity)
	{
		this.activity = activity;
		context = activity.getApplicationContext();
	}
	
	/**
	 * IDを無ければ取得するメソッド
	 */
	public void getRegidServer()
	{
		if(checkPlayService())
		{
			LoaderManager lm = activity.getLoaderManager();
			lm.restartLoader(0, null, this);
		}
	}
	
	@Override
	public Loader<Boolean> onCreateLoader(int id, Bundle args)
	{
		GoogleAccess ga = new GoogleAccess(context);
		ga.forceLoad();
		return ga;
	}

	@Override
	public void onLoadFinished(Loader<Boolean> loaderInfo, Boolean result)
	{
		if(result == false)
		{
			Log.v(TAG, "再度送信しません");
			return;
		}
		
		Log.v(TAG, "サーバーにIDを送信します");
		//サーバーへ取得したIDを送信します
		SendServer task = new SendServer(context);
		task.execute();
	}

	@Override
	public void onLoaderReset(Loader<Boolean> arg0)
	{
		//no operation
	}
	
	private boolean checkPlayService()
	{
		return checkPlayService(activity);
	}
	
	/**
	 * GooglePlayServiceがインストールされているかをチャックするメソッドs
	 * @param activity
	 * @return
	 */
	public static boolean checkPlayService(Activity activity)
	{
		Context con = activity.getApplicationContext();
		//GooglePlayServiceがあるか
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(con);
		if(resultCode != ConnectionResult.SUCCESS)
		{
			if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
			{
				GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			}
			else
			{
				Log.i(TAG, "Playサービスがサポートされていない端末です");
				activity.finish();
			}
			return false;
		}
		return true;
	}
}
