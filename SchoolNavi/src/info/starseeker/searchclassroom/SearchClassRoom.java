package info.starseeker.searchclassroom;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import android.content.AsyncTaskLoader;
import android.content.Context;

public class SearchClassRoom extends AsyncTaskLoader<ArrayList<RoomInfo>>
{
	//where句の
	int week;
	int period;

	public SearchClassRoom(Context context, int week, int period)
	{
		super(context);
		this.week = week;
		this.period = period;
	}

	@Override
	public ArrayList<RoomInfo> loadInBackground()
	{
		ArrayList<RoomInfo> arr = new ArrayList<RoomInfo>();
		try
		{	
			//jdbcドライバー
			Class.forName("com.mysql.jdbc.Driver");
			
			Connection con;
			Statement stmt;
			ResultSet rest;
			
			//サーバーurl
			final String url = "jdbc:mysql://schoolnavi";
			final String user = "student";
			final String pass = "oraoic";
			
			//接続先の準備
			con = DriverManager.getConnection(url,user,pass);
			
			//接続先に接続
			stmt = con.createStatement();
			
			//sqlの実行
			rest = stmt.executeQuery("select * from RoomT where RoomID not in" +
										"(select RoomID from TimeTableT where" +
										" Period = " + Integer.toString(period) +
										" and WeekID = " + Integer.toString(week)+ ")" +
										" and RoomID != 'no'");
			//select文の結果の行が続く限り繰り返す
			while (rest.next())
			{
				String roomname = rest.getString("RoomName");
				int pcroom = rest.getInt("PCRoom");
				//時間割情報をセットする
				arr.add(new RoomInfo(roomname, pcroom));
			}
			rest.close();
			stmt.close();
			con.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		//同期処理の結果を返す
		return arr;
	}

}
