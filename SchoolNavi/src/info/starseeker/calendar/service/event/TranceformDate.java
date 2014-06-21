/**
 * DatetimeをDate型に変換するための抽象クラス
 * @auther yuzu
 */
package info.starseeker.calendar.service.event;

import java.util.Date;
import java.util.GregorianCalendar;

import com.google.gdata.data.DateTime;

abstract public class TranceformDate {
	
	private static final Boolean COMPLETE = true;
	private static final Boolean DATE_ONLY = false;
	
	private static final Byte YEAR_IDX = 0;
	private static final Byte MONTH_IDX = 1;
	private static final Byte DATE_IDX = 2;
	private static final Byte HOUR_IDX = 0;
	private static final Byte MINUTES_IDX = 1;
	
	protected String[] dateArray;
	protected String[] timeArray;
	
	/**
	 * DateTime型をDate型に変換する処理を記述する
	 * @param dateTime 変換したいDataTime型のデータ
	 * @return DateTimeをDateに変えるための実際の処理を記述
	 */
	abstract public Date datetimeToDate(DateTime dateTime);
	
	/**
	 * 配列に入れて日付だけか、日付と時間を返す
	 * @param dateTimeStr
	 * @return DateTimeを日付と時間に分け、時間が存在するかどうかを返す
	 */
	protected Boolean dateTimeSprit(String dateTimeStr)
	{
		final int DATE_INDEX = 0;
		final int TIME_INDEX = 1;
		
		//日付部分と時間部分を分ける
		String[] spritStr = dateTimeStr.split(" ");
		
		//日付を取り出す
		dateArray = spritStr[DATE_INDEX].split("-");
		
		if(spritStr.length == 1)//日付のみ存在する
		{
			spritStr = null;//配列のメモリを擬似的に開放する
			return DATE_ONLY;
		}
		
		timeArray = spritStr[TIME_INDEX].split(":");
		spritStr = null;
		return COMPLETE;
	}
	
	/**
	 * 実際にDate型を作るクラス
	 * @param result 時間がセットされてるかどうか
	 * @return DateTimeをDateに
	 */
	protected Date dateFormat(Boolean result)
	{
		//返す時間を用意する変数
		GregorianCalendar cal = new GregorianCalendar();
		
		//年月日
		int year = Integer.parseInt(dateArray[YEAR_IDX]);
		int month = Integer.parseInt(dateArray[MONTH_IDX]);
		int date = Integer.parseInt(dateArray[DATE_IDX]);
			
		//時間の初期値を設定
		int hour = 0;
		int minute = 0;
		
		if(result)//予定に時間がセットされているとtrue
		{
			//セットされていた場合時間を取り出す
			hour = Integer.parseInt(timeArray[HOUR_IDX]);
			minute = Integer.parseInt(timeArray[MINUTES_IDX]);
		}

		cal.clear();
		cal.set(year, month-1, date, hour, minute);

		Date resultDate = new Date();
		resultDate = cal.getTime();
		return resultDate;
	}
}
