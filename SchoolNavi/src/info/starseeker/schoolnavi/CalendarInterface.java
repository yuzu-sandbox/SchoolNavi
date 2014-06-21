/**
 * カレンダー関係のインターフェース
 */
package info.starseeker.schoolnavi;

import info.starseeker.calendar.service.event.MyCalendarEvent;

import java.util.ArrayList;

import android.content.Loader;

public interface CalendarInterface
{
	/**
	 * カレンダーの通信処理が終わった時に呼び出されるメソッド
	 * @param loaderInfo CalendarLoaderの通信情報
	 * @param eventList 取得したCalendarのevent情報
	 */
	public void calendarLoaderCallbacks(Loader<ArrayList<MyCalendarEvent>> loaderInfo, ArrayList<MyCalendarEvent> eventList);
}
