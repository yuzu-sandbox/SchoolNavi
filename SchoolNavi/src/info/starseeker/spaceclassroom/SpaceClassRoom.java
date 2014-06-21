package info.starseeker.spaceclassroom;

import info.starseeker.customwidget.MultiDirectionSlidingDrawer;
import info.starseeker.customwidget.MultiDirectionSlidingDrawer.OnDrawerCloseListener;
import info.starseeker.customwidget.MultiDirectionSlidingDrawer.OnDrawerOpenListener;
import info.starseeker.network.NetworkStatus;
import info.starseeker.otheractivity.MyActivity;
import info.starseeker.schoolnavi.R;
import info.starseeker.searchclassroom.RoomInfo;
import info.starseeker.searchclassroom.SearchClassRoom;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SpaceClassRoom extends MyActivity implements OnClickListener, LoaderCallbacks<ArrayList<RoomInfo>>,OnDrawerOpenListener,OnDrawerCloseListener
{
	private View allcover;
	private MultiDirectionSlidingDrawer mDrawer;
	private LoaderManager lm = getLoaderManager();
	private ProgressDialog pd;

	@Override protected void onCreate(Bundle savedInstanceState)
	{
		// setContentViewの前でタイトル非表示
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scr);

		findViewById(R.id.menuListBtn).setOnClickListener(this);
		findViewById(R.id.scrbtn).setOnClickListener(new SearchRoom());

		mDrawer = (MultiDirectionSlidingDrawer)findViewById(R.id.drawer);
		mDrawer.setOnDrawerOpenListener(this);
		mDrawer.setOnDrawerCloseListener(this);

		// SideMenuでの画面遷移のListenerのセット
		findViewById(R.id.sideCal).setOnClickListener(this);
		findViewById(R.id.sideNews).setOnClickListener(this);
		findViewById(R.id.sideHome).setOnClickListener(this);
		findViewById(R.id.sideSetting).setOnClickListener(this);
		findViewById(R.id.sideTT).setOnClickListener(this);

		allcover = findViewById(R.id.allcover);
		allcover.setOnClickListener(this);

		findViewById(R.id.roomSelectImg).setOnClickListener(new RoomSelectChangeScale());
		LinearLayout LL = (LinearLayout)findViewById(R.id.sideSCR);
		LL.setBackgroundColor(Color.rgb(51, 51, 51));
		LL.setClickable(false);
	}
	@Override public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override public boolean onKeyDown(int keyCode, KeyEvent event)
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

	@Override public void onClick(View v)
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
		else
		{
			switch(id)
			{
				case R.id.sideCal :
				case R.id.sideNews :
				case R.id.sideSetting :
				case R.id.sideHome :
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
	@Override public void onDrawerOpened()
	{
		allcover.setVisibility(View.VISIBLE);


	}
	@Override public void onDrawerClosed()
	{
		allcover.setVisibility(View.GONE);
	}
	class RoomSelectChangeScale implements OnClickListener
	{
		private int flag = 0;
		@Override public void onClick(View v)
		{
			// 回転処理
			ImageView img = (ImageView)findViewById(R.id.roomSelectImg);

			// 検索エリア省略
			View serch = findViewById(R.id.serchArea);
			if(flag == 0)
			{
				img.setImageResource(R.drawable.arrow_top);
				serch.setVisibility(View.GONE);
				flag = 1;
			}
			else
			{
				img.setImageResource(R.drawable.arrow_bottom);
				serch.setVisibility(View.VISIBLE);
				flag = 0;
			}

		}
	}
	class SearchRoom implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			checkConnect();
		}
	}
	public Loader<ArrayList<RoomInfo>> onCreateLoader(int id, Bundle bundle)
	{
		Spinner sweek = (Spinner)findViewById(R.id.weekdayspinner);
		Spinner speriod = (Spinner)findViewById(R.id.periodspinner);
		int week = sweek.getSelectedItemPosition() + 1;
		int period = speriod.getSelectedItemPosition() + 1;
		SearchClassRoom scr = new SearchClassRoom(SpaceClassRoom.this, week, period);
		scr.forceLoad();
		return scr;
	}
	@Override public void onLoadFinished(Loader<ArrayList<RoomInfo>> lalri, ArrayList<RoomInfo> alri)
	{
		LinearLayout pcLL = (LinearLayout)findViewById(R.id.computerroom);
		if(pcLL.getChildCount() > 0)
		{
			pcLL.removeAllViews();
		}
		LinearLayout crLL = (LinearLayout)findViewById(R.id.classroom);
		if(crLL.getChildCount() > 0)
		{
			crLL.removeAllViews();
		}
		LinearLayout pcinLL = new LinearLayout(SpaceClassRoom.this);
		pcinLL.setOrientation(LinearLayout.HORIZONTAL);
		pcinLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		LinearLayout crinLL = new LinearLayout(SpaceClassRoom.this);
		crinLL.setOrientation(LinearLayout.HORIZONTAL);
		crinLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		int pcCnt = 0;
		int crCnt = 0;

		final int Max_Cnt = 4;

		for(RoomInfo ri : alri)
		{
			TextView addroomtxt = new TextView(SpaceClassRoom.this);
			addroomtxt.setText(ri.getRoomName());
			addroomtxt.setGravity(Gravity.CENTER);
			addroomtxt.setPadding(10,10,10,10);
			addroomtxt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
			if(ri.getPcRoom() == 1)
			{
				pcinLL.addView(addroomtxt);
				pcCnt++;
				if(pcCnt == Max_Cnt)
				{
					pcLL.addView(pcinLL);
					pcCnt = 0;
					pcinLL = new LinearLayout(SpaceClassRoom.this);
					pcinLL.setOrientation(LinearLayout.HORIZONTAL);
					pcinLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				}
			}
			else
			{
				crinLL.addView(addroomtxt);
				crCnt++;
				if(crCnt == Max_Cnt)
				{
					crLL.addView(crinLL);
					crCnt = 0;
					crinLL = new LinearLayout(SpaceClassRoom.this);
					crinLL.setOrientation(LinearLayout.HORIZONTAL);
					crinLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				}
			}
		}
		pd.dismiss();
	}
	@Override public void onLoaderReset(Loader<ArrayList<RoomInfo>> arg0)
	{

	}
	public void checkConnect()
	{
		NetworkStatus ns = new NetworkStatus(SpaceClassRoom.this);
		if(ns.getNetworsStatus())
		{
			pd = new ProgressDialog(SpaceClassRoom.this);
			pd.setMessage("空き教室を調べています。");
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setCancelable(false);
			pd.show();
			findViewById(R.id.roomSelectImg).performClick();
			findViewById(R.id.resultScroll).setVisibility(View.VISIBLE);

			lm.restartLoader(0, null, SpaceClassRoom.this);
		}
		else
		{
			errorConnectDialog();
		}
	}
	public void errorConnectDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("通信エラー");
		builder.setMessage("ネットワークに接続できませんでした\n" +
				"電波状態を確認して再実行してください");
		builder.setPositiveButton("再実行", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				checkConnect();
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
			}
		});
		builder.setCancelable(false);
		builder.show();
	}
}