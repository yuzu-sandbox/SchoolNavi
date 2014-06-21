package info.starseeker.network;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionDB
{
	private Connection con = null;
	private Statement stmt = null;

	//サーバーurl, user, pass入力
	private final String url = "jdbc:mysql:///schoolnavi";
	private final String user = "";
	private final String pass = "";
	
	public Statement getConnection()
	{
		try
		{
			//jdbcドライバー準備
			Class.forName("com.mysql.jdbc.Driver");
	
			//接続先の準備
			con = DriverManager.getConnection(url,user,pass);
	
			//接続先に接続
			stmt = con.createStatement();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return stmt;
	}
	
	public void closeConnection()
	{
		try
		{
			stmt.close();
			con.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
