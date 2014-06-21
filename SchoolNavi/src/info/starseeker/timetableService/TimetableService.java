package info.starseeker.timetableService;

import info.starseeker.network.ConnectionDB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import android.content.AsyncTaskLoader;
import android.content.Context;

public class TimetableService extends AsyncTaskLoader<ArrayList<TimetableInfo>>
{
	
	//where句で使用
	int wgrade;
	String wclass;
	String wcourse;

	//コンストラクタ
	public TimetableService(Context context, int wgrade, String wclass, String wcourse)
	{
		super(context);
		this.wgrade = wgrade;
		this.wclass = wclass;
		this.wcourse = wcourse;
	}

	//メイン部分
	public ArrayList<TimetableInfo> loadInBackground()
	{
		ArrayList<TimetableInfo> arr = new ArrayList<TimetableInfo>();
		try
		{
			ResultSet rest;
			ConnectionDB connect = new ConnectionDB();
			
			//sqlの実行
			rest = connect.getConnection().executeQuery("select WeekID, Period, SubjectName, RoomName, TeacherName " +
										"from TimeTableT tt inner join SubjectT s on tt.SubjectID = s.SubjectID " +
										"inner join RoomT r on tt.RoomID = r.RoomID " +
										"inner join TeacherT te on tt.TeacherID = te.TeacherID " +
										"inner join GroupT g on tt.GroupID = g.GroupID " +
										"where g.Grade = '" + Integer.toString(wgrade) +
										"' and g.Class = '" + wclass +
										"' and g.Course = '" + wcourse +
										"' order by WeekID, Period");
						
			//select文の結果の行が続く限り繰り返す
			while (rest.next())
			{
				//時間割情報を取得する
				int week = rest.getInt("WeekID");
				int period = rest.getInt("Period");
				String subject = rest.getString("SubjectName");
				String roomname = rest.getString("RoomName");
				String teacher = rest.getString("TeacherName");
				//時間割情報をセットする
				arr.add(new TimetableInfo(week, period, subject, roomname, teacher));
			}
			rest.close();
			connect.closeConnection();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		//同期処理の結果を返す
		return arr;
	}

	@Override
	protected void onStartLoading()
	{
	}

}
