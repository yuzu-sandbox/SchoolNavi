/**
 * 時間割関係のインターフェース
 * 時間割取得とクラス確認はセットなので一つにします
 */
package info.starseeker.schoolnavi;

import info.starseeker.timetableService.TimetableInfo;

import java.util.ArrayList;

import android.content.Loader;

public interface TimetableInterface
{
	/**
	 * 時間割の通信処理が終わった時に呼び出されるメソッド
	 * @param tts TimeTableLoaderの通信情報
	 * @param ttss 取得したTimeTableの情報
	 */
	public void timetableLoaderCallbacks(Loader<ArrayList<TimetableInfo>> tts, ArrayList<TimetableInfo> ttss);
	/**
	 * クラスの確認処理が終わった時に呼び出されるメソッド
	 * @param lcheck CheackClassLoaderの通信情報
	 * @param bcheck 取得した結果の情報 true:クラスあり,false:クラスなし
	 */
	public void checkclassLoaderCallbacks(Loader<Boolean> lcheck, Boolean bcheck);
}
