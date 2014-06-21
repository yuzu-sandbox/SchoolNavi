/*
 * @auther yuzu
 * DateTimeをDateに変換するクラス
 * datetimeToDateメソッドにDateTimeを渡すとDateを返す
 */
package info.starseeker.calendar.service.event;

import java.util.Date;

import com.google.gdata.data.DateTime;

public class DatetimeToDate extends TranceformDate{

	@Override
	public Date datetimeToDate(DateTime dateTime)
	{
		//dateTime.toUiString() -> yyyy-MM-dd hh:mm
		String dateTimeStr = dateTime.toUiString();
		
		//イベントの日付が入る配列
		//[0]=year, [1]=month, [2]=day
		dateArray = new String[3];
		//イベントの時間が入る配列
		//[0]=hour, [1]=minutes
		timeArray = new String[2];		
		
		//DATE_ONLY -> false, COMPLETE -> true を返す
		Boolean result = dateTimeSprit(dateTimeStr);
		
		return dateFormat(result);//Date型に整えて返却
	}
}