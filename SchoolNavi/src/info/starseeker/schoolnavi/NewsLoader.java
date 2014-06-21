package info.starseeker.schoolnavi;

import info.starseeker.news.NewsService;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;

public class NewsLoader implements LoaderCallbacks<String>
{
	private Context context;
	NewsInterface newsi;
	public NewsLoader(NewsInterface newsi, Context context)
	{
		this.newsi = newsi;
		this.context = context;
	}

	@Override
	public Loader<String> onCreateLoader(int id, Bundle arg1)
	{
		NewsService newss = new NewsService(context);
		newss.forceLoad();
		return newss;
	}

	@Override
	public void onLoadFinished(Loader<String> newsl, String newstitle)
	{
		newsi.newsLoaderCallbacks(newsl, newstitle);
	}

	@Override
	public void onLoaderReset(Loader<String> arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

}
