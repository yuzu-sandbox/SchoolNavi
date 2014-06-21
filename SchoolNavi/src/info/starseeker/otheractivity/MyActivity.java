package info.starseeker.otheractivity;

import info.starseeker.calendar.Calendar;
import info.starseeker.mydebug.DebugTagName;
import info.starseeker.news.News;
import info.starseeker.schoolnavi.MainActivity;
import info.starseeker.schoolnavi.R;
import info.starseeker.setting.Setting;
import info.starseeker.spaceclassroom.SpaceClassRoom;
import info.starseeker.timetable.Timetable;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class MyActivity extends Activity implements DebugTagName
{
	public Intent getIntent(int id, Context context)
	{
		Intent intent = null;
		switch (id)
		{
			case R.id.sideNews:
				intent = new Intent(context, News.class);
				break;
			case R.id.sideCal:
				intent = new Intent(context, Calendar.class);
				break;
			case R.id.sideHome:
				intent = new Intent(context, MainActivity.class);
				break;
			case R.id.sideSCR:
				intent = new Intent(context, SpaceClassRoom.class);
				break;
			case R.id.sideSetting:
				intent = new Intent(context, Setting.class);
				break;
			case R.id.sideTT:
				intent = new Intent(context, Timetable.class);
				break;
		}
		return intent;

	}
}