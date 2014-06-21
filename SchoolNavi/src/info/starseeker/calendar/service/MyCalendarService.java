/**
 * カレンダーのイベントを持った配列を扱うクラス
 * @author yuzu
 */
package info.starseeker.calendar.service;

import info.starseeker.calendar.service.event.MyCalendarEvent;
import java.util.ArrayList;

public class MyCalendarService {
	private ArrayList<MyCalendarEvent> eventList;

	/**
	 * EventListを持ったオブジェクト
	 */
	public MyCalendarService() {
		//no ope
	}

	/**
	 * EventListを持ったオブジェクト
	 * @param eventList CalendarEventのList
	 */
	public MyCalendarService(ArrayList<MyCalendarEvent> eventList)
	{
		this.eventList = eventList;
	}

	/**
	 * EventListをすべて返す
	 * @return EventList
	 */
	public ArrayList<MyCalendarEvent> getEventList()
	{
		return eventList;
	}

	/**
	 * EventListの引数で渡された日のイベントのListを返す
	 * @param date 取り出したい日付
	 * @return 指定した日のEventList
	 */
	public ArrayList<MyCalendarEvent> getDayEvent(int date)
	{
		ArrayList<MyCalendarEvent> dayEventList = new ArrayList<MyCalendarEvent>();
		for(MyCalendarEvent event : eventList)
		{
			if(event.getStartDate() <= date && date <= event.getEndDate())
			{
				dayEventList.add(event);
			}
		}
		return dayEventList;
	}

	/**
	 * EventListから引数で渡したイベントのIDと同じイベントを返す
	 * @param eventId イベントのID
	 * @return イベント or 存在しない場合はnull
	 */
	public MyCalendarEvent getEvent(String eventId)
	{
		for(MyCalendarEvent event : eventList)
		{
			if(event.getEventId().equals(eventId))
			{
				return event;
			}
		}
		return null;//存在しない時
	}
}
