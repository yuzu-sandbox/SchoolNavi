/**
 * サーバーにRegistrationIDを送信する
 * @author yuzu
 */
package info.starseeker.notification;

import info.starseeker.mydebug.DebugTagName;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import static info.starseeker.datacontroller.PreferenceInfo.getPrefs;

public class SendServer extends AsyncTask<String, Void, Integer> implements DebugTagName
{
	//idを登録するサーバーurl
	private final String SERVER_URL = "";
	private final int ERROR = -1;
	
	private final Context context;
	
	/**
	 * サーバーに取得したIDを登録します
	 * @param context
	 */
	public SendServer(Context context)
	{
		this.context = context;
	}
	
	@Override
	protected Integer doInBackground(String... params)
	{
		Log.d(TAG, "doInBackground");
		SharedPreferences prefs = getPrefs(context);
		String regid = prefs.getString(NotificationService.PROPERTY_REG_ID, "");
		if(regid.equals("") == true)
		{
			return ERROR;
		}
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("regid", regid));
		
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost method = new HttpPost(SERVER_URL);
		try
		{
			method.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
			HttpResponse response = client.execute(method);//実際に送信
			int status = response.getStatusLine().getStatusCode();
			Log.v(TAG, "status = " + status);
			return status;
		}
		catch(Exception e)
		{
			Log.e(TAG, e.getMessage());
		}
		
		return ERROR;
	}
	
	@Override
	protected void onPostExecute(Integer result)
	{
		if(result > 0)
		{
			Log.v(TAG, "サーバーへ送信が成功しました");
		}
		Log.v(TAG, "サーバーへの送信処理が終了しました");
	}
}
