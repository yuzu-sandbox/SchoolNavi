package info.starseeker.checkclass;

import info.starseeker.network.ConnectionDB;

import java.sql.ResultSet;
import java.sql.SQLException;

import android.content.AsyncTaskLoader;
import android.content.Context;

public class CheckClass extends AsyncTaskLoader<Boolean>
{
	//where句で使用
	int wgrade;
	String wclass;
	String wcourse;

	//コンストラクタ
	public CheckClass(Context context, int wgrade, String wclass, String wcourse)
	{
		super(context);
		this.wgrade = wgrade;
		this.wclass = wclass;
		this.wcourse = wcourse;
	}

	@Override
	public Boolean loadInBackground()
	{
		boolean check = false;
		try
		{
			ResultSet rest;
			ConnectionDB connect = new ConnectionDB();
			
			//sqlの実行
			rest = connect.getConnection().executeQuery("select * from GroupT " +
										"where Grade = '" + Integer.toString(wgrade) +
										"' and Class = '" + wclass +
										"' and Course = '" + wcourse + "'");
						
			//選択されたクラスがあればtrueにする
			while (rest.next())
			{
				check = true;
			}
			rest.close();
			connect.closeConnection();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return check;
	}
	
	@Override
	protected void onStartLoading()
	{
		forceLoad();
	}
}
