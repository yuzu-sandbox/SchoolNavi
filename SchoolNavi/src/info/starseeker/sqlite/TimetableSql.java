package info.starseeker.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TimetableSql extends SQLiteOpenHelper
{
	static final String DB = "SchoolNavi.db";
	static final int DB_VERSION = 1;
	static final String TABLE_NAME = "timettable";
	static final String COL_PERIOD = "period";
	static final String COL_WEEK = "week";
	static final String COL_SUBJECT = "subject";
	static final String COL_ROOM = "room";
	static final String COL_TEACHER = "teacher";
	
	public TimetableSql(Context c)
	{
	   super(c, DB, null, DB_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db)
	{
	   db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
			   + COL_WEEK + " INTEGER NOT NULL,"
			   + COL_PERIOD + " INTEGER NOT NULL,"
			   + COL_SUBJECT + " TEXT,"
			   + COL_ROOM + " TEXT,"
			   + COL_TEACHER + " TEXT,"
			   + "primary key(" + COL_WEEK + "," + COL_PERIOD + "));");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
}
