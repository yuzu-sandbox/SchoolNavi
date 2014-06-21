package info.starseeker.news;

import info.starseeker.customwidget.CustomTextView;
import info.starseeker.customwidget.MultiDirectionSlidingDrawer;
import info.starseeker.customwidget.MultiDirectionSlidingDrawer.OnDrawerCloseListener;
import info.starseeker.customwidget.MultiDirectionSlidingDrawer.OnDrawerOpenListener;
import info.starseeker.network.ConnectionDB;
import info.starseeker.otheractivity.MyActivity;
import info.starseeker.schoolnavi.R;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class News extends MyActivity implements OnClickListener, OnDrawerOpenListener, OnDrawerCloseListener, OnScrollListener
{
	private AsyncTask<Void,Void,ArrayList<NewsData>> mTask;
	private View allcover, mFooter;
	private MultiDirectionSlidingDrawer mDrawer;
	private NewsAdapter newsAdapter;
	private List<Integer> listcolor;
	private ListView mlistview;
	private List<NewsData> objects;
	private static int StartID = 9999;
	private static int GET_NEWS = 10;
	private int topId = 0,bottomId = StartID;//Newsの一番上と下のIDを保持
	private int newscnt = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// setContentViewの前でタイトル非表示
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);

		//Newsのlist項目の色の準備
		listcolor = new ArrayList<Integer>();
		listcolor.add(Color.rgb(231, 76, 60));//red
		listcolor.add(Color.rgb(46, 204, 113));//green
		listcolor.add(Color.rgb(155,89,182));//porple
		listcolor.add(Color.rgb(241,196,15));//yellow
		listcolor.add(Color.rgb(52, 152, 219));//brue
		listcolor.add(Color.rgb(230, 126, 34));//orange

		objects = new ArrayList<NewsData>();

		newsAdapter = new NewsAdapter(this, 0, objects);

		mlistview = (ListView)findViewById(R.id.newsList);
		mlistview.addFooterView(getFooter());
		mlistview.setAdapter(newsAdapter);
		mlistview.setOnScrollListener(this);
		mlistview.setOnItemClickListener(new OnMyItemClickListener());

		findViewById(R.id.newNews).setOnClickListener(this);

		//SideBar関連の準備
		mDrawer = (MultiDirectionSlidingDrawer)findViewById(R.id.drawer);
		mDrawer.setOnDrawerOpenListener(this);
		mDrawer.setOnDrawerCloseListener(this);
		findViewById(R.id.menuListBtn).setOnClickListener(this);
		findViewById(R.id.sideCal).setOnClickListener(this);
		findViewById(R.id.sideHome).setOnClickListener(this);
		findViewById(R.id.sideSCR).setOnClickListener(this);
		findViewById(R.id.sideSetting).setOnClickListener(this);
		findViewById(R.id.sideTT).setOnClickListener(this);

		allcover = findViewById(R.id.allcover);
		allcover.setOnClickListener(this);

		LinearLayout LL = (LinearLayout)findViewById(R.id.sideNews);
		LL.setBackgroundColor(Color.rgb(51,51,51));
		LL.setClickable(false);
		//newsの追加処理
		Log.v(TAG,"追加");
		addNews(mlistview);
	}

	private View getFooter()
	{
		if(mFooter == null)
		{
			mFooter = getLayoutInflater().inflate(R.layout.listview_footer,	null);
		}
		return mFooter;
	}


	class OnMyItemClickListener implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			Log.d(TAG,Integer.toString(position));
			CustomTextView tv = (CustomTextView)view.findViewById(R.id.rowText);
			ImageView iv = (ImageView)view.findViewById(R.id.rowArrow);
			if(tv.getMaxLine() == 1)
			{
				tv.setMaxLines(Integer.MAX_VALUE);
				iv.setImageResource(R.drawable.arrow_top);
			}
			else
			{
				tv.setMaxLines(1);
				iv.setImageResource(R.drawable.arrow_bottom);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
	{
		if(totalItemCount == firstVisibleItem + visibleItemCount)// 一番下まで見たならば
		{
			//ニュースの追加取得
			addNews(mlistview);
			Log.d(TAG, "データ取得");

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			if(mDrawer.isOpened())
				mDrawer.animateClose();
			else
				this.finish();
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v)
	{
		Intent intent = null;
		int id = v.getId();
		if(id == R.id.menuListBtn)
		{
			if(!mDrawer.isOpened())
				mDrawer.animateOpen();

		}

		else if(v == allcover)
		{
			mDrawer.animateClose();
		}
		else if(id == R.id.newNews)
		{
			insertNews(mlistview);
		}
		else
		{
			switch(id)
			{
				case R.id.sideCal :
				case R.id.sideHome :
				case R.id.sideSCR :
				case R.id.sideSetting :
				case R.id.sideTT :
					intent = new Intent(getIntent(v.getId(), this));
					break;
			}
			try
			{
				if(mDrawer.isOpened())
					mDrawer.animateClose();
				startActivity(intent);
			}
			catch(Exception ex)
			{
				Toast.makeText(this, "画面遷移に失敗しました。", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onDrawerOpened()
	{
		allcover.setVisibility(View.VISIBLE);
	}

	@Override
	public void onDrawerClosed()
	{
		allcover.setVisibility(View.GONE);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
		//Overrideの必要があるだけで何もしない
	}

	//上に追加
	private void insertNews(final ListView view)
	{
		Log.d(TAG,"Insert");
		new AsyncTask<Void,Void,ArrayList<NewsData>>()
		{
			@Override
			protected ArrayList<NewsData> doInBackground(Void... arg0) {
				//通信処理
				ArrayList<NewsData> arrni = new ArrayList<NewsData>();
				try
				{
					ResultSet rest;
					ConnectionDB connect = new ConnectionDB();

					//SQLの実行：newsTないのでweekTで代用
					rest = connect.getConnection().executeQuery("select NewsID, Title, Main, date_format(Date, '%c月%d日') as date, Sender from NewsT" +
							" where NewsID > '" + topId +
							"' order by NewsID");

					Log.v("aaa", Integer.toString(rest.getRow()));
					while(rest.next())
					{
						int newsid = rest.getInt("NewsID");
						if (topId < newsid);
						{
							topId = newsid;
						}
						String main = rest.getString("Main");
						String date = rest.getString("date");
						String from = rest.getString("Sender");
						arrni.add(new NewsData(listcolor.get(newsid%6), from, date, main));
						newscnt++;
					}
					rest.close();
					connect.closeConnection();
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
				return arrni;
			};

			@Override
			protected void onPostExecute(ArrayList<NewsData> result)
			{
				if(result.size() > 0)
				{
					for (NewsData nd : result)
					//listに追加する
					newsAdapter.insert(nd, 0);
				}
				else
				{
					//もし取得がなければ
					//なにか処理があれば
				}
			}

		}.execute();
	}
	//下に追加
	private void addNews(final ListView view)
	{
		Log.d(TAG,"Add");
		// 既に読み込み中ならスキップ
		if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
		return;
		}
		mTask = new AsyncTask<Void,Void,ArrayList<NewsData>>()
		{
			@Override
			protected ArrayList<NewsData> doInBackground(Void... arg0)
			{
				//通信処理
				ArrayList<NewsData> arrni = new ArrayList<NewsData>();
				try
				{
					ResultSet rest;
					ConnectionDB connect = new ConnectionDB();

					//SQLの実行：newsTないのでweekTで代用
					rest = connect.getConnection().executeQuery("select NewsID, Title, Main, date_format(Date, '%c/%d') as date, Sender from NewsT" +
							" where NewsID < '" + bottomId +
							"' order by NewsID desc" +
							" limit " + GET_NEWS);

					while(rest.next())
					{
						int newsid = rest.getInt("NewsID");
						if (topId < newsid)
						{
							topId = newsid;
						}
						if (bottomId > newsid)
						{
							bottomId = newsid;
						}
						String main = rest.getString("Main");
						String date = rest.getString("date");
						String from = rest.getString("Sender");
						arrni.add(new NewsData(listcolor.get(newscnt % 6), from, date, main));
						newscnt++;
					}
					rest.close();
					connect.closeConnection();
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
				return arrni;
			};

			@Override
			protected void onPostExecute(ArrayList<NewsData> result)
			{
				if(result.size() > 0)
				{
					Log.v(TAG,Integer.toString(result.size()));
					//listに追加する
					newsAdapter.addAll(result);
					//objects.add(newsd);
					//newsAdapter.add(newsd);
					Log.v(TAG, "足したよ");
				}
				else
				{
					//もし取得がなければ
					view.removeFooterView(getFooter());
				}
			}
		}.execute();
	}
}
