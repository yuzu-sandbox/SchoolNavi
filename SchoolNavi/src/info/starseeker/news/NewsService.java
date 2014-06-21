package info.starseeker.news;

import info.starseeker.network.ConnectionDB;

import java.sql.ResultSet;
import java.sql.SQLException;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

public class NewsService extends AsyncTaskLoader<String>
{
	public NewsService(Context context)
	{
		super(context);
	}

	@Override
	public String loadInBackground()
	{
		String newstext = "";
		try
		{
			int i = 1;
			ResultSet rest;
			ConnectionDB connect = new ConnectionDB();
		
			//sqlの実行
			rest = connect.getConnection().executeQuery("select NewsID, Title, date_format(Date, '%c/%d') as date from NewsT order by NewsID desc limit 5");
			
			//select文の結果の行が続く限り繰り返す
			while (rest.next())
			{
				String newstitle = rest.getString("Title");
				String newsdate = rest.getString("date");
				Log.v("title", newstitle);
				if (i > 6)
					newstext += "【"+ newsdate+ "】"+ newstitle;
				else
					newstext += "【"+ newsdate+ "】"+ newstitle + "      ";
				i++;
			}
			if (newstext == "")
			{
				newstext = "Newsはありません";
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return newstext;
	}
}
