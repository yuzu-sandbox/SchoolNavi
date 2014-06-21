package info.starseeker.sqlite;

import info.starseeker.timetable.SubjectInfo;
import info.starseeker.timetableService.TimetableInfo;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TimetableDataController
{
	private TimetableSql ttsql;
	private SQLiteDatabase timetabledb;
	
	//コンストラクター
	public TimetableDataController(Context context)
	{
		ttsql = new TimetableSql(context);
	}
	
	//時間割セット
	public void setTimeTable(ArrayList<TimetableInfo> altt)
	{
		timetabledb = ttsql.getWritableDatabase();
		timetabledb.beginTransaction();
		timetabledb.execSQL("delete from timettable;");
		try
		{
			for (TimetableInfo tti : altt)
			{
				int week = tti.getWeek();
				int period =  tti.getPeriod();
				String subject = tti.getSubject();
				String room = tti.getRoom();
				String teacher = tti.getTeacher();

				Object[] insert = {week, period, subject, room, teacher};

				timetabledb.execSQL("insert into timettable values(?,?,?,?,?);", insert);
			}
			timetabledb.setTransactionSuccessful();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			timetabledb.endTransaction();
			timetabledb.close();
			ttsql.close();
		}
	}
	
	//時間割取得
	public SubjectInfo getTimeTable(int period, int week)
	{
		SubjectInfo si = null;
		Cursor c = null;
		timetabledb = ttsql.getReadableDatabase();
		
		c = timetabledb.rawQuery("select subject, room from timettable where period = " + period + " and week = " + week, null);
		if(c.moveToFirst())
		{
			si = new SubjectInfo(c.getString(0), c.getString(1), null);
		}
		
		c.close();
		timetabledb.close();
		ttsql.close();

		return si;
	}
	
	//授業情報取得
	public SubjectInfo getSubject(int period, int weekday)
	{
		SubjectInfo si = null;
		Cursor c = null;
		timetabledb = ttsql.getReadableDatabase();
		
		c = timetabledb.rawQuery("select subject, teacher, room from timettable where period = " + period + " and week = " + weekday, null);
		if(c.moveToFirst())
		{
			si = new SubjectInfo(c.getString(0), c.getString(2), c.getString(1));
		}
		c.close();
		timetabledb.close();
		ttsql.close();
		
		return si;
	}
}
