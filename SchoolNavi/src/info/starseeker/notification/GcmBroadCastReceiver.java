/**
 * GCMの通知を受け取るクラス
 * @author yuzu
 */
package info.starseeker.notification;

import info.starseeker.mydebug.DebugTagName;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmBroadCastReceiver extends WakefulBroadcastReceiver implements DebugTagName
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		//通知を受け取るとこのメソッドが呼ばれます
		Intent service = new Intent(context, GcmIntentService.class);
		startWakefulService(context, service);
		setResultCode(Activity.RESULT_OK);		
	}
}
