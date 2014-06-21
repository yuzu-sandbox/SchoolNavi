package info.starseeker.schoolnavi;

import info.starseeker.checkclass.CheckClass;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;

public class CheckClassLoader implements LoaderCallbacks<Boolean>
{
	private TimetableInterface myInterFace;
	private Context context;
	private int wgrade;
	private String wclass;
	private String wcourse;
	
	public CheckClassLoader(TimetableInterface myInterface, Context context, int wgrade, String wclass, String wcourse)
	{
		this.myInterFace = myInterface;
		this.context = context;
		this.wgrade = wgrade;
		this.wclass = wclass;
		this.wcourse = wcourse;
	}
	
	@Override
	public Loader<Boolean> onCreateLoader(int id, Bundle args) {
		CheckClass cc = new CheckClass(context, wgrade, wclass, wcourse);
		cc.forceLoad();
		return cc;
	}

	@Override
	public void onLoadFinished(Loader<Boolean> lcheck, Boolean bcheck)
	{
		myInterFace.checkclassLoaderCallbacks(lcheck, bcheck);
	}

	@Override
	public void onLoaderReset(Loader<Boolean> arg0) {
		//何もしない
		
	}

}
