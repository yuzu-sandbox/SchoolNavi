/**
 * Google Calendarにアクセスするための情報が入ってるインターフェース
 * @auther yuzu
 */
package info.starseeker.calendar.service.access;


public interface CalendarAccess {
	//日と時間が定義されている場合に返す
	public static final boolean COMPLETE = true;
	//日のみが定義されている場合に返す
	public static final boolean DATE_ONLY = false;
	
	//関西IT勉強会（テスト用）
	public static final String URL = "http://www.google.com/calendar/feeds/9tpbceee3kjbn6aorimdkfqg88@group.calendar.google.com/public/full";
}
