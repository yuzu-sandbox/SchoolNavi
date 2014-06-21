/**
 * GoogleCalendarから取得したEventを格納するクラス
 * このクラスは1つのイベントを管理する
 * @auther yuzu
 */

package info.starseeker.calendar.service.event;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.extensions.When;

public class MyCalendarEvent {

	//予定の開始と終了時間
	private String id;
	private Date eventStartTime;
	private Date eventEndTime;
	private String title;//イベントのタイトル
	private String textContent;//イベントの詳細
	private String location;

	/**
	 * CalendarEventEntryのデータを引数に自動的にイベントの予定をセットする
	 * @param entry　Calendarの1件の予定
	 */
	public MyCalendarEvent(CalendarEventEntry entry)
	{
		//イベントのID設定
		this.id = entry.getIcalUID();
		//イベントから時間の一覧を取得
		When when = entry.getTimes().get(0);
		//変換のためのクラスをインスタンス化
		DatetimeToDate dtd = new DatetimeToDate();
		//イベント開始時間と終了時間を変換して格納
		this.eventStartTime = dtd.datetimeToDate(when.getStartTime());
		this.eventEndTime = dtd.datetimeToDate(when.getEndTime());

		this.title = entry.getTitle().getPlainText();
		this.textContent = entry.getTextContent().getContent().getPlainText();
		this.location = entry.getLocations().get(0).getValueString();
	}

	/**
	 * イベントのIDを返す
	 * @return Event eventId
	 */
	public String getEventId()
	{
		return this.id;
	}
	/**
	 * イベントの開始時間を返す
	 * @return EventStartTime
	 */
	public Date getStartTime()
	{
		return this.eventStartTime;
	}
	/**
	 * イベントの終了時間を返す
	 * @return EventEndTime
	 */
	public Date getEndTime()
	{
		return this.eventEndTime;
	}
	/**
	 * イベントのタイトルを返す
	 * @return EventTitle
	 */
	public String getTitle()
	{
		return this.title;
	}
	/**
	 * イベントの詳細を返す
	 * @return EventContent
	 */
	public String getTextContent()
	{
		return this.textContent;
	}
	/**
	 * イベントが行われる場所を返す
	 * 設定されていない場合はnullを返す
	 * @return Location or null
	 */
	public String getLocation()
	{
		return this.location;
	}

	/**
	 * eventStartの日付を返す
	 * @return Date
	 */
	public int getStartDate()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		return Integer.parseInt(sdf.format(eventStartTime));
	}
	/**
	 * eventEndTimeの日付を返す
	 * @return Date
	 */
	public int getEndDate()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		return Integer.parseInt(sdf.format(eventEndTime));
	}
	/**
	 * eventStartTimeの年月日を返す
	 * @return Date
	 */
	public String getStartYMD()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		return sdf.format(eventStartTime);
	}
	/**
	 * eventEndTimeの年月日を返す
	 * @return Date
	 */
	public String getEndYMD()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		return sdf.format(eventEndTime);
	}
	/**
	 * eventStartの時分を返す
	 * @return Date
	 */
	public String getStartClock()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(eventStartTime);
	}
	/**
	 * eventEndTimeの時分を返す
	 * @return Date
	 */
	public String getEndClock()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(eventEndTime);
	}
}
