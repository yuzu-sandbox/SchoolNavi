/**
 * カレンダーページの表示やイベントを担当するクラス
 * @author yuzu, Tsukamoto
 */
package info.starseeker.calendar;

import info.starseeker.calendar.service.EventAdapter;
import info.starseeker.calendar.service.EventRow;
import info.starseeker.calendar.service.MyCalendarService;
import info.starseeker.calendar.service.access.CalendarQueryApi;
import info.starseeker.calendar.service.event.MyCalendarEvent;
import info.starseeker.customwidget.MultiDirectionSlidingDrawer;
import info.starseeker.customwidget.MultiDirectionSlidingDrawer.OnDrawerCloseListener;
import info.starseeker.customwidget.MultiDirectionSlidingDrawer.OnDrawerOpenListener;
import info.starseeker.network.NetworkStatus;
import info.starseeker.otheractivity.MyActivity;
import info.starseeker.schoolnavi.R;
import info.starseeker.schoolnavi.R.id;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Calendar extends MyActivity implements OnClickListener, LoaderCallbacks<ArrayList<MyCalendarEvent>>,OnDrawerCloseListener,OnDrawerOpenListener
{
	private final int DISP_WEEK = 6;//表示する週
	private final int WEEK_DAY = 7;//1週間の日数
	private final int MAX_DISP_TITLE = 2;//タイトル表示数
	private final int FIRST_DATE = 1;//1日目

	private View allcover;
	private MyCalendarService calService;
	private LoaderManager loadMan;
	private int mPosition;//表示される月の相対的な値
	private LinearLayout calDispField;
	private MultiDirectionSlidingDrawer mDrawer;
	private ProgressDialog connectDialog;

	private Context context;

	private OnClickListener calDayListener;

	@Override protected void onCreate(Bundle savedInstanceState)
	{
		// setContentViewの前でタイトル非表示
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);

		// contextの保存
		context = this;

		mDrawer = (MultiDirectionSlidingDrawer)findViewById(R.id.drawer);
		mDrawer.setOnDrawerCloseListener(this);
		mDrawer.setOnDrawerOpenListener(this);

		findViewById(R.id.menuListBtn).setOnClickListener(this);

		// SideMenuでの画面遷移のListenerのセット
		findViewById(R.id.sideTT).setOnClickListener(this);
		findViewById(R.id.sideNews).setOnClickListener(this);
		findViewById(R.id.sideHome).setOnClickListener(this);
		findViewById(R.id.sideSetting).setOnClickListener(this);
		findViewById(R.id.sideSCR).setOnClickListener(this);

		allcover = findViewById(R.id.allcover);
		allcover.setOnClickListener(this);

		LinearLayout LL = (LinearLayout)findViewById(R.id.sideCal);
		LL.setBackgroundColor(Color.rgb(51, 51, 51));
		LL.setClickable(false);

		//カレンダー予定表示用等
		calDispField = (LinearLayout)findViewById(R.id.cal_linear);
		mPosition = 0;
		loadMan = getLoaderManager();

		//Loaderスタート
		loaderStartConnection();

		findViewById(R.id.cal_prev_btn).setOnClickListener(this);
		findViewById(R.id.cal_next_btn).setOnClickListener(this);
	}

	class ListClick implements OnItemClickListener
	{
		// onItemClickメソッドには、AdapterView(adapter)、選択した項目View(rowのxmlの内容=LinearLayout)、選択された位置のint値、IDを示すlong値が渡される
		public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
		{
			TextView idView = (TextView)view.findViewById(R.id.eventid);
			String eventId = (String)idView.getText();
			MyCalendarEvent calEvent = calService.getEvent(eventId);
			showDialog(calEvent);
		}
	}

	@Override public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{
			if (mDrawer.isOpened())
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
		else if(id == R.id.cal_prev_btn)//カレンダー前へクリック
		{
			mPosition--;
			loaderStartConnection();
			dayContentReset();
		}
		else if(id == R.id.cal_next_btn)//カレンダー次へクリックs
		{
			mPosition++;
			loaderStartConnection();
			dayContentReset();
		}
		else
		{
			switch(id)
			{
				case R.id.sideSCR :
				case R.id.sideNews :
				case R.id.sideSetting :
				case R.id.sideHome :
				case R.id.sideTT :
					intent = new Intent(getIntent(id, this));
					break;
			}
			try
			{
				if (mDrawer.isOpened())
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

	private void dayContentReset()
	{
		((TextView)findViewById(R.id.contentday)).setText("▼ ");
		ListView list = (ListView)findViewById(R.id.contentlist);
		ArrayAdapter adapter = (ArrayAdapter)list.getAdapter();
		adapter.clear();
		adapter.notifyDataSetChanged();
	}
	
	private void showDialog(MyCalendarEvent e)
	{
		class Dialog extends DialogFragment
		{
			private MyCalendarEvent event;
			Dialog(MyCalendarEvent e)
			{
				event = e;
			}
			
			@Override
			public AlertDialog onCreateDialog(Bundle savedInstanceState)
			{
				LayoutInflater inflater  = getActivity().getLayoutInflater();
				View view = inflater.inflate(R.layout.cal_dialog, null, false);

				//外側のLinearLayoutの幅が崩れるのでParamで再設定
				LinearLayout LL = (LinearLayout)view.findViewById(R.id.dialogLLV);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
				LL.setLayoutParams(params);
				LL.setOrientation(LinearLayout.VERTICAL);

				//内容を入力
				((TextView)view.findViewById(R.id.eventTitle)).setText(event.getTitle());
				((TextView)view.findViewById(R.id.eventStartDay)).setText(event.getStartYMD());
				((TextView)view.findViewById(R.id.eventEndDay)).setText(event.getEndYMD());
				((TextView)view.findViewById(R.id.eventStartTime)).setText(event.getStartClock());
				((TextView)view.findViewById(R.id.eventEndTime)).setText(event.getEndClock());
				((TextView)view.findViewById(R.id.eventLoc)).setText(event.getLocation());
				((TextView)view.findViewById(R.id.eventContent)).setText(event.getTextContent());

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				// タイトルに予定の日付を設定
				//builder.setTitle(""); //TODO:いらないんだったら消そう by yuzu
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						//no ope
					}
				});
				builder.setView(view);
				return builder.create();
			}
		}
		Dialog dialog = new Dialog(e);
		dialog.setCancelable(true);
		dialog.show(getFragmentManager(), "span_dialog");
	}

	@Override
	public Loader<ArrayList<MyCalendarEvent>> onCreateLoader(int id, Bundle args)
	{
		try
		{
			Log.v(CALTAG, "mPosition : " + mPosition);
			CalendarQueryApi query = new CalendarQueryApi(this, "MONTH", mPosition);
			query.forceLoad();
			return query;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Log.e(CALTAG, e.getMessage());
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<MyCalendarEvent>> loaderInfo, ArrayList<MyCalendarEvent> eventList)
	{
		Log.d(CALTAG, "イベント取得サイズ : " + eventList.size());
		//CalendarServiceのオブジェクトをインスタンス化
		calService = new MyCalendarService(eventList);
		//何月か表示するための処理
		GregorianCalendar gCal = (GregorianCalendar)GregorianCalendar.getInstance();

		int today = gCal.get(GregorianCalendar.DAY_OF_MONTH);

		gCal.add(GregorianCalendar.MONTH, mPosition);
		gCal.set(GregorianCalendar.DAY_OF_MONTH, 1);

		TextView calMonthDisp = (TextView)findViewById(R.id.cal_month_date);
		calMonthDisp.setText(gCal.get(GregorianCalendar.YEAR) + "年" + (gCal.get(GregorianCalendar.MONTH) + 1) + "月");//何月か表示

		//その月が何日あるか
		int maxdate = gCal.getActualMaximum(GregorianCalendar.DATE);

		//日付表示用の変数
		int day = 1;
		for(int dispWeekCnt = 0; dispWeekCnt < DISP_WEEK && day <= maxdate; dispWeekCnt++)
		{
			LinearLayout week = new LinearLayout(this);//一週間表示用のレイアウト
			week.setOrientation(LinearLayout.HORIZONTAL);//追加される方向を指定
			for(int weekday = 0; weekday < WEEK_DAY; weekday++)
			{
				View child = getLayoutInflater().inflate(R.layout.cal_1day, null);
				if(day <= maxdate)//表示する日付を超えているかどうか
				{
					if(day == FIRST_DATE)//1日の時だけ処理
					{
						if(calDispField.getChildCount() > 0)//要素がある場合要素削除
						{
							calDispField.removeAllViews();
						}

						//カレンダーの最初の部分に空白を作る処理
						int weekOfDay = gCal.get(GregorianCalendar.DAY_OF_WEEK);
						for(int calWeek = GregorianCalendar.SUNDAY; calWeek < weekOfDay; calWeek++)
						{
							View emptyChild = getLayoutInflater().inflate(R.layout.cal_1day, null);
							emptyChild.setBackgroundResource(R.drawable.backgray_border);
							LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
							week.addView(emptyChild, emptyParams);
							weekday++;
						}
					}

					TextView dateView = (TextView)child.findViewById(id.caldate);
					dateView.setText(Integer.toString(day));

					int dispCnt = 0;
					int eventCnt = 0;
					for(MyCalendarEvent event : calService.getEventList())
					{
						//予定が範囲外なら次へ
						if(day < event.getStartDate() || day > event.getEndDate())	continue;
						eventCnt++;
						//Eventの数が表示数を超えたら
						if(dispCnt >= MAX_DISP_TITLE)	continue;

						TextView title = null;
						switch(dispCnt)
						{
							case(0):
								title = (TextView)child.findViewById(R.id.eventTitle1);	break;
							case(1):
								title = (TextView)child.findViewById(R.id.eventTitle2);	break;
							case(2):
								title = (TextView)child.findViewById(R.id.eventTitle3);	break;
						}
						dispCnt++;
						title.setText(event.getTitle());
					}//enf for その日の予定をすべて表示

					//縦幅が足りないので3件目を隠す
					TextView title = (TextView)child.findViewById(R.id.eventTitle3);
					title.setVisibility(View.GONE);

					if(eventCnt > MAX_DISP_TITLE)
					{
						TextView otherEvent = (TextView)child.findViewById(R.id.otherEvent);
						otherEvent.setText("他" + (eventCnt - MAX_DISP_TITLE) + "件");
					}
					// 日をクリックしたときの処理の設定
					calDayListener = new View.OnClickListener()
					{
						@Override public void onClick(View v)
						{
							// /内容のリスト
							// /titleList = new ArrayList<String>();
							// 押した日を取得
							ArrayAdapter<EventRow> adapter = new EventAdapter(context, R.layout.cal_contentrow);
							TextView daynum = (TextView)v.findViewById(R.id.caldate);
							int daynumber = Integer.parseInt((String)daynum.getText());

							// 押した日を表示
							TextView month = (TextView)findViewById(R.id.cal_month_date);
							TextView contentDay = (TextView)findViewById(R.id.contentday);
							contentDay.setText("▼ " + month.getText() + Integer.toString(daynumber) + "日");

							ArrayList<MyCalendarEvent> eventlist = calService.getDayEvent(daynumber);
							for(int i = 0;i < eventlist.size();i++ )
							{
								MyCalendarEvent event = eventlist.get(i);
								adapter.add(new EventRow(event.getTitle(), event.getEventId()));
							}
							// 日の内容のリストにデータ挿入
							Log.d("list", "リストにデータ挿入");
							ListView contentlist = (ListView)findViewById(R.id.contentlist);

							contentlist.setAdapter(adapter);
							contentlist.setOnItemClickListener(new ListClick());
						}
					};

					child.setOnClickListener(calDayListener);
				}
				else//表示する日付を超えたら
				{
					child.setBackgroundResource(R.drawable.backgray_border);
				}

				//週に予定を追加
				LinearLayout.LayoutParams paramChild = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
				week.addView(child, paramChild);

				if(mPosition == 0 && today == day)
				{
					child.performClick();
					child.setBackgroundResource(R.drawable.backdarkgray_border);
				}

				day++;//次の日へ
			}// end for １週間
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
			calDispField.addView(week, params);//月のViewに1週間を追加
		}//end for 1週間表示
		endConnectDialog();
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<MyCalendarEvent>> arg0)
	{
		//no ope
	}

	/**
	 * 通信中のダイアログを表示する
	 */
	private void startConnectDialog()
	{
		connectDialog = new ProgressDialog(this);
		connectDialog.setTitle("予定を取得中");
		connectDialog.setMessage("カレンダーの予定を\n" +
				"データベースから取得しています");
		connectDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		connectDialog.setCancelable(false);
		connectDialog.show();
	}

	/**
	 * 通信中のダイアログを閉じる
	 */
	private void endConnectDialog()
	{
		connectDialog.dismiss();
	}

	/**
	 * Loaderをスタートさせる前にネットワーク状況をチェックしDialogを表示する
	 */
	private void loaderStartConnection()
	{
		NetworkStatus ns = new NetworkStatus(this);
		if(ns.getNetworsStatus())
		{
			startConnectDialog();
			loadMan.restartLoader(2, null, this);
		}
		else
		{
			AlertDialog.Builder checkConnectDialog = new AlertDialog.Builder(this);
			checkConnectDialog.setTitle("通信エラー");
			checkConnectDialog.setMessage("ネットワークに接続できませんでした\n" +
					"電波状態を確認して再実行してください");
			checkConnectDialog.setPositiveButton("再実行", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					loaderStartConnection();
				}
			});
			checkConnectDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					Toast.makeText(getApplicationContext(), "予定が取得できませんでした", Toast.LENGTH_LONG).show();
				}
			});
			checkConnectDialog.setCancelable(false);
			checkConnectDialog.show();
		}
	}
}