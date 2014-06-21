/**
 * Top画面のカレンダーの情報を取得するクラス
 * @author yuzu
 */
package info.starseeker.schoolnavi;

import info.starseeker.calendar.service.CalendarException;
import info.starseeker.calendar.service.access.CalendarQueryApi;
import info.starseeker.calendar.service.event.MyCalendarEvent;
import info.starseeker.mydebug.DebugTagName;

import java.util.ArrayList;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;

public class CalendarLoader implements
		LoaderCallbacks<ArrayList<MyCalendarEvent>>, DebugTagName
{

	private CalendarInterface calendarlc;
	private Context context;

	/**
	 * CalendarLoaderを使うためのコンストラクタ
	 * 
	 * @param myLoaderCallbacks
	 *            インターフェース
	 * @param context
	 *            呼び出すActivityのcontext
	 */
	public CalendarLoader(CalendarInterface calendarlc, Context context)
	{
		this.calendarlc = calendarlc;
		this.context = context;
	}

	@Override
	public Loader<ArrayList<MyCalendarEvent>> onCreateLoader(int id, Bundle args)
	{
		try
		{
			CalendarQueryApi queryApi = new CalendarQueryApi(context, "WEEK");
			queryApi.forceLoad();
			return queryApi;
		}
		catch(CalendarException ce)
		{
			ce.printStackTrace();
			Log.e(CALTAG, ce.getMessage());
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<MyCalendarEvent>> loaderInfo,ArrayList<MyCalendarEvent> eventList)
	{
		calendarlc.calendarLoaderCallbacks(loaderInfo, eventList);
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<MyCalendarEvent>> arg0)
	{

	}
}
