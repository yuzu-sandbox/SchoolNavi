/**
 * Top画面のカレンダーの情報を取得するクラス
 * @author tanpopo
 */
package info.starseeker.schoolnavi;

import info.starseeker.timetableService.TimetableInfo;
import info.starseeker.timetableService.TimetableService;

import java.util.ArrayList;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;

public class TimetableLoader implements LoaderCallbacks<ArrayList<TimetableInfo>>{
	
	private TimetableInterface timetablelc;
	private Context context;
	private int wgrade;
	private String wclass;
	private String wcourse;
	
	public TimetableLoader(TimetableInterface timetablelc, Context context, int wgrade, String wclass, String wcourse)
	{
		this.timetablelc = timetablelc;
		this.context = context;
		this.wgrade = wgrade;
		this.wclass = wclass;
		this.wcourse = wcourse;
	}

	@Override
	public Loader<ArrayList<TimetableInfo>> onCreateLoader(int id, Bundle bundle)
	{
		//onCreateLoaderの処理
		TimetableService tts = new TimetableService(context, wgrade, wclass, wcourse);
		tts.forceLoad();
		return tts;
	}
	
	@Override
	public void onLoadFinished(Loader<ArrayList<TimetableInfo>> tts, ArrayList<TimetableInfo> ttss)
	{	
		timetablelc.timetableLoaderCallbacks(tts, ttss);
	}
	
	@Override
	public void onLoaderReset(Loader<ArrayList<TimetableInfo>> tts)
	{
		//今回は何もしない
	}
}
