package info.starseeker.datacontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceInfo
{
	private SharedPreferences preference;
	private Editor editor;
	
	public PreferenceInfo(Context context)
	{
		preference = context.getSharedPreferences("SchoolNavi", Context.MODE_PRIVATE);
	}
	
	public static SharedPreferences getPrefs(Context context)
	{
		return context.getSharedPreferences("SchoolNavi", Context.MODE_PRIVATE);
	}
	
	/**
	 * クラスの設定
	 * @param grade 学年
	 * @param sclass クラス
	 * @param course コース
	 */
	public void setClass(int grade, String sclass, String course)
	{
		editor = preference.edit();
		
		editor.putInt("学年", grade).commit();
		editor.putString("クラス", sclass).commit();
		editor.putString("コース", course).commit();
	}
	
	public void setNotifies(Boolean bool)
	{
		editor = preference.edit();
		
		editor.putBoolean("Notifies", bool).commit();
	}
	
	/**
	 * クラスの取得
	 * @return
	 */
	public String getClassNumber()
	{
		StringBuilder strBuil = new StringBuilder();
		strBuil.append(Integer.toString(preference.getInt("学年", 9)));
		strBuil.append(preference.getString("クラス", ""));
		strBuil.append(preference.getString("コース", ""));
		return strBuil.toString();
	}
	
	/**
	 * 初回起動でない状態にする
	 */
	public void setCommit()
	{
		editor = preference.edit();
		
		editor.putBoolean("commit", true).commit();
	}
	
	/**
	 * 初回起動かの確認
	 * @return
	 */
	public boolean checkCommit()
	{
		return preference.getBoolean("commit", false);
	}
	
	/**
	 * 学年取得
	 * @return
	 */
	public int getGrade()
	{
		return preference.getInt("学年", 0);
	}
	
	/**
	 * クラス取得
	 * @return
	 */
	public String getClassName()
	{
		return preference.getString("クラス", "");
	}
	
	/**
	 * コース取得
	 * @return
	 */
	public String getCourse()
	{
		return preference.getString("コース", "");
	}
	
	public Boolean getNotifies()
	{
		return preference.getBoolean("Notifies", true);
	}
}
