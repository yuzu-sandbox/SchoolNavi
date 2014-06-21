/**
 * GoogleにCalendarのイベントを取得しに行くクラス
 * @auther yuzu
 */
package info.starseeker.calendar.service.access;

import info.starseeker.calendar.service.CalendarException;
import info.starseeker.calendar.service.event.MyCalendarEvent;
import info.starseeker.mydebug.DebugTagName;

import java.net.URL;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.google.gdata.client.Query;
import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;


public class CalendarQueryApi extends AsyncTaskLoader<ArrayList<MyCalendarEvent>> implements CalendarAccess, DebugTagName
{
	//ユーザーidとpw設定
	protected static final String userId = "";
	protected static final String userPw = "";
	private String dateRange;
	private ArrayList<MyCalendarEvent> eventList;
	private int position = 0;
	
	/**
	 * カレンダーを取得するクラスを作成するコンストラクタ
	 * @param context Activityの情報
	 */
	public CalendarQueryApi(Context context)
	{
		super(context);
	}
	
	/**
	 * カレンダーを取得するクラスに情報をセットする
	 * @param context Activityの情報
	 * @param range 取得データの範囲
	 * @throws CalendarException
	 */
	public CalendarQueryApi(Context context, String range) throws CalendarException
	{
		super(context);
		setDateRange(range);
	}
	
	/**
	 * カレンダーを取得するクラスに情報をセットする
	 * @param context Activityの情報
	 * @param range 取得データの範囲
	 * @param position 現在の月から何ヶ月か
	 * @throws CalendarException
	 */
	public CalendarQueryApi(Context context, String range, int position) throws CalendarException
	{
		super(context);
		setDateRange(range);
		setPosition(position);
	}
	
	@Override
	public ArrayList<MyCalendarEvent> loadInBackground()
	{
		eventList = new ArrayList<MyCalendarEvent>();
		//Googleにアクセスするサービス
		CalendarService service = new CalendarService("SchoolNavi");
		try
		{
			//サービスにユーザー情報を付加する
			service.setUserCredentials(userId, userPw);
			//アクセスするURLを設定
			URL url = new URL(CalendarAccess.URL);
			
			//Event取得クエリの開始と終了を設定する
			GregorianCalendar start = getStartTime();
			GregorianCalendar end = getEndTime(start);
			//クエリを用意
			CalendarQuery query = new CalendarQuery(url);
			
			//イベント取得の範囲を設定
			query.setMinimumStartTime(new DateTime(start.getTime()));//ここから
			query.setMaximumStartTime(new DateTime(end.getTime()));//ここまでの時間を取得する
			
			query.setMaxResults(Integer.MAX_VALUE);//イベント取得する最大数
			query.addCustomParameter(new Query.CustomParameter("singleevents", "true"));//繰り返しイベントを分けて取得する
			query.addCustomParameter(new Query.CustomParameter("orderby", "starttime"));//イベントのソート,starttimeを基準に並び替える
			query.addCustomParameter(new Query.CustomParameter("sortorder", "a"));//sortorder a = asc d = desc
			
			//イベントを取得、取得したCalendarEventFeedを返す
			CalendarEventFeed feed = service.query(query, CalendarEventFeed.class);
			for(CalendarEventEntry entry : feed.getEntries())
			{
				eventList.add(new MyCalendarEvent(entry));
			}
			
			return eventList;

		}
		catch(Exception e)
		{
			Log.e(TAG, "Exception : " + e.getMessage());
			e.printStackTrace();
		}
		return eventList;
	}
	
	
	/**
	 * Overrideして WEEK か MONTHを設定する
	 * @param range
	 * @throws CalendarException 入力文字列が正しくありません
	 */
	public void setDateRange(String range) throws CalendarException
	{
		if(range.equals("WEEK") || range.equals("MONTH"))
		{
			this.dateRange = range;
		}
		else
		{
			new CalendarException("入力文字列が正しくありません");
		}
	}
	
	/**
	 * setrangeがMONTHの時に現在の月から何ヶ月経ったかを設定する
	 * @param position 現在の月から何ヶ月を相対的に
	 */
	public void setPosition(int position)
	{
		this.position = position;
	}
	/**
	 * クエリのスタート時間を設定する
	 * @return 設定したスタート時間を返す
	 */
	private GregorianCalendar getStartTime()
	{
		GregorianCalendar start = new GregorianCalendar(TimeZone.getTimeZone("Asia/Tokyo"), Locale.JAPAN);
		//時間設定
		start.set(GregorianCalendar.HOUR_OF_DAY, 0);
		start.set(GregorianCalendar.MINUTE, 0);
		start.set(GregorianCalendar.SECOND, 0);
		start.set(GregorianCalendar.MILLISECOND, 0);
		//日にち設定
		start.set(GregorianCalendar.YEAR, start.get(GregorianCalendar.YEAR));
		
		if(dateRange.equals("WEEK"))
		{
			start.set(GregorianCalendar.MONTH, start.get(GregorianCalendar.MONTH));
			start.set(GregorianCalendar.DATE, start.get(GregorianCalendar.DATE));
		}
		else if(dateRange.equals("MONTH"))
		{
			start.set(GregorianCalendar.MONTH, start.get(GregorianCalendar.MONTH) + position);
			start.set(GregorianCalendar.DATE, 1);
		}
		return start;
	}
	
	/**
	 * クエリの終了時間を設定する
	 * @param start スタート時間から相対的に終了時間を設定する
	 * @return 設定した終了時間を返す
	 */
	private GregorianCalendar getEndTime(GregorianCalendar start)// throws CalendarException
	{
		GregorianCalendar end = (GregorianCalendar)start.clone();
		if(dateRange.equals("WEEK"))
		{
			end.add(GregorianCalendar.DATE, 7);
		}
		else if(dateRange.equals("MONTH"))
		{
			end.add(GregorianCalendar.MONTH, 1);//2013/11/1 -> 2013/12/1
			end.add(GregorianCalendar.DATE, -1);//2013/12/1 -> 2013/11/30
		}
		return end;
	}
}