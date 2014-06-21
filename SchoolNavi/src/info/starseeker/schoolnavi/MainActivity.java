/**
 * TOP画面のActivityの表示を管理するクラス
 * @author yuzu Tsukamoto tanpopo
 */
package info.starseeker.schoolnavi;

import info.starseeker.calendar.Calendar;
import info.starseeker.calendar.service.event.MyCalendarEvent;
import info.starseeker.customwidget.MultiDirectionSlidingDrawer;
import info.starseeker.customwidget.MultiDirectionSlidingDrawer.OnDrawerCloseListener;
import info.starseeker.customwidget.MultiDirectionSlidingDrawer.OnDrawerOpenListener;
import info.starseeker.datacontroller.PreferenceInfo;
import info.starseeker.network.NetworkStatus;
import info.starseeker.news.News;
import info.starseeker.notification.NotificationService;
import info.starseeker.otheractivity.MyActivity;
import info.starseeker.sqlite.TimetableDataController;
import info.starseeker.timetable.SubjectInfo;
import info.starseeker.timetable.Timetable;
import info.starseeker.timetableService.TimetableInfo;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends MyActivity implements OnClickListener, TimetableInterface, CalendarInterface, NewsInterface, OnDrawerCloseListener, OnDrawerOpenListener
{
	//データ取得が終わってるかどうか
	private Boolean calendarSession;
	private Boolean timeTableSession;
	private Boolean newsSession;

	private MultiDirectionSlidingDrawer mDrawer;

	private View allcover;

	//カレンダー取得用の定数
	private static final int MAX_DISP_TITLE = 3;

	//通信チェック用の定数
	private static final int CHECK = 0;
	private static final int CTN = 1;

	//Loader用の定数
	private static final int CLASS = 0;
	private static final int CALENDAR = 1;
	private static final int TIMETABLE = 2;
	private static final int NEWS = 3;

	// 通信中のダイアログ
	private ProgressDialog progressDialog;

	// プリファレンス
	private PreferenceInfo pi;

	private LoaderManager lm = getLoaderManager();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// setContentViewの前でタイトル非表示
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		mDrawer = (MultiDirectionSlidingDrawer)findViewById(R.id.drawer);
		mDrawer.setOnDrawerOpenListener(this);
		mDrawer.setOnDrawerCloseListener(this);

		// プリファレンスの準備
		pi = new PreferenceInfo(this);

		// 画面遷移のListenerのセット
		findViewById(R.id.scrollnews).setOnClickListener(this);

		// SideMenuの表示のListenerのセット
		findViewById(R.id.menuListBtn).setOnClickListener(this);

		//通信処理を終了を判断する変数に通信中を表すfalseを入れる
		calendarSession = false;
		timeTableSession = false;
		newsSession = false;

		findViewById(R.id.menuListBtn).setOnClickListener(this);

		// SideMenuでの画面遷移のListenerのセット
		findViewById(R.id.sideCal).setOnClickListener(this);
		findViewById(R.id.sideNews).setOnClickListener(this);
		findViewById(R.id.sideSCR).setOnClickListener(this);
		findViewById(R.id.sideSetting).setOnClickListener(this);
		findViewById(R.id.sideTT).setOnClickListener(this);

		JumpPage jump = new JumpPage();
		findViewById(R.id.tt1st).setOnClickListener(jump);
		findViewById(R.id.tt2nd).setOnClickListener(jump);
		findViewById(R.id.tt3rd).setOnClickListener(jump);
		findViewById(R.id.tt4th).setOnClickListener(jump);
		findViewById(R.id.tt5th).setOnClickListener(jump);
		findViewById(R.id.TimeTable).setOnClickListener(jump);
		findViewById(R.id.calendar).setOnClickListener(jump);

		allcover = findViewById(R.id.allcover);
		allcover.setOnClickListener(this);

		LinearLayout LL = (LinearLayout)findViewById(R.id.sideHome);
		LL.setBackgroundColor(Color.rgb(51,51,51));
		LL.setClickable(false);

		if(pi.checkCommit() == false) //初回起動の確認
		{
			new NotificationService(this).getRegidServer();
			showDialog();
		}
		else
		{
			timeTableSession = true;

			setClassName();

			showTimeTable();

			checkConnect(CTN);
		}
	}

	public class JumpPage implements OnClickListener
	{

		@Override
		public void onClick(View v) {
			int id = v.getId();
			Intent intent;
			if(mDrawer.isOpened())
				mDrawer.animateClose();
			if(id == R.id.calendar)
			{
				intent = new Intent(MainActivity.this, Calendar.class);
				startActivity(intent);
			}
			else//TimeTable
			{
				intent = new Intent(MainActivity.this, Timetable.class);
				startActivity(intent);
			}
		}

	}
	@Override
	public void onResume()
	{
		super.onResume();
		setClassName();
		showTimeTable();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
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

	@Override
	public void onClick(View v)
	{
		Intent intent = null;
		int id = v.getId();
		if(id == R.id.menuListBtn)
		{
			if( !mDrawer.isOpened() )
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
				case R.id.scrollnews :
					intent = new Intent(MainActivity.this, News.class);
					break;
				case R.id.sideCal :
				case R.id.sideNews :
				case R.id.sideSCR :
				case R.id.sideSetting :
				case R.id.sideTT :
					intent = new Intent(getIntent(v.getId(), this));
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
	/**
	 * ProgressDialogを止めるためのメソッド
	 */
	private void stopProgressDialog()
	{
		if(calendarSession == true && timeTableSession == true && newsSession == true)
		{
			progressDialog.dismiss();

			//アプリの初回起動をしないようにする
			pi.setCommit();
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
	public void calendarLoaderCallbacks(Loader<ArrayList<MyCalendarEvent>> loaderInfo, ArrayList<MyCalendarEvent> eventList)
	{
		if(eventList.isEmpty())
			Log.d(CALTAG, "EventListに要素がありません");

		Log.v(CALTAG, "EventListの要素数：" + eventList.size());

		ArrayList<LinearLayout> calLayout = new ArrayList<LinearLayout>();
		calLayout.add((LinearLayout)findViewById(R.id.event1));
		calLayout.add((LinearLayout)findViewById(R.id.event2));
		calLayout.add((LinearLayout)findViewById(R.id.event3));
		calLayout.add((LinearLayout)findViewById(R.id.event4));
		calLayout.add((LinearLayout)findViewById(R.id.event5));
		calLayout.add((LinearLayout)findViewById(R.id.event6));
		calLayout.add((LinearLayout)findViewById(R.id.event7));

		GregorianCalendar today = new GregorianCalendar(TimeZone.getTimeZone("Asia/Tokyo"));

		for(int i = 0; i < calLayout.size(); i++)
		{
			TextView dateView = (TextView)calLayout.get(i).findViewById(R.id.caldate);
			int date = today.get(GregorianCalendar.DATE);

			Resources res = getResources();
			//土曜日と日曜日の色設定
			if(today.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY)
			{
				dateView.setBackgroundColor(res.getColor(R.color.red_sunday));
			}
			else if(today.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY)
			{
				dateView.setBackgroundColor(res.getColor(R.color.brue_saturday));
			}
			else
			{
				dateView.setBackgroundColor(res.getColor(R.color.gray_normalday));
			}

			int dispCnt = 0;//表示するイベントの数
			int eventCnt = 0;//その日のイベントの総数
			for(MyCalendarEvent event : eventList)
			{
				//予定が範囲外なら次へ
				if(date < event.getStartDate() || date > event.getEndDate())	continue;

				eventCnt++;//イベントの件数をカウント

				//表示できる数を超えたら次へ
				if(dispCnt >= MAX_DISP_TITLE)	continue;

				TextView title = null;
				switch(dispCnt)
				{
					case(0):
						title = (TextView)calLayout.get(i).findViewById(R.id.eventTitle1);	break;
					case(1):
						title = (TextView)calLayout.get(i).findViewById(R.id.eventTitle2);	break;
					case(2):
						title = (TextView)calLayout.get(i).findViewById(R.id.eventTitle3);	break;
				}
				dispCnt++;
				title.setText(event.getTitle());
			}
			//表示件数を超えた分を「他○件」で表示
			if(eventCnt > MAX_DISP_TITLE)
			{
				TextView otherEvent = (TextView)calLayout.get(i).findViewById(R.id.otherEvent);
				otherEvent.setText("他" + (eventCnt - MAX_DISP_TITLE) + "件");
			}

			dateView.setText(Integer.toString(date));
			today.add(GregorianCalendar.DATE, 1);
		}

		//ダイアログストップ
		calendarSession = true;
		stopProgressDialog();
	}

	@Override
	public void timetableLoaderCallbacks(Loader<ArrayList<TimetableInfo>> tts, ArrayList<TimetableInfo> ttss)
	{
		TimetableDataController ttdc = new TimetableDataController(MainActivity.this);
		ttdc.setTimeTable(ttss);
		showTimeTable();
		timeTableSession = true;
		stopProgressDialog();
	}

	private void showDialog()
	{
		pi = new PreferenceInfo(MainActivity.this);
		class Dialog extends DialogFragment
		{
			int grade = 1;
			int classname = 0;
			int course = 0;
			String classname_s = "";
			String course_s = "";
			String[] nums_class = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
			String[] nums_course = {"SI","SN","KS","KN","KW","JT","JN","IS","IN"};

			@Override
			public AlertDialog onCreateDialog(Bundle savedInstanceState)
			{
				LayoutInflater inflater  = getActivity().getLayoutInflater();
				View view = inflater.inflate(R.layout.dialog, null, false);

				//学年選択のNumberPicker
				NumberPicker np = (NumberPicker)view.findViewById(R.id.numberPicker);
				np.setMaxValue(4);
				np.setMinValue(1);
				np.setOnValueChangedListener(new OnValueChangeListener()
				{
				    @Override
				    public void onValueChange(NumberPicker picker, int oldVal, int newVal)
				    {
				        //入力値が変更されるたびに入れたい処理
				    	grade = newVal;
				    }
				});

				//クラス選択のNumberPicker
				NumberPicker np2 = (NumberPicker)view.findViewById(R.id.numberPicker2);
				np2.setMaxValue(nums_class.length - 1);
				np2.setMinValue(0);
				np2.setDisplayedValues(nums_class);
				np2.setOnValueChangedListener(new OnValueChangeListener()
				{
				    @Override
				    public void onValueChange(NumberPicker picker, int oldVal, int newVal)
				    {
				        //入力値が変更されるたびに入れたい処理
				    	classname = newVal;
				    }
				});

				//コース選択のNumberPicker
				NumberPicker np3 = (NumberPicker)view.findViewById(R.id.numberPicker3);
				np3.setMaxValue(nums_course.length - 1);
				np3.setMinValue(0);
				np3.setDisplayedValues(nums_course);
				np3.setOnValueChangedListener(new OnValueChangeListener()
				{
				    @Override
				    public void onValueChange(NumberPicker picker, int oldVal, int newVal)
				    {
				        //入力値が変更されるたびに入れたい処理
				    	course = newVal;
				    }
				});

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				//builder.setTitle("Number Picker");
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) {
						classname_s = nums_class[classname];
						course_s = nums_course[course];
						AlertDialog.Builder checkDialog = new AlertDialog.Builder(getActivity());
						checkDialog.setTitle("確認");
						checkDialog.setMessage(grade + classname_s + course_s + "\nでよろしいですか?");
						checkDialog.setPositiveButton("はい", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								//プリファレンスの書き換え
								pi.setClass(grade, classname_s, course_s);

								setClassName();

								checkConnect(CHECK);
							}
						});
						checkDialog.setNegativeButton("いいえ", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								showDialog();
							}
						});
						checkDialog.setCancelable(false);
						checkDialog.show();
					}
				});
				builder.setView(view);

				return builder.create();
			}
		}
		Dialog dialog = new Dialog();
		dialog.setCancelable(false);
		dialog.show(getFragmentManager(), "span_dialog");
	}

	/**
	 * ヘッダーのクラス名をセットするメソッド
	 */
	private void setClassName()
	{
		StringBuilder strBuil = new StringBuilder();
		strBuil.append("Class:");
		strBuil.append(pi.getClassNumber());
		TextView className = (TextView)findViewById(R.id.class_name);
		className.setText(strBuil);
	}

	/**
	 * 時間割を表示する
	 */
	private void showTimeTable()
	{
		GregorianCalendar week = new GregorianCalendar(TimeZone.getTimeZone("Asia/Tokyo"));
		TimetableDataController ttdc = new TimetableDataController(MainActivity.this);
		ArrayList<LinearLayout> subject = new ArrayList<LinearLayout>();
		subject.add((LinearLayout)findViewById(R.id.tt1st));
		subject.add((LinearLayout)findViewById(R.id.tt2nd));
		subject.add((LinearLayout)findViewById(R.id.tt3rd));
		subject.add((LinearLayout)findViewById(R.id.tt4th));
		subject.add((LinearLayout)findViewById(R.id.tt5th));

		for(int i = 0; i < subject.size(); i++)
		{
			TextView timeView = (TextView)subject.get(i).findViewById(R.id.sjTime);
			StringBuilder timeStr = new StringBuilder();
			timeStr.append(i + 1);
			switch(i)
			{
				case(0)://1時間目
					timeStr.append("st");	break;
				case(1)://2時間目
					timeStr.append("nd");	break;
				case(2)://3時間目
					timeStr.append("rd");	break;
				case(3)://4時間目
				case(4)://5時間目
					timeStr.append("th");	break;
			}
			timeView.setText(timeStr.toString());//何限目かを表示
			int a = week.get(GregorianCalendar.DAY_OF_WEEK) -1;
			SubjectInfo si = ttdc.getTimeTable(i+1, a);
			if(si != null)
			{
				TextView subjectView = (TextView)subject.get(i).findViewById(R.id.sjName);
				subjectView.setText(si.getSubject());
				TextView roomView = (TextView)subject.get(i).findViewById(R.id.classRoom);
				roomView.setText(si.getRoom());
			}
			else
			{
				TextView subjectView = (TextView)subject.get(i).findViewById(R.id.sjName);
				subjectView.setText("授業なし");
				TextView roomView = (TextView)subject.get(i).findViewById(R.id.classRoom);
				roomView.setText(null);
			}
		}
	}

	/**
	 * 通信ステータスを取得確認し、通信可能ならローダーをスタートする
	 */
	public void checkConnect(int id)
	{
		NetworkStatus ns = new NetworkStatus(this);

		int grade = pi.getGrade();
		String classname = pi.getClassName();
		String course = pi.getCourse();

		//通信ができる状態の時の処理
		if(ns.getNetworsStatus())
		{
			if(id == CHECK)
			{
				progressDialog = new ProgressDialog(MainActivity.this);
				progressDialog.setMessage("クラスを確認しています。");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setCancelable(false);
				progressDialog.show();
				lm.restartLoader(CLASS, null, new CheckClassLoader(MainActivity.this, MainActivity.this, grade, classname, course));
			}
			else if (id == CTN)
			{
				progressDialog = new ProgressDialog(MainActivity.this);
				progressDialog.setMessage("データを取得しています");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setCancelable(false);
				progressDialog.show();

				if(calendarSession != true)
				{
					lm.initLoader(CALENDAR, null, new CalendarLoader(MainActivity.this, getApplicationContext()));;
				}

				if(timeTableSession != true)
				{
					lm.initLoader(TIMETABLE, null, new TimetableLoader(MainActivity.this,MainActivity.this, grade, classname, course));
				}
				if(newsSession != true)
				{
					lm.initLoader(NEWS, null, new NewsLoader(this, this));
				}
			}
		}
		else
		{
			errorConnectDialog(id);
		}
	}

	/**
	 * 通信ステータスがエラーの時に再度通信を促すダイアログを表示するメソッド
	 */
	public void errorConnectDialog(final int id)
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
				checkConnect(id);
			}
		});
		builder.setCancelable(true);
		builder.show();
	}

	@Override
	public void checkclassLoaderCallbacks(Loader<Boolean> lcheck, Boolean bcheck)
	{
		progressDialog.dismiss();
		if (bcheck)
		{
			checkConnect(CTN);
		}
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("クラスがありません。");
			builder.setMessage("クラスが見つかりませんでした。" +
								"\nもう一度設定してください。");
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					showDialog();
				}
			});
			builder.show();
		}
	}
	public void addNews()
	{
		lm.restartLoader(0, null, new NewsLoader(this, this));
	}
	@Override
	public void newsLoaderCallbacks(Loader<String> newsl, String newstitle)
	{
		TextView newstext = (TextView)findViewById(R.id.scrollnews);
		newstext.setText("最新のNews:" + newstitle);
		newsSession = true;
		stopProgressDialog();
	}
}