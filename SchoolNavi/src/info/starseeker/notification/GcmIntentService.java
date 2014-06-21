/**
 * 通知が来た時のインテントを処理するクラス
 * @author yuzu
 */
package info.starseeker.notification;

import info.starseeker.datacontroller.PreferenceInfo;
import info.starseeker.mydebug.DebugTagName;
import info.starseeker.news.News;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService implements DebugTagName
{
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager notifiMana;
	NotificationCompat.Builder builder;

	public GcmIntentService()
	{
		super("GcmIntentService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent)
	{
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		
		String messageType = gcm.getMessageType(intent);
		if(!extras.isEmpty())
		{
			if(GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
			{
				Log.e(TAG, "Deleted messages on server");
			}
			else if(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
			{
				Log.e(TAG, "Send error");
			}
			else
			{
				Log.d(TAG, "Received : " + extras.toString());
				if (new PreferenceInfo(this).getNotifies())
					sendNotification();
			}
		}
		
		GcmBroadCastReceiver.completeWakefulIntent(intent);
	}
	
	/**
	 * AndroidのサービスにNotificationを送る
	 */
	private void sendNotification()
	{
		//intentを使った移動先を設定
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, News.class), 0);
        
        //Notification作成
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
        .setTicker("新しいNewsが配信されました")
        .setContentTitle("SchoolNavi")
        .setStyle(new NotificationCompat.BigTextStyle().bigText("新しいニュースが配信されました"))
        .setContentText("新しいNewsが配信されました確認してください")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setDefaults(Notification.DEFAULT_SOUND);
        //押された時に移動する先を設定
        mBuilder.setContentIntent(contentIntent);
        
        //SystemServiceのインスタンスを取得し、通知を表示する
        notifiMana = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        notifiMana.notify(NOTIFICATION_ID, mBuilder.build());
	}
}
